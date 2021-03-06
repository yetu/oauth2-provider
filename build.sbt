import java.nio.file.{Files, Paths}

import com.typesafe.sbt.SbtScalariform._
import sbt.Keys._
import scoverage.ScoverageSbtPlugin

import scalariform.formatter.preferences._
import java.nio.file.{Paths, Files}

scalaVersion := "2.11.6"

name := "oauth2provider"

organization := "com.yetu"

resolvers += Resolver.bintrayRepo("yetu", "maven")

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb)
  .configs(CustomIntegrationTest, BrowserTest)
  .settings(inConfig(CustomIntegrationTest)(Defaults.testTasks): _*)
  .settings(inConfig(BrowserTest)(Defaults.testTasks): _*)
  .settings(
    testOptions in Test := Seq(Tests.Filter(unitFilter)),
    testOptions in CustomIntegrationTest := Seq(Tests.Filter(integrationFilter)),
    testOptions in BrowserTest := Seq(Tests.Filter(browserFilter))
  )



pipelineStages := Seq(digest, gzip)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.scalatestplus" %% "play" % "1.2.0" % "test",
  "org.scalacheck" %% "scalacheck" % "1.12.2" % "test",

  "com.yetu" %% "securesocial" % "3.0.10",
  "com.yetu" %% "yetu-notification-client-scala" % "1.5",
  "com.yetu" %% "yetu-play-common-views"  % "0.0.2",

  "com.nulab-inc" %% "play2-oauth2-provider" % "0.14.0",

  "net.adamcin.httpsig" % "httpsig-api" % "1.0.6",
  "net.adamcin.httpsig" % "httpsig-ssh-jce" % "1.0.6",
  "net.adamcin.httpsig" % "httpsig-ssh-bc" % "1.2.0",
  "org.bouncycastle" % "bcpkix-jdk15on" % "1.51",
  "com.unboundid" % "unboundid-ldapsdk" % "2.3.8",
  "net.logstash.logback" % "logstash-logback-encoder" % "3.0",
  "com.softwaremill.macwire" %% "macros" % "1.0.2",
  "com.softwaremill.macwire" %% "runtime" % "1.0.2",
  "com.yetu" %% "oauth2-resource-server" % "0.2.4",
  "com.yetu" %% "oauth2-resource-server" % "0.2.4" % "test" classifier "tests",

  //riak
  "com.scalapenos" %% "riak-scala-client" % "0.9.5",

  //JSON Web token libs
  "com.plasmaconduit" %% "jws" % "0.12.0",
  "com.plasmaconduit" %% "jwt" % "0.9.0"
)
libraryDependencies += filters

resolvers += Resolver.sonatypeRepo("snapshots")

resolvers += "Plasma Conduit Repository" at "http://dl.bintray.com/plasmaconduit/releases"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += Resolver.bintrayRepo("yetu", "maven")

scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  //  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Xlint", // Enable recommended additional warnings.
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  "-language:implicitConversions" //allow implicit convertions defined by implicit def convertAtoB(a:A):B type functions
)

//********************************************************
// Scalariform settings
//********************************************************
scalariformSettings

defaultScalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(DoubleIndentClassDeclaration, true)
  .setPreference(PreserveDanglingCloseParenthesis, true)
  .setPreference(PreserveSpaceBeforeArguments, true)


//********************************************************
// Test settings
//********************************************************
ScoverageSbtPlugin.ScoverageKeys.coverageExcludedPackages := "<empty>;Reverse.*;com.yetu.oauth2provider.views.html.*"


lazy val CustomIntegrationTest = config("it") extend Test

lazy val BrowserTest = config("browser") extend Test

parallelExecution in Test := false

parallelExecution in CustomIntegrationTest := false

parallelExecution in BrowserTest := false

javaOptions in Test += {
  "-Dconfig.file=conf/application-test.conf"
}

javaOptions in BrowserTest += {
  "-Dconfig.file=conf/application-test.conf"
}

javaOptions in CustomIntegrationTest += {
  if (Files.exists(Paths.get("conf/application.conf"))) {
    "-Dconfig.file=conf/application.conf"
  } else {
    "-Dconfig.file=conf/application-integrationtest.conf"
  }
}

def integrationFilter(name: String): Boolean = name endsWith "ITSpec"

def browserFilter(name: String): Boolean = name endsWith "BrowserSpec"

def unitFilter(name: String): Boolean = {
  ((name endsWith "Test") || (name endsWith "Spec")) &&
    ! integrationFilter(name) &&
    ! browserFilter(name)
}


