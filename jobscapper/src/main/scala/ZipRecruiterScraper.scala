import io.github.bonigarcia.wdm.WebDriverManager
import org.jsoup.Jsoup
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}

import java.io.{File, FileWriter}
import java.time.Duration
import java.util.Scanner
import scala.collection.mutable.ListBuffer

object ZipRecruiterScraper {
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

    val startList = List.range(1, numPages + 1)

    // Webdriver Initialization
    WebDriverManager.chromedriver().setup()
    // Create a new ChromeDriver instance
    val driver = new ChromeDriver()

    //Logging in
    driver.get("https://www.ziprecruiter.com/login")
    val emailField = driver.findElement(By.id("email"))
    emailField.sendKeys("BDproject.00@gmail.com")

    val passwordField = driver.findElement(By.id("password"))
    passwordField.sendKeys("BDproject123")
    passwordField.submit()
    Thread.sleep(1000)
    //val wait1 = new WebDriverWait(driver, Duration.ofSeconds(30).toMillis())

    // Open Search Result Pages
    for (start <- startList) {
      val url = s"https://www.ziprecruiter.com/candidate/search?search=$query&location=$location&pagenumber=$start"
      driver.executeScript(s"window.open('$url', 'tab$start');")
      Thread.sleep(1000)
    }

    // Extract Job Information
    val jobList = new ListBuffer[Map[String, String]]
    print("Extracting Job Information \n")
    for (start <- startList) {
      driver.switchTo().window(s"tab$start")
      val wait2 = new WebDriverWait(driver, Duration.ofSeconds(30).toMillis)
      val results = wait2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("job_content")))

      results.forEach {
        result => val parsedResult = Jsoup.parse(result.getAttribute("outerHTML"))
        val job = Map(
          "Job Title" -> parsedResult.select("a.job_link").text(),
          "Company" -> parsedResult.select("div.company_name_row").text(),
//          "Company" -> parsedResult.select("a.t_org_link").text(),
//          "Location" -> parsedResult.select("div.company_location_row"),
////          "Location" -> s"$location",
//          "Job Link" -> parsedResult.select("a.job_link").attr("href"),
//          "Salary" -> parsedResult.select("p.salarySnippet").text()
        )
        jobList += job
      }
    }

    // Write Results to CSV File
    val csvFile = new File(s"$query$location.csv")
    val writer = new FileWriter(csvFile)
    writer.write("Job Title, Company\n") /*, Location, Job Link, Salary*/
    for (job <- jobList) {
      writer.write(s"${job("Job Title")},${job("Company")}\n")/*,${job("Company")},${job("Location")},${job("Job Link")},${job("Salary")*/
    }
    writer.close()

    // Webdriver Cleanup
    driver.quit()

  }
}

