import io.github.bonigarcia.wdm.WebDriverManager
import org.jsoup.Jsoup
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.support.ui.WebDriverWait
import org.scalatest.funsuite.AnyFunSuite

class IndeedScraperMultiTest extends AnyFunSuite {

  // A test for parseSalary function
  test("parseSalary should return the correct salary from the parsed result") {
    val html =
      """
        |<div>
        |  <div class="metadata salary-snippet-container">
        |    $90,000 a year
        |  </div>
        |</div>
      """.stripMargin

    val parsedResult = Jsoup.parse(html)
    val salary = IndeedScraperMulti.parseSalary(parsedResult)

    assert(salary == "$90,000 a year")
  }

  // A test for parseDatePosted function
  test("parseDatePosted should return the correct date posted from the parsed result") {
    val html =
      """
        |<div>
        |  <span class="date">
        |    30+ days ago
        |  </span>
        |</div>
      """.stripMargin

    val parsedResult = Jsoup.parse(html)
    val datePosted = IndeedScraperMulti.parseDatePosted(parsedResult)

    assert(datePosted == "30+ days ago")
  }

  // A test for scrapeIndeed function
  test("scrapeIndeed should return a non-empty list of jobs") {
    WebDriverManager.chromedriver().setup()
    val driver = new ChromeDriver()
    val query = "software engineer"
    val location = "Austin"
    val numPages = 1

    val jobs = IndeedScraperMulti.scrapeIndeed(driver, query, location, numPages)
    driver.quit()

    assert(jobs.nonEmpty)
  }

  // A test for getJobDescription function
  test("getJobDescription should return the correct job description") {
    WebDriverManager.chromedriver().setup()
    val driver = new ChromeDriver()
    val wait = new WebDriverWait(driver, 30)

    val jobLink = "https://www.indeed.com/viewjob?jk=01eac57395f52da2&from=serp&vjs=3"
    val description = IndeedScraperMulti.getJobDescription(driver, wait, jobLink)

    driver.quit()
    assert(description.isDefined)
  }
}
