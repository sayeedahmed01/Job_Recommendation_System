import io.github.bonigarcia.wdm.WebDriverManager
import org.scalatest.funsuite.AnyFunSuite
import org.jsoup.Jsoup
import org.openqa.selenium.chrome.ChromeDriver

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
}
