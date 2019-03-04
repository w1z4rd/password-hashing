name         := """password-hashing"""
version      := "0.1.0-SNAPSHOT"
organization := "costa"

scalaVersion := "2.12.8"

resolvers ++= Seq(Resolver.jcenterRepo)

  libraryDependencies ++= {
    Seq("org.specs2" %% "specs2-core" % "3.8.6" % "test")
  }

scalafmtConfig := Some(file(".scalafmt"))

