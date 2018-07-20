name := """restaurant-remote-reception"""
organization := "com.example"

version := "latest"

lazy val root = (project in file("."))
    .enablePlugins(PlayScala, JavaAppPackaging, DockerPlugin)
    .settings(
        name := "restaurant-remote-reception",
        packageName in Docker := "restaurant-remote-reception",
        dockerBaseImage := "openjdk:8",
        dockerExposedPorts := Seq(9000, 9000)
    )

scalaVersion := "2.11.11"

libraryDependencies += filters
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.1" % Test
libraryDependencies += "com.typesafe.play" %% "play-slick" % "2.0.2"
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.36"


