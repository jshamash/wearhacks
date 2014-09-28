name := "Wearhacks"

version := "1.0"

organization := "Team 38"

scalaVersion  := "2.10.4"

scalacOptions := Seq("-feature", "-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
	"Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
	"Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
	"Spray.io Repository" at "http://repo.spray.io",
	"Spray.io Nightly" at "http://nightlies.spray.io/",
	"Orient Technologies Maven2 Repository" at "http://www.orientechnologies.com/listing/m2"
)


libraryDependencies ++= {
  val akkaV = "2.3.0"
  val sprayV = "1.3.1"
  val sprayJsonV = "1.2.6"
  val orientV = "1.7.8"
  Seq(
    "io.spray"            %   "spray-can"     % sprayV,
    "io.spray"            %   "spray-routing" % sprayV,
    "io.spray"            %   "spray-client"  % sprayV,
    "io.spray"            %%  "spray-json"	  % sprayJsonV,
    "io.spray"            %   "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.scala-lang.modules" %% "scala-async" % "0.9.1",
    "com.orientechnologies" % "orient-commons"  % orientV,
    "com.orientechnologies" % "orientdb-core"   % orientV,
    "com.orientechnologies" % "orientdb-client" % orientV,
    "com.orientechnologies" % "orientdb-enterprise" % orientV,
    "com.orientechnologies" % "orientdb-graphdb" % orientV,
    "com.michaelpollmeier" %% "gremlin-scala" % "2.5.2",
    "net.jalg" % "hawkj" % "1.3",
    "com.amazonaws" % "aws-java-sdk" % "1.0.002"
  )
}

Revolver.settings
