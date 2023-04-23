import com.resume.ats.check.controller.RMCheckerController
import com.resume.ats.check.models.RMDetail
import com.resume.ats.check.service.{RMCheckerService, DatabaseConnector}
import org.mockito.ArgumentMatchers.{any, eq => argEq}

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile



import java.io.IOException
import java.sql.ResultSet
import scala.collection.mutable.ListBuffer

class RMCheckerControllerTest extends AnyFlatSpec with Matchers with MockitoSugar {

  val atsCheckerService: RMCheckerService = mock[RMCheckerService]
  val databaseConnector: DatabaseConnector = mock[DatabaseConnector]
  val atsCheckerController = new RMCheckerController(atsCheckerService, databaseConnector)

  "generateAtsDetails" should "return a JSON string with valid input" in {
    val resumefile = new MockMultipartFile("resume", "resume.pdf", "application/pdf", "resume content".getBytes)
    val category = "Software Engineer"
    val resultSet: ResultSet = mock[ResultSet]

    when(databaseConnector.loadData(category)).thenReturn(resultSet)
    when(resultSet.next()).thenReturn(true, false)
    when(atsCheckerService.generateRMDetails(any[MultipartFile], any[String])).thenReturn(new RMDetail)

    val result = atsCheckerController.generateRMDetails(resumefile, category)
    result should startWith("[[")
    result should endWith("]]")
  }

  "generateAtsDetails" should "return an error or empty JSON string with an invalid category" in {
    val resumefile = new MockMultipartFile("resume", "resume.pdf", "application/pdf", "resume content".getBytes)
    val category = "Invalid Category"
    val resultSet: ResultSet = mock[ResultSet]

    when(databaseConnector.loadData(category)).thenReturn(resultSet)
    when(resultSet.next()).thenReturn(false)

    val result = atsCheckerController.generateRMDetails(resumefile, category)
    result shouldEqual "[]"
  }


  "generateAtsDetails" should "return an error message or IOException when an IOException occurs during file processing" in {
    val resumefile = new MockMultipartFile("resume", "resume.pdf", "application/pdf", "resume content".getBytes)
    val category = "Software Engineer"
    val resultSet: ResultSet = mock[ResultSet]

    when(databaseConnector.loadData(category)).thenReturn(resultSet)
    when(resultSet.next()).thenReturn(true, false)
    when(atsCheckerService.generateRMDetails(any[MultipartFile], any[String])).thenThrow(new IOException("File processing error"))

    assertThrows[IOException] {
      atsCheckerController.generateRMDetails(resumefile, category)
    }
  }

}

