ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "ScalaImplementation" ,
    libraryDependencies += "org.seleniumhq.selenium" % "selenium-java" % "3.141.59",
    libraryDependencies += "io.github.bonigarcia" % "webdrivermanager" % "5.0.3" ,
    libraryDependencies += "org.jsoup" % "jsoup" % "1.14.3",
    libraryDependencies += "org.scalaj" %% "scalaj-http" % "2.4.2",
    libraryDependencies += "com.lihaoyi" %% "ujson" % "1.4.3",
    libraryDependencies += "io.circe" %% "circe-parser" % "0.14.1",
    libraryDependencies += "org.scalaj" %% "scalaj-http" % "2.4.2",
    libraryDependencies += "com.microsoft.playwright" % "playwright" % "1.17.1",
    libraryDependencies += "com.lihaoyi"%%"requests" % "0.7.0",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % "test"

  )
