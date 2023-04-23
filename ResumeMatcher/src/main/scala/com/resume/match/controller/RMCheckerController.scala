package com.resume.ats.check.controller

import java.io.IOException
import java.sql.ResultSet

import com.resume.ats.check.models.RMDetail
import com.resume.ats.check.service.{RMCheckerService, DatabaseConnector}
import lombok.RequiredArgsConstructor
import org.springframework.web.bind.annotation.{CrossOrigin, PostMapping, RequestParam, RestController}
import org.springframework.web.multipart.MultipartFile
import play.api.libs.json.{JsArray, JsString, Json}

import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks.{break, breakable}

@RestController
@RequiredArgsConstructor
@CrossOrigin
class RMCheckerController(private val rmCheckerService: RMCheckerService, private val databaseConnector: DatabaseConnector) {
  @PostMapping(Array("/check-resume"))
  @throws[IOException]
  def generateRMDetails(@RequestParam("file") resumefile: MultipartFile, @RequestParam("category") category: String): String = {
    val row: ResultSet = databaseConnector.loadData(category)
    val resultSet = new ListBuffer[Seq[String]]
    var counter = 0

    breakable {
      while (counter < 10) {
        if (!row.next()) {
          break
        }

        val jobID = row.getString(1)
        val jobTitle = row.getString(2)
        val company = row.getString(3)
        val location = row.getString(4)
        val jobLink = "www.indeed.com" + row.getString(5)
        val salary = row.getString(6)
        val jobDescription = row.getString(7)
        val category = row.getString(8)
        val timeScraped = row.getString(9)

        val matched = generateMoreMatches(resumefile, jobDescription)
        val temp = Seq(jobID, jobTitle, company, location, jobLink, salary, category, timeScraped, matched)
        resultSet.append(temp)

        counter += 1
      }
    }

    val json = JsArray(resultSet.map(seq => JsArray(seq.map(JsString))))
    json.toString()
  }

  @throws[IOException]
  def generateMoreMatches(resumefile: MultipartFile, desc: String): String = {
    val rmDetails: RMDetail = rmCheckerService.generateRMDetails(resumefile, desc)
    rmDetails.toString
  }
}
