import bintray.Plugin.bintrayPublishSettings
import bintray.Keys._

organization := "io.github"
name := "crawler"

crossScalaVersions := Seq("2.11.6", "2.10.5")
scalaVersion := crossScalaVersions.value.head

libraryDependencies ++= Seq (
    "net.sourceforge.htmlunit" % "htmlunit" % "2.9"
  , "org.specs2" %% "specs2" % "2.3.13-scalaz-7.1.0-RC1" % "test"
)

// git versioning
enablePlugins(GitVersioning)
useJGit

// releasing to bintray
licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))
bintrayPublishSettings
bintrayOrganization in bintray := Some("crawler")
