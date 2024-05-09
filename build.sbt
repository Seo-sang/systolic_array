// See README.md for license details.

ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0"
ThisBuild / organization     := "com.github.seosang"

val chiselVersion = "3.5.6"

lazy val root = (project in file("."))
  .settings(
    name := "systolic_array",
    libraryDependencies ++= Seq(
      "org.chipsalliance" %% "chisel" % "6.2.0",
      "org.scalatest" %% "scalatest" % "3.2.16" % "test",
    ),
    scalacOptions ++= Seq(
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-Xcheckinit",
      "-P:chiselplugin:genBundleElements",
    ),
    addCompilerPlugin("org.chipsalliance" % "chisel-plugin" % "6.2.0" cross CrossVersion.full),
  )