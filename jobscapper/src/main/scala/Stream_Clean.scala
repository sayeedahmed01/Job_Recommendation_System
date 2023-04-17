import org.apache.spark.streaming._
import org.apache.spark.streaming.StreamingContext._
//import org.apache.spark.streaming.kafka._
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql._
import java.net._
import java.io._



object Steam_Clean extends App{

  val conf = new SparkConf()
    .setAppName("Spark Streaming CSV Example")
    .setMaster("local[*]") // Replace with your cluster URL if you're running on a cluster
    .set("spark.streaming.stopGracefullyOnShutdown", "true") // Gracefully stop Spark Streaming on shutdown
    //.set("spark.driver.memory", "2g") // Set driver memory to 2 GB
    //.set("spark.executor.memory", "2g") // Set executor memory to 2 GB
    .set("spark.ui.showConsoleProgress", "true") // Disable console progress bar

  val ssc = new StreamingContext(conf, Seconds(1))

  //val spark = SparkSession.builder().getOrCreate()
  //import spark.implicits._

  val inputDirectory = "Finance+all_locations1.csv"
  val csvDStream = ssc.textFileStream(inputDirectory)


  val schema = StructType(Seq(
    StructField("job_id", StringType, nullable = false),
    StructField("job_title", IntegerType, nullable = true),
    StructField("company", StringType, nullable = true) ,
    StructField("locaion", StringType, nullable = false),
    StructField("job_link", IntegerType, nullable = true),
    StructField("salary", StringType, nullable = true),
    StructField("job_description", StringType, nullable = true),
    StructField("date_posted", StringType, nullable = true)
  ))

  val rowDStream = csvDStream.flatMap(_.split("\n"))
    .map(_.split('|'))
    .map(x => Row(x(0), x(1).toInt, x(2), x(3), x(4).toInt, x(5), x(6), x(7)))



    rowDStream.foreachRDD(rdd => {
    if (!rdd.isEmpty()) {
      val spark = SparkSession.builder().config(rdd.sparkContext.getConf).getOrCreate()
      import spark.implicits._
      val df = spark.createDataFrame(rdd, schema)
      df.show()
      df.write.mode("append").csv("output") // write the DataFrame to a file
    }
  })

  ssc.start()
  ssc.awaitTermination()
}
