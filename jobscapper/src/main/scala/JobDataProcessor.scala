  import org.apache.spark.sql.functions._
  import org.apache.spark.sql.{DataFrame, SparkSession}

  object JobDataProcessor {
    def main(args: Array[String]): Unit = {
      val spark = SparkSession.builder()
        .appName("JobDataProcessor")
        .master("local")
        .getOrCreate()

      val inputFilePath = "/Users/sayeedahmed/IdeaProjects/JobScrapper/all_locations.csv"

      val rawData = readCSV(spark, inputFilePath)
      val cleanedData = cleanData(rawData)

      val jdbcUrl = "jdbc:mysql://127.0.0.1:3306/jobs_db"
      val dbTable = "jobs_all"
      val dbUser = "root"
      val dbPassword = "Knock!23"

      val connectionProperties = Map("user" -> dbUser, "password" -> dbPassword)

      val existingData = readFromDatabase(spark, jdbcUrl, dbTable, connectionProperties)
      val newData = filterNewData(cleanedData, existingData)
      writeToDatabase(newData, "append", jdbcUrl, dbTable, connectionProperties)

      val dataFromDatabase = readFromDatabase(spark, jdbcUrl, dbTable, connectionProperties)
      val newDataCount = newData.count()
      printInsertedCount(newDataCount, dataFromDatabase, existingData)

      spark.stop()
    }

    def readCSV(spark: SparkSession, filePath: String): DataFrame = {
      spark.read
        .option("header", "true")
        .option("inferSchema", "true")
        .option("sep", "|")
        .csv(filePath)
    }

    def cleanData(df: DataFrame): DataFrame = {
      df.dropDuplicates("Job_ID")
        .filter(col("Job_Title").isNotNull && col("Job_Link").isNotNull && col("Company").isNotNull)
        .na.fill("Unknown", Seq("Location"))
        .na.fill("Not provided", Seq("Salary", "Job_Description"))
        .withColumn("Job_Title", removeExtraCharacters(col("Job_Title")))
        .withColumn("Company", removeExtraCharacters(col("Company")))
        .withColumn("Location", removeExtraCharacters(col("Location")))
        .withColumn("Job_Description", removeExtraCharacters(col("Job_Description")))
        .withColumn("Salary", regexp_replace(col("Salary"), "\\$", ""))
        .withColumn("Date_Posted", when(col("Date_Posted").isNull, current_date()).otherwise(col("Date_Posted")))
    }

    def removeExtraCharacters(column: org.apache.spark.sql.Column): org.apache.spark.sql.Column = {
      regexp_replace(lower(column), "[^a-z0-9\\s\\.,_-]", "")
    }

    def readFromDatabase(spark: SparkSession, jdbcUrl: String, dbTable: String, connectionProperties: Map[String, String]): DataFrame = {
      val props = new java.util.Properties
      connectionProperties.foreach { case (key, value) => props.setProperty(key, value) }
      spark.read
        .jdbc(jdbcUrl, dbTable, props)
    }

    def filterNewData(cleanedData: DataFrame, existingData: DataFrame): DataFrame = {
      cleanedData.join(existingData, cleanedData("Job_ID") === existingData("Job_ID"), "leftanti")
    }

    def writeToDatabase(df: DataFrame, saveMode: String, jdbcUrl: String, dbTable: String, connectionProperties: Map[String, String]): Unit = {
      val props = new java.util.Properties
      connectionProperties.foreach { case (key, value) => props.setProperty(key, value) }
      df.write
        .mode(saveMode)
        .jdbc(jdbcUrl, dbTable, props)
    }

    def printFirstRows(df: DataFrame, numRows: Int): Unit = {
      df.show(numRows)
    }

    def printInsertedCount(newCount: Long, newData: DataFrame, existingData: DataFrame): Unit = {
      val totalCount = existingData.union(newData).count()
      println(s"Inserted $newCount new records. Total record count is now $totalCount")
    }

  }

