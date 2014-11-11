import com.typesafe.sbt.SbtStartScript

seq(SbtStartScript.startScriptForClassesSettings: _*)

name := "textteaser"

version := "1.0"

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
	"com.google.code.gson" % "gson" % "2.3",
	"org.apache.opennlp" % "opennlp-tools" % "1.5.3",
	"net.codingwell" %% "scala-guice" % "4.0.0-beta4",
	"org.json4s" %% "json4s-native" % "3.2.11",
	"com.foursquare" %% "rogue-field" % "2.4.0" intransitive(),
	"com.foursquare" %% "rogue-core" % "2.4.0" intransitive(),
	"com.foursquare" %% "rogue-lift" % "2.4.0" intransitive(),
	"com.foursquare" %% "rogue-index" % "2.4.0" intransitive(),
	"net.liftweb" %% "lift-mongodb-record" % "2.6-RC1",
	"org.slf4j" % "slf4j-api" % "1.7.7",
	"ch.qos.logback" % "logback-classic" % "1.1.2",
	"com.google.guava" % "guava" % "18.0",
	"com.google.code.findbugs" % "jsr305" % "2.0.2",
	"org.scalatest" %% "scalatest" % "2.2.2" % "test"
)

resolvers += "OpenNLP Repository" at "http://opennlp.sourceforge.net/maven2/"

resolvers += "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"

resolvers += "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"

