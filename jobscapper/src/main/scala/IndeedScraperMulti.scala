import io.github.bonigarcia.wdm.WebDriverManager
import org.jsoup.Jsoup
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}
import org.openqa.selenium.{By, TimeoutException, WebDriver}

import java.io.{File, FileWriter}
import java.time.Duration
import java.util.Scanner
import java.util.concurrent.Executors
import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

case class Job(id: String, title: String, company: String, location: String, link: String, salary: String, description: String, datePosted: String)

object IndeedScraperMulti {
  def main(args: Array[String]): Unit = {
    val scanner = new Scanner(System.in)
    print("Enter job query: ")
    val query = scanner.nextLine()
    print("Number of pages: ")
    val numPages = scanner.nextInt()
    scanner.close()
    val locations = List("Austin")//, "Boston")//, "Seattle", "Chicago", "Austin", "San Jose","Boulder", "Washington")

    val csvFile = new File(s"$query+all_locations.csv")
    val writer = new FileWriter(csvFile, true) // Open file in append mode
    writer.write("Job ID|Job Title|Company|Location|Job Link|Salary|Job Description|Date Posted\n")

    val options = new ChromeOptions()
    options.addArguments("headless")

    // Setup WebDriver only once
    WebDriverManager.chromedriver().setup()

    // Define a custom ExecutionContext for parallelism
    val executor = Executors.newFixedThreadPool(locations.size)
    implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(executor)

    // Use Future.sequence to wait for all Futures to complete
    val scrapingFutures = locations.map { location =>
      Future {
        val driver = new ChromeDriver(options)
        val jobs = scrapeIndeed(driver, query, location, numPages)
        driver.quit()
        (location, jobs)
      }
    }

    val allJobs = Await.result(Future.sequence(scrapingFutures), 60.minutes)

    // Write the jobs to the CSV file
    allJobs.foreach { case (_, jobs) => writeJobsToCSV(writer, jobs) }

    // Shutdown the thread pool executor
    executor.shutdown()

    writer.close()
  }

  def scrapeIndeed(driver: WebDriver, query: String, location: String, numPages: Int): List[Job] = {
    val searchResultURLs = (0 until numPages * 10 by 10).map(start => s"https://www.indeed.com/jobs?q=$query&l=$location&sort=date&start=$start").toList
    val wait = new WebDriverWait(driver, Duration.ofSeconds(60).toMillis)

    searchResultURLs.flatMap { url =>
      driver.get(url)
      val results = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("resultContent")))
      results.asScala.map { result =>
        val parsedResult = Jsoup.parse(result.getAttribute("outerHTML"))
        val jobLink = parsedResult.select("a").attr("href")
        val description = getJobDescription(driver, wait, jobLink)
        Job(
          id = parsedResult.select("a").attr("id"),
          title = parsedResult.select("a").text(),
          company = parsedResult.select("span.companyName").text(),
          location = parsedResult.select("div.companyLocation").text(),
          link = jobLink,
          salary = parseSalary(parsedResult),
          description = description.getOrElse(""),
          datePosted = parseDatePosted(parsedResult)
        )
      }.toList
    }
  }

  def getJobDescription(driver: WebDriver, wait: WebDriverWait, jobLink: String): Option[String] = {
    Try {
      // Open job link in a new tab
      val script = s"window.open('$jobLink', '_blank');"
      driver.asInstanceOf[ChromeDriver].executeScript(script)

      // Wait for 2 seconds
      Thread.sleep(2000)

      // Switch to the new tab
      val newTabHandle = driver.getWindowHandles.asScala.toList.last
      driver.switchTo().window(newTabHandle)

      // Extract job description
      val result = Try(wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(@class, 'jobsearch-JobComponent-description') or contains(@class, 'jobsearch-jobDescriptionText')]")))).recover {
        case _: TimeoutException => null
      }.get

      val description = if (result != null) {
        val parsedResult = Jsoup.parse(result.getAttribute("outerHTML"))
        parsedResult.select("div.jobsearch-JobComponent-description, div.jobsearch-jobDescriptionText").text()
      } else {
        throw new Exception("Timeout while waiting for job description")
      }

      // Wait for 2 seconds
      Thread.sleep(2000)

      // Close the new tab and switch back to the main window
      driver.close()
      driver.switchTo().window(driver.getWindowHandles.asScala.toList.head)

      description
    } match {
      case Success(description) => Some(description)
      case Failure(_) => None
    }
  }


  def parseSalary(parsedResult: org.jsoup.nodes.Document): String = {
    val salarySnippet = parsedResult.select("div.metadata.salary-snippet-container")
    if (salarySnippet != null) salarySnippet.text
    else {
      val estimatedSalary = parsedResult.select("div.metadata.estimated-salary-container")
      if (estimatedSalary != null) estimatedSalary.text()
      else ""
    }
  }

  def parseDatePosted(parsedResult: org.jsoup.nodes.Document): String = {
    val datePostedElement = parsedResult.select("span.date").first()
    if (datePostedElement != null) datePostedElement.text() else "NA"
  }

  def writeJobsToCSV(writer: FileWriter, jobs: List[Job]): Unit = {
    jobs.foreach { job =>
      writer.write(s"${job.id}|${job.title}|${job.company}|${job.location}|${job.link}|${job.salary}|${job.description}|${job.datePosted}\n")
    }
  }
}