//import AssemblyKeys._

name := "rest"

version := "1.0"

scalaVersion := "2.12.7"

val AkkaVersion = "2.6.18"
val AkkaHttpVersion = "10.2.9"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.11",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.13.2",
  "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
  //    "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "org.scalatest" %% "scalatest" % "3.1.4",
  "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion % Test

)

val circeVersion = "0.14.1"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

resolvers ++= Seq(
  //    "Spray repository" at "https://repo.spray.io",
  //    "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"
)

//assemblySettings
