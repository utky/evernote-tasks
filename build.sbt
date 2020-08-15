import Dependencies._

ThisBuild / scalaVersion     := "2.13.2"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "ilyaletre"
ThisBuild / organizationName := "ilyaletre"

lazy val root = (project in file("."))
  .settings(
    name := "evernote-tasks",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.0.0",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
      "com.evernote" % "evernote-api" % "1.25.1",
      "org.scalatra.scalate" %% "scalate-core" % "1.9.6",
      scalaTest % Test
    )
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
