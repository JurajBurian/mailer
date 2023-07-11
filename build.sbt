val pScalaVersion = "2.13.5"

organization := "com.github.jurajburian"

name := "mailer"

version := "1.2.4"

description := "Thin wrapper of JavaMail library written in Scala language. Mailer is aim to be used in situations when is necessary send multiple mails, e.t. instance of javax.mail.Session is created and used by Mailer."

scalaVersion in Scope.GlobalScope := pScalaVersion

crossScalaVersions := Seq("2.11.12", "2.12.13", pScalaVersion)

publishMavenStyle := true

publishTo := {
	val nexus = "https://oss.sonatype.org/"
	if (isSnapshot.value)
		Some("snapshots" at nexus + "content/repositories/snapshots")
	else
		Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

// enable automatic linking to the external Scaladoc of managed dependencies
autoAPIMappings := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
	<url>https://github.com/JurajBurian/mailer</url>
		<licenses>
			<license>
				<name>unlicense</name>
				<url>http://unlicense.org/</url>
				<distribution>repo</distribution>
			</license>
		</licenses>
		<scm>
			<url>https://github.com/jurajburian/mailer</url>
			<connection>scm:git:https://github.com/jurajburian/mailer</connection>
		</scm>
		<developers>
			<developer>
				<id>JurajBurian</id>
				<name>Juraj Burian</name>
				<url>https://github.com/JurajBurian</url>
			</developer>
			<developer>
				<id>vaclavsvejcar</id>
				<name>Vaclav Svejcar</name>
				<url>https://github.com/vaclavsvejcar</url>
			</developer>
			<developer>
				<id>basert</id>
				<name>Fabian Gruber</name>
				<url>https://github.com/basert</url>
			</developer>
		</developers>)

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
	"com.sun.mail" % "javax.mail" % "1.6.2",
	"de.saly" % "javamail-mock2-fullmock" % "0.5-beta4" % "test",
	"org.scalatest" %% "scalatest" % "3.0.9" % "test"
)
