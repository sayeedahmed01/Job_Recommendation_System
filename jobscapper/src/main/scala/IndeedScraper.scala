import io.github.bonigarcia.wdm.WebDriverManager
import org.jsoup.Jsoup
import org.openqa.selenium.By
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}

import java.io.{File, FileWriter}
import java.time.Duration
import java.util.Scanner
import scala.collection.mutable.ListBuffer

object IndeedScraper {
  def main(args: Array[String]): Unit = {
    // User Input
    val scanner = new Scanner(System.in)
    print("Enter job query: ")
    val query = scanner.nextLine()
    print("Enter job location: ")
    val location = scanner.nextLine()
    print("Number of pages: ")
    val numPages = scanner.nextInt()
    scanner.close()

    val startList = List.range(0, numPages * 10, 10)

    // Use WebDriverManager to install the latest version of ChromeDriver
    WebDriverManager.chromedriver().setup()
    // Create a new ChromeDriver instance
//    val driver = new ChromeDriver()
    val options = new ChromeOptions()
    options.addArguments("--headless=new")
    val driver = new ChromeDriver(options)

    // Open Search Result Pages
    for (start <- startList) {
      val url = s"https://www.indeed.com/jobs?q=$query&l=$location&sort=date&start=$start"
      driver.executeScript(s"window.open('$url', 'tab$start');")
      Thread.sleep(10000)
    }

    // Extract Job Information
    val jobList = new ListBuffer[Map[String, String]]
    val jobList1 = new ListBuffer[Map[String, String]]
    for (start <- startList) {
      driver.switchTo().window(s"tab$start")
      val wait = new WebDriverWait(driver, Duration.ofSeconds(30).toMillis)
      val result = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("resultContent")))
      result.forEach { result =>
        val parsedResult = Jsoup.parse(result.getAttribute("outerHTML"))
        val job = Map(
          "Job ID" -> parsedResult.select("a").attr("id"),
          "Job Title" -> parsedResult.select("a").text(),
          "Company" -> parsedResult.select("span.companyName").text(),
          "Location" -> parsedResult.select("div.companyLocation").text(),
          "Job Link" -> parsedResult.select("a").attr("href"),
          "Date Posted" -> {
            if (parsedResult.select("span.date").first() != null) {
              parsedResult.select("span.date").first().text()
            } else {
              "NA"
            }
          },
          "Salary" -> {
            val salarySnippet = parsedResult.select("div.metadata.salary-snippet-container")
            if (salarySnippet != null) salarySnippet.text()
            else {
              val estimatedSalary = parsedResult.select("div.metadata.estimated-salary-container")
              if (estimatedSalary != null) estimatedSalary.text()
              else "NA"
            }
          }
        )
        jobList += job
      }
    }
    for (job <- jobList) {
      "Job Description" -> {
        val jobLink = job("Job Link")
        driver.executeScript(s"window.open('$jobLink');")
        val wait = new WebDriverWait(driver, Duration.ofSeconds(30).toMillis)
        val result = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("jobsearch-JobComponent-description")))
        result.forEach { result =>
          val parsedResult = Jsoup.parse(result.getAttribute("outerHTML"))
          val jobDescription = parsedResult.select("div.jobsearch-JobComponent-description").text()

          jobList1 += job + ("Job Description" -> jobDescription)
        }
      }
    }
    driver.close()



    // Write Results to CSV File
    val csvFile = new File(s"$query$location.csv")
    val writer = new FileWriter(csvFile)
    writer.write("Job ID|Job Title|Company|Location|Job Link|Salary|Job Description|Date Posted\n")
    for (job <- jobList1) {
      writer.write(s"${job("Job ID")}|${job("Job Title")}|${job("Company")}|${job("Location")}|${job("Job Link")}|${job("Salary")}|${job("Job Description")}|${job("Date Posted")}\n")
    }
    writer.close()

    // Webdriver Cleanup
    driver.quit()

  }
}