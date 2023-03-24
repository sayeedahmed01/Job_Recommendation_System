ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "jobscapper",
    idePackagePrefix := Some(" package edu.neu.coe.csye7200.jobscapper")
  )
