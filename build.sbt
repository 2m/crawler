name := "crawler"

version := "0.6.0"

scalaVersion := "2.11.2"

scalaBinaryVersion := "2.11"

scalacOptions := Seq("-deprecation", "-unchecked", "-feature")

libraryDependencies ++= Seq (
    "net.sourceforge.htmlunit" % "htmlunit" % "2.9"
  , "org.specs2" %% "specs2" % "2.3.13-scalaz-7.1.0-RC1" % "test"
)
