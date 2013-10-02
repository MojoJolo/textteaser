import com.typesafe.sbt.SbtStartScript

seq(SbtStartScript.startScriptForClassesSettings: _*)

name := "textteaser"

version := "1.0"

scalaVersion := "2.10.1"

libraryDependencies ++= Seq(
	"com.google.code.gson" % "gson" % "2.2.4",
	"org.apache.opennlp" % "opennlp-tools" % "1.5.2-incubating",
	"net.codingwell" %% "scala-guice" % "3.0.2",
	"org.json4s" %% "json4s-native" % "3.2.4",
	"com.foursquare" %% "rogue-field" % "2.2.0" intransitive(),
	"com.foursquare" %% "rogue-core" % "2.2.0" intransitive(),
	"com.foursquare" %% "rogue-lift" % "2.2.0" intransitive(),
	"com.foursquare" %% "rogue-index" % "2.2.0" intransitive(),
	"net.liftweb" %% "lift-mongodb-record" % "2.5.1",
	"org.slf4j" % "slf4j-api" % "1.7.5",
  	"ch.qos.logback" % "logback-classic" % "1.0.13"
)

resolvers += "OpenNLP Repository" at "http://opennlp.sourceforge.net/maven2/"