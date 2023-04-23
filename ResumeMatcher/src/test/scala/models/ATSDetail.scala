import com.resume.ats.check.models.RMDetail
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RMDetailSpec extends AnyFlatSpec with Matchers {
  "ATSDetail" should "set and get totalKeywords" in {
    val detail = new RMDetail
    detail.setTotalKeywords(List("Java", "Scala", "Python"))
    detail.getTotalKeywords shouldBe List("Java", "Scala", "Python")
  }

  it should "set and get unMatchedKeywords" in {
    val detail = new RMDetail
    detail.setUnMatchedKeywords(List("Java", "Ruby"))
    detail.getUnMatchedKeywords shouldBe List("Java", "Ruby")
  }

  it should "set and get matchPercentage" in {
    val detail = new RMDetail
    detail.setMatchPercentage("70.5")
    detail.getMatchPercentage shouldBe "70.5"
  }

  it should "generate a string representation" in {
    val detail = new RMDetail
    detail.setTotalKeywords(List("Java", "Scala", "Python"))
    detail.setUnMatchedKeywords(List("Java", "Ruby"))
    detail.setMatchPercentage("70.5")
    detail.toString shouldBe "70.5"
  }

  it should "set and get matchPercentage as double" in {
    val detail = new RMDetail
    detail.setMatchPercentage("70.5")
    detail.getMatchPercentage.toDouble shouldBe 70.5
  }

  it should "throw an exception for invalid matchPercentage string" in {
    val detail = new RMDetail
    intercept[NumberFormatException] {
      detail.setMatchPercentage("not a double")
    }
  }
}
