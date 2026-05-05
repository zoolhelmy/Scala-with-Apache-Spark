ThisBuild / version := "0.1.0-FINAL"

ThisBuild / scalaVersion := "2.12.10"

lazy val root = (project in file("."))
  .settings(
    name := "FlightAnalytics"
  )

enablePlugins(AssemblyPlugin)

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "2.4.8",
  "org.apache.spark" %% "spark-sql" % "2.4.8",
  "org.scalatest" %% "scalatest" % "3.2.18" % Test
)
