ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "ScalaImplementation" ,
    libraryDependencies += "org.seleniumhq.selenium" % "selenium-java" % "3.141.59",
    libraryDependencies += "io.github.bonigarcia" % "webdrivermanager" % "5.0.3" ,
    libraryDependencies += "org.jsoup" % "jsoup" % "1.14.3"

  )
