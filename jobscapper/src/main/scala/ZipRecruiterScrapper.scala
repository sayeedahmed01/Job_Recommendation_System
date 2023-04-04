import org.openqa.selenium.By
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}
import java.io.{File, FileWriter}
import java.time.Duration
import org.jsoup.Jsoup
import java.util.Scanner
import scala.collection.mutable.ListBuffer
import io.github.bonigarcia.wdm.WebDriverManager

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
    passwordField.submit
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
    println(jobList)
    for (start <- startList) {
      driver.switchTo().window(s"tab$start")
      val wait2 = new WebDriverWait(driver, Duration.ofSeconds(30).toMillis())
      val results = wait2.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("job_results")))
      println(results)

/*      results.forEach { result =>
        val job = Map(
          "Job Title" -> result.findElement(By.cssSelector("a.job_link")).getText(),
          "Company" -> result.findElement(By.cssSelector("a.t_org_link")).getText(),
          "Location" -> result.findElement(By.cssSelector("div.job_org")).getText(),
          "Job Link" -> result.findElement(By.cssSelector("a.job_link")).getAttribute("href"),
          "Salary" -> {
            val salarySnippet = result.findElement(By.cssSelector("span.salary_estimate"))
            if (salarySnippet != null) salarySnippet.getText()
            else ""
          }
        )
        jobList += job
      }*/


      results.forEach { result =>
        val parsedResult = Jsoup.parse(result.getAttribute("outerHTML"))
        val job = Map(
          "Job Title" -> parsedResult.select("a.job_link").text(),
          "Company" -> parsedResult.select("a.t_org_link").text(),
          "Location" -> parsedResult.select("div.job_org").text(),
          "Job Link" -> parsedResult.select("a.job_link").attr("href"),
          "Salary" -> {
            val salarySnippet = parsedResult.select("span.salary_estimate").first()
            if (salarySnippet != null) salarySnippet.text()
            else ""
          }
        )
        jobList += job
      }
      driver.close()
      println(jobList)
    }

    // Write Results to CSV File
    val csvFile = new File(s"$query$location.csv")
    val writer = new FileWriter(csvFile)
    writer.write("Job Title, Company, Location, Job Link, Salary\n")
    for (job <- jobList) {
      writer.write(s"${job("Job Title")},${job("Company")},${job("Location")},${job("Job Link")},${job("Salary")}\n")
    }
    writer.close()

    // Webdriver Cleanup
    driver.quit()

  }
}