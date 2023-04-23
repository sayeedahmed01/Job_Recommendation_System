name := "ResumeMatcher"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.13.6"

val springVersion = "3.0.2"

libraryDependencies ++= Seq(
      "org.springframework.boot" % "spring-boot-starter-web" % springVersion,
      "org.springframework.boot" % "spring-boot-devtools" % springVersion % "runtime",
      "commons-io" % "commons-io" % "2.11.0",
      "org.projectlombok" % "lombok" % "1.18.20" % "optional",
      "org.apache.pdfbox" % "pdfbox" % "2.0.24",
      "org.apache.pdfbox" % "fontbox" % "2.0.24",
      "org.apache.opennlp" % "opennlp-tools" % "2.1.1",
      "org.springframework" % "spring-context" % "5.3.14",
      "org.springframework" % "spring-web" % "5.3.14",
      "org.springframework" % "spring-webmvc" % "5.3.14",
     "javax.inject" % "javax.inject" % "1",
     "org.apache.spark" %% "spark-sql" % "3.2.0",
       "mysql" % "mysql-connector-java" % "8.0.26",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.13.0",
  "org.scalatest" %% "scalatest" % "3.2.10" % Test,
  "org.scalatestplus" %% "mockito-3-4" % "3.2.10.0" % Test,
  "org.mockito" % "mockito-core" % "3.12.4" % Test,
  "com.typesafe.play" %% "play-json" % "2.9.2",
  "org.springframework" % "spring-test" % "5.3.13" % Test


)

Compile / unmanagedResourceDirectories += baseDirectory.value / "src" / "main" / "resources"

