import org.openqa.selenium.{By, WebElement}
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.scalatest._
import io.github.bonigarcia.wdm.WebDriverManager

class MainTest extends flatspec.AnyFlatSpec with matchers.should.Matchers {
  //System.setProperty("webdriver.chrome.driver", "<path-to-your-chrome-driver>")
  val options = new ChromeOptions()
  options.addArguments("headless")

  WebDriverManager.chromedriver().setup()


  "JobScraper" should "display job postings for a matching category" in {
    val driver = new ChromeDriver()
    driver.get("http://localhost:8080")

    val fileInput = driver.findElement(By.cssSelector("input[type='file']"))
    fileInput.sendKeys("C:/Users/cheth/Downloads/Resumes/Resumes/resumesDE.pdf")

    val categorySelect = driver.findElement(By.tagName("select"))
    val options = categorySelect.findElements(By.tagName("option"))

    // Select "data scientist" option
    options.stream().filter((option: WebElement) => option.getText == "software engineering").findFirst.get.click()

    val submitButton = driver.findElement(By.tagName("button"))
    submitButton.click()

    // Wait for the table to appear
    Thread.sleep(10000)

    val tableRows = driver.findElements(By.cssSelector("table tr"))

    // Check if there are any rows in the table
    tableRows.size() should be > 0

    driver.quit()
  }
}

