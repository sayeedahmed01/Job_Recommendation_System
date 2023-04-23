package com.resume.ats.check.service

import java.io.IOException
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ScanPdfService {

  @throws[IOException]
  def scanPdfFromFile(file: MultipartFile): String = {
    if (file == null || file.isEmpty) {
      throw new IllegalArgumentException("File cannot be null or empty")
    }

    var document: PDDocument = null
    try {
      document = PDDocument.load(file.getInputStream)
      val stripper: PDFTextStripper = new PDFTextStripper
      val content: String = stripper.getText(document).toLowerCase
      content
    } finally {
      if (document != null) {
        document.close()
      }
    }
  }
}
