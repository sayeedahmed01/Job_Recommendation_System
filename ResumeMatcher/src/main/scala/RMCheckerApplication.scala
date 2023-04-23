package com.resume.ats.check

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

object RMCheckerApplication {
  def main(args: Array[String]): Unit = {
    SpringApplication.run(classOf[RMCheckerApplication], args: _*)
  }
}

@SpringBootApplication
class RMCheckerApplication
