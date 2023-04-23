package com.resume.ats.check.service

import java.io.IOException
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import scala.collection.mutable.ListBuffer
import com.resume.ats.check.models.RMDetail
import javax.inject.Inject

@Service
class RMCheckerService @Inject()(keywordExtractorService: KeywordExtractorService,
                                 scanPdfService: ScanPdfService) {

  @throws[IOException]
  def generateRMDetails(file: MultipartFile, desc: String): RMDetail = {
    val rmDetail = new RMDetail
    val pdfContent = scanPdfService.scanPdfFromFile(file)
    rmDetail.setTotalKeywords(keywordExtractorService.extractKeywords(desc))
    val unmatchedKeywords = new ListBuffer[String]
    val matchedKeywords = new ListBuffer[String]

    for (keyword <- rmDetail.getTotalKeywords) {
      val k = keyword.toLowerCase
      if (pdfContent.contains(k)) matchedKeywords += k
      else unmatchedKeywords += k
    }

    val scalaList: List[String] = unmatchedKeywords.toList
    rmDetail.setUnMatchedKeywords(scalaList)

    val percentage = matchedKeywords.size.toDouble / rmDetail.getTotalKeywords.size * 100
    //rmDetail.setMatchPercentage(f"$percentage%.2f% of keywords matched")
    //rmDetail.setMatchPercentage(f"$$percentage%.2f%% of keywords matched")
    rmDetail.setMatchPercentage("" + percentage)

    rmDetail
  }
}