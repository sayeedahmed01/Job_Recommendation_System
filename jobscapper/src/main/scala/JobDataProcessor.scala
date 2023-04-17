import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}

object JobDataProcessor {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("JobDataProcessor")
      .master("local")
      .getOrCreate()

    val inputFilePath = "/Users/sayeedahmed/IdeaProjects/JobScrapper/Data Engineer+all_locations.csv"

    val rawData = readCSV(spark, inputFilePath)

    val cleanedData = cleanData(rawData)

    val jdbcUrl = "jdbc:mysql://127.0.0.1:3306/jobs_db"
    val dbTable = "jobs_all"
    val dbUser = "root"
    val dbPassword = "Knock!23"

    val connectionProperties = new java.util.Properties()
    connectionProperties.put("user", dbUser)
    connectionProperties.put("password", dbPassword)

    // Read existing data from the database
    val existingData = readFromDatabase(spark, jdbcUrl, dbTable, connectionProperties)

    // Perform a left anti-join to filter out rows that already exist in the database
    val newData = cleanedData.join(existingData, cleanedData("Job_ID") === existingData("Job_ID"), "leftanti")
    newData.show(5)
    // Write only the new data to the database
    writeToDatabase(newData, "append", jdbcUrl, dbTable, connectionProperties)

    // Read data from the database
    val dataFromDatabase = readFromDatabase(spark, jdbcUrl, dbTable, connectionProperties)

    // Print first 5 rows from the database
    dataFromDatabase.show(5)
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

  def writeToDatabase(df: DataFrame, saveMode: String, jdbcUrl: String, dbTable: String, connectionProperties: java.util.Properties): Unit = {
    df.write
      .mode(saveMode)
      .jdbc(jdbcUrl, dbTable, connectionProperties)
  }

  def readFromDatabase(spark: SparkSession, jdbcUrl: String, dbTable: String, connectionProperties: java.util.Properties): DataFrame = {
    spark.read
      .jdbc(jdbcUrl, dbTable, connectionProperties)
  }
}
