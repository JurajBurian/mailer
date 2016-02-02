organization := "jubu"
name := "mailer"

scalaVersion := "2.11.7"

crossScalaVersions := Seq("2.10.5", "2.11.7")

version := "1.0.0"

crossPaths := false

description := "Thin wrapper of JavaMail library written in Scala language. Mailer is aim to be used in situations when is necessary send multiple mails, e.t. instance of javax.mail.Session is created and used by Mailer."

scalacOptions := Seq(
	"-encoding", "UTF-8",
	"-unchecked",
	"-deprecation",
	"-feature",
	"-Xfatal-warnings",
	"-Xlint",
	"-Yrangepos",
	"-language:postfixOps"
)

libraryDependencies ++= Seq(
	"javax.mail" % "mail" % "1.4.7",
	"de.saly" % "javamail-mock2-fullmock" % "0.5-beta3" % "test",
	"org.scalatest" %% "scalatest" % "3.0.0-M15" % "test"
)

