import com.resume.ats.check.service.ScanPdfService
import org.mockito.Mockito._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

class ScanPdfServiceTest extends AnyFlatSpec with Matchers {

  val service = new ScanPdfService()

  "ScanPdfService" should "scan PDF file and extract text" in {
    // Create a simple PDF file content as a byte array
    val pdfContent = "%PDF-1.5\n1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj\n2 0 obj<</Type/Pages/Kids[3 0 R]/Count 1>>endobj\n3 0 obj<</Type/Page/Parent 2 0 R/Resources<</Font<</F1<</Type/Font/BaseFont/Helvetica/Subtype/Type1>>>>>>/MediaBox[0 0 595 842]/Contents 4 0 R>>endobj\n4 0 obj<</Length 55>>stream\nBT\n/F1 18 Tf\n100 750 Td\n(This is a test PDF file) Tj\nET\nendstream\nendobj\nxref\n0 5\n0000000000 65535 f\n0000000009 00000 n\n0000000056 00000 n\n0000000115 00000 n\n0000000243 00000 n\ntrailer<</Size 5/Root 1 0 R>>\nstartxref\n365\n%%EOF".getBytes(StandardCharsets.UTF_8)

    val file = mock(classOf[MultipartFile])
    when(file.getInputStream).thenReturn(new ByteArrayInputStream(pdfContent))

    val expected = "this is a test pdf file"
    val result = service.scanPdfFromFile(file)
    result shouldEqual expected
  }

  it should "handle an empty PDF file" in {
    val pdfContent = Array[Byte]()

    val file = mock(classOf[MultipartFile])
    when(file.getInputStream).thenReturn(new ByteArrayInputStream(pdfContent))

    val expected = ""
    val result = service.scanPdfFromFile(file)
    result shouldEqual expected
  }
}
