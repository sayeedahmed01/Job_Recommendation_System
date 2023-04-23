import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.mockito.Mockito._
import org.springframework.web.multipart.MultipartFile
import com.resume.ats.check.service.{KeywordExtractorService, ScanPdfService, RMCheckerService}
import com.resume.ats.check.models.RMDetail

class RMCheckerServiceTest extends AnyFunSuite with Matchers {

  val mockKeywordExtractorService: KeywordExtractorService = mock(classOf[KeywordExtractorService])
  val mockScanPdfService: ScanPdfService = mock(classOf[ScanPdfService])
  val atsCheckerService = new RMCheckerService(mockKeywordExtractorService, mockScanPdfService)

  test("generateAtsDetails should return correct ATSDetail") {
    val resumeFile: MultipartFile = mock(classOf[MultipartFile])
    val jobDescription = "software developer, java, scala,play framework"

    when(mockScanPdfService.scanPdfFromFile(resumeFile)).thenReturn("software developer\", \"java\", \"scala\", \"play framework")
    when(mockKeywordExtractorService.extractKeywords(jobDescription)).thenReturn(List("software developer", "java", "scala", "play framework"))

    val result = atsCheckerService.generateRMDetails(resumeFile, jobDescription)

    result.getMatchPercentage.toDouble shouldBe 100.0

    verify(mockScanPdfService).scanPdfFromFile(resumeFile)
    verify(mockKeywordExtractorService).extractKeywords(jobDescription)
  }
}
