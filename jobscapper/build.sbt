ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "ScalaImplementation" ,
      libraryDependencies ++= Seq(
          "org.seleniumhq.selenium" % "selenium-java" % "3.141.59",
          "io.github.bonigarcia" % "webdrivermanager" % "5.0.3" ,
          "org.jsoup" % "jsoup" % "1.14.3",
          "org.scalaj" %% "scalaj-http" % "2.4.2",
          "com.lihaoyi" %% "ujson" % "1.4.3",
          "io.circe" %% "circe-parser" % "0.14.1",
          "org.scalaj" %% "scalaj-http" % "2.4.2",
          "com.microsoft.playwright" % "playwright" % "1.17.1",
          "com.lihaoyi"%%"requests" % "0.7.0",
          "org.scalatest" %% "scalatest" % "3.2.10" % "test",
          "org.apache.spark" %% "spark-core" % "3.3.2",
          "org.apache.spark" %% "spark-sql" % "3.3.2",
          "org.apache.spark" %% "spark-streaming" % "3.3.2",
          "org.mongodb.spark" %% "mongo-spark-connector" % "10.1.1"

      )
  )
