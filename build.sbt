import Dependencies._

ThisBuild / scalaVersion     := "2.13.2"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "ilyaletre"
ThisBuild / organizationName := "ilyaletre"

lazy val root = (project in file("."))
  .settings(
    name := "evernote-tasks",
    libraryDependencies += "com.evernote" % "evernote-api" % "1.25.1",
    libraryDependencies += "org.scalatra.scalate" %% "scalate-core" % "1.9.6",
    libraryDependencies += scalaTest % Test
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
