import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite

class JobDataProcessorTest extends AnyFunSuite with BeforeAndAfterAll {
  var spark: SparkSession = _

  override def beforeAll(): Unit = {
    spark = SparkSession.builder()
      .appName("JobDataProcessorTest")
      .master("local")
      .getOrCreate()
  }

  override def afterAll(): Unit = {
    spark.stop()
  }

  test("readCSV should return a non-empty DataFrame") {
    val inputFilePath = "/Users/sayeedahmed/IdeaProjects/JobScrapper/jobscapper/src/test/resources/sample.csv"
    val df = JobDataProcessor.readCSV(spark, inputFilePath)
    assert(!df.isEmpty)
  }

  test("cleanData should remove duplicates and null values") {
    val inputFilePath = "/Users/sayeedahmed/IdeaProjects/JobScrapper/jobscapper/src/test/resources/sample.csv"
    val rawData = JobDataProcessor.readCSV(spark, inputFilePath)
    val cleanedData = JobDataProcessor.cleanData(rawData)
    val duplicatedJobIds = cleanedData.groupBy("job_ID").count().filter(col("count") > 1)
    val nullJobTitles = cleanedData.filter(col("job_title").isNull)
    val nullJobLinks = cleanedData.filter(col("job_link").isNull)
    val nullCompanies = cleanedData.filter(col("company").isNull)

    assert(duplicatedJobIds.isEmpty)
    assert(nullJobTitles.isEmpty)
    assert(nullJobLinks.isEmpty)
    assert(nullCompanies.isEmpty)
  }

  test("removeExtraCharacters should remove unwanted characters from a column") {
    val schema = StructType(Seq(StructField("job_title", StringType, nullable = true)))
    val input = spark.createDataFrame(spark.sparkContext.parallelize(Seq(Row("Test$%*Title^&@"))), schema)
    val output = input.withColumn("job_title", JobDataProcessor.removeExtraCharacters(col("job_title")))
    val expected = spark.createDataFrame(spark.sparkContext.parallelize(Seq(Row("testtitle"))), schema)

    assert(output.collect() sameElements expected.collect())
  }


  // A test for the database connection
  val jdbcUrl = "jdbc:mysql://127.0.0.1:3306/jobs_db"
  val dbTable = "jobs_test"
  val dbUser = "root"
  val dbPassword = "Knock!23"
  val connectionProperties: Map[String, String] = Map("user" -> dbUser, "password" -> dbPassword)

  test("readFromDatabase should establish a connection and return a non-empty DataFrame") {
    val df = JobDataProcessor.readFromDatabase(spark, jdbcUrl, dbTable, connectionProperties)
    assert(!df.isEmpty)
  }

}
