import org.scalajs.dom
import org.scalajs.dom.{console, document, html}
import org.scalajs.dom.raw.{Event, XMLHttpRequest}

import scala.scalajs.js
import scala.scalajs.js.JSON

object JobScraper {
  def main(args: Array[String]): Unit = {
    // Add a header and description
    val header = document.createElement("h1").asInstanceOf[html.Heading]
    header.innerHTML = "Job Scraper"
    val description = document.createElement("p").asInstanceOf[html.Paragraph]
    description.innerHTML = "Our project is an automated system to match job seekers with the latest job postings online for jobs that they are interested in."

    val fileInput = document.createElement("input").asInstanceOf[dom.html.Input]
    fileInput.setAttribute("type", "file")

    // Create a drop-down menu for category selection
    val categorySelect = document.createElement("select").asInstanceOf[dom.html.Select]
    val options = List("data engineer", "data scientist", "human resources", "software engineering")
    options.foreach { option =>
      val optionElement = document.createElement("option").asInstanceOf[dom.html.Option]
      optionElement.value = option
      optionElement.innerHTML = option
      categorySelect.appendChild(optionElement)
    }

    val submitButton = document.createElement("button").asInstanceOf[dom.html.Button]
    submitButton.innerHTML = "Submit"

    val responseContainer = document.createElement("div").asInstanceOf[dom.html.Div]

    submitButton.onclick = (e: Event) => {
      val file = fileInput.files(0)
      val selectedCategory = categorySelect.value

      val formData = new dom.FormData()
      formData.append("file", file)
      formData.append("category", selectedCategory)

      val xhr = new XMLHttpRequest()
      xhr.open("POST", "http://localhost:8001/check-resume")

      xhr.onload = (e: Event) => {
        val responseText = xhr.responseText
        val jsonResponse = JSON.parse(responseText).asInstanceOf[js.Array[js.Array[String]]]

        val table = document.createElement("table").asInstanceOf[dom.html.Table]
        table.setAttribute("border", "1")

        jsonResponse.foreach { row =>
          val tr = document.createElement("tr").asInstanceOf[dom.html.TableRow]

          row.zipWithIndex.foreach { case (cell, index) =>
            if (index != 0 && index != 7) {
              val td = document.createElement("td").asInstanceOf[dom.html.TableCell]

              if (index == 4) {
                val link = document.createElement("a").asInstanceOf[dom.html.Anchor]
                link.href = cell
                link.innerHTML = cell
                link.target = "_blank"
                td.appendChild(link)
              } else {
                td.innerHTML = cell
              }

              tr.appendChild(td)
            }
          }
          table.appendChild(tr)
        }

        responseContainer.innerHTML = ""
        responseContainer.appendChild(table)
      }

      xhr.send(formData)
    }

    // Append the header, description, and input elements to the document body
    document.body.appendChild(header)
    document.body.appendChild(description)
    document.body.appendChild(fileInput)
    document.body.appendChild(categorySelect)
    document.body.appendChild(submitButton)
    document.body.appendChild(responseContainer)

    // Add CSS styles
    document.head.innerHTML +=
      """
    <style>
      body {
      font-family: Arial, sans-serif;
    max-width: 800px;
    margin: 0 auto;
    }
    h1, p {
    text-align: center;
    }
    input, select, button {
    display: block;
    margin: 10px auto;
}
table {
  margin: 20px auto;
  border-collapse: collapse;
}
td, th {
  padding: 5px;
  text-align: left;
}
</style>
"""
  }
}