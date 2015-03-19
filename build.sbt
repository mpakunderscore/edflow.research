lazy val root = (project in file(".")).enablePlugins(PlayJava)

name := "edtag"

version := "2.0"

scalaVersion := "2.11.1"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.2.3"

resolvers ++= Seq(
  "Maven Repository" at "http://repo1.maven.org/maven2/",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

javacOptions ++= Seq("-source", "1.7")

libraryDependencies ++= Seq( jdbc , anorm , cache , ws )

libraryDependencies += javaEbean

libraryDependencies += "postgresql" % "postgresql" % "9.1-901.jdbc4"

libraryDependencies += "org.jsoup" % "jsoup" % "1.7.3"

libraryDependencies += "com.google.api.client" % "google-api-client-json" % "1.2.3-alpha"