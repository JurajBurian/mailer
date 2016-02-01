organization := "jubu"
name := "mailer"

scalaVersion := "2.11.7"
crossScalaVersions := Seq("2.10.5", "2.11.7")

version := "1.0.0"

description := ""

libraryDependencies ++= Seq(
	"javax.mail" % "mail" % "1.4.7",
	"de.saly" % "javamail-mock2-fullmock" % "0.5-beta3" % "test",
	"org.scalatest" %% "scalatest" % "3.0.0-M15" % "test"
)

