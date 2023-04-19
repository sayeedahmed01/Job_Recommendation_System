  import org.apache.spark.sql.functions._
  import org.apache.spark.sql.{DataFrame, SparkSession}

  object JobDataProcessor {
    def processData(inputFilePath: String): Unit = {
      val spark = SparkSession.builder()
        .appName("JobDataProcessor")
        .master("local")
        .getOrCreate()

      val rawData = readCSV(spark, inputFilePath)
      val cleanedData = cleanData(rawData)

      val jdbcUrl = "jdbc:mysql://127.0.0.1:3306/jobs_db"
      val dbTable = "jobs_all"
      val dbUser = "root"
      val dbPassword = "Knock!23"
      val connectionProperties = Map("user" -> dbUser, "password" -> dbPassword)
      val maxLengths = Map(
        "job_ID" -> 100,
        "job_title" -> 255, // tinytext has a maximum length of 255 characters
        "company" -> 255, // tinytext has a maximum length of 255 characters
        "location" -> 200,
        "job_link" -> 16777215, // mediumtext has a maximum length of 16,777,215 characters
        "salary" -> 45,
        "job_description" -> 4294967295L, // longtext has a maximum length of 4,294,967,295 characters
        "category" -> 45,
        "timeScraped" -> 50
      )

      val existingData = readFromDatabase(spark, jdbcUrl, dbTable, connectionProperties)
      val newData = filterNewData(cleanedData, existingData, maxLengths) // Pass the maxLengths to the function
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
      df.dropDuplicates("job_ID")
        .filter(col("job_title").isNotNull && col("job_link").isNotNull && col("company").isNotNull)
        .na.fill("Unknown", Seq("location"))
        .na.fill("Not provided", Seq("salary", "job_description"))
        .withColumn("job_title", removeExtraCharacters(col("job_title")))
        .withColumn("company", removeExtraCharacters(col("company")))
        .withColumn("location", removeExtraCharacters(col("location")))
        .withColumn("job_description", removeExtraCharacters(col("job_description")))
        .withColumn("salary", regexp_replace(col("salary"), "\\$", ""))
        .withColumn("timeScraped", when(col("timeScraped").isNull, current_timestamp()).otherwise(col("timeScraped")))
        .filter(col("timeScraped").cast("timestamp").isNotNull)
        .filter(col("job_link").startsWith("/")) // Add this line to filter out rows where the job_link column doesn't start with '/'
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

    def filterNewData(cleanedData: DataFrame, existingData: DataFrame, maxLengths: Map[String, AnyVal]): DataFrame = {
      cleanedData
        .filter(length(col("job_ID")) <= maxLengths("job_ID"))
        .filter(length(col("job_title")) <= maxLengths("job_title"))
        .filter(length(col("company")) <= maxLengths("company"))
        .filter(length(col("location")) <= maxLengths("location"))
        .filter(length(col("job_link")) <= maxLengths("job_link"))
        .filter(length(col("salary")) <= maxLengths("salary"))
        .filter(length(col("job_description")) <= maxLengths("job_description"))
        .filter(length(col("category")) <= maxLengths("category")) // Add this line for the category column
        .filter(length(col("timeScraped")) <= maxLengths("timeScraped"))
        .join(existingData, cleanedData("job_ID") === existingData("job_ID"), "leftanti")
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

