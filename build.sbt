ThisBuild / scalaVersion     := "2.13.1"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

val uzhttp = "org.polynote" %% "uzhttp" % "0.1.1"

lazy val root = (project in file("."))
  .settings(
    name := "single-sign-on",
    libraryDependencies ++= Seq(
      uzhttp
    )
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
