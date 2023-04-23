import Dependencies._

ThisBuild / scalaVersion     := "2.13.10"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "BigData_Interface",
    libraryDependencies += munit % Test,
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.4.0",
    libraryDependencies += "com.lihaoyi" %%% "upickle" % "1.4.2",
    libraryDependencies += "com.opencsv" % "opencsv" % "5.5.1",
    libraryDependencies +="com.github.tototoshi" %% "scala-csv" % "1.3.6",
    libraryDependencies +=  "org.seleniumhq.selenium" % "selenium-java" % "3.141.59",
    libraryDependencies +="org.scalatest" %% "scalatest" % "3.2.10" % Test,
      libraryDependencies += "io.github.bonigarcia" % "webdrivermanager" % "5.0.3" ,
    scalaJSUseMainModuleInitializer := true
  ).enablePlugins(ScalaJSPlugin)

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
