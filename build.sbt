// See README.md for license details.

ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0"
ThisBuild / organization     := "com.github.seosang"

val chiselVersion = "3.5.5"

lazy val root = (project in file("."))
  .settings(
    name := "systolic_array",
    libraryDependencies ++= Seq(
      "org.chipsalliance" %% "chisel" % "6.0.0",
      "edu.berkeley.cs" %% "chisel3" % chiselVersion,
      "edu.berkeley.cs" %% "chiseltest" % "0.6.1" % "test",
      "org.scalatest" %% "scalatest" % "3.2.16" % "test",
    ),
    scalacOptions ++= Seq(
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-Xcheckinit",
      "-P:chiselplugin:genBundleElements",
    ),
    addCompilerPlugin("edu.berkeley.cs" % "chisel3-plugin" % chiselVersion cross CrossVersion.full),
    addCompilerPlugin("org.chipsalliance" % "chisel-plugin" % "6.0.0" cross CrossVersion.full),
  )