name := """restaurant-remote-reception"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
    .enablePlugins(PlayScala, JavaAppPackaging, DockerPlugin)
    .settings(
        name := "restaurant-remote-reception",
        packageName in Docker := "restaurant-remote-reception",
        dockerBaseImage := "openjdk:8",
        dockerExposedPorts := Seq(9000, 9443),
        dockerExposedVolumes := Seq("/opt/docker/logs")
    )

scalaVersion := "2.11.11"

libraryDependencies += filters
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.1" % Test

libraryDependencies += "com.typesafe.play" %% "play-slick" % "2.0.2"


// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"