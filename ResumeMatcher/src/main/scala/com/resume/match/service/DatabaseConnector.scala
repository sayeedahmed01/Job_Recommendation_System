package com.resume.ats.check.service

import java.util.Properties
import org.springframework.stereotype.Service
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}
import java.sql.{Connection, DriverManager, ResultSet}


@Service
class DatabaseConnector{
  private val jdbcUrl = "jdbc:mysql://127.0.0.1/jobs_db"
  private val dbTable = "jobs_all"
  private val dbUser = "root"
  private val dbPassword = "root"

  def loadData(category: String): ResultSet = {
    Class.forName("com.mysql.jdbc.Driver")
    val conn = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword)

    // create a statement and execute a query
    val stmt = conn.createStatement()
    //val row = stmt.executeQuery("SELECT * FROM "+ dbTable + "where category = " + category)
    val row = stmt.executeQuery(s"SELECT * FROM $dbTable WHERE category = '$category' ORDER BY timeScraped DESC")

    //println(row)

    row
  }



}
