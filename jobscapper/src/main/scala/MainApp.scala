
object MainApp {
  def main(args: Array[String]): Unit = {
    val queries = List("software engineering", "human resources", "data engineer", "data scientist")
    val numPages = 5
    val locations = List("Boston", "Seattle", "Chicago", "Austin", "San Jose", "Boulder", "Washington")
    val interval =  720000// Time interval

    while (true) {
      for (query <- queries) {
        println(s"Scraping job data for query: $query")
        IndeedScraperMulti.processQuery(query, numPages, locations)
        val inputFilePath = "src/main/resources/all_locations.csv"
        JobDataProcessor.processData(inputFilePath)
        Thread.sleep(3000)
      }
      println(s"Waiting for ${interval / 60000} minutes before starting the next iteration.")
      Thread.sleep(interval)
    }
  }
}
