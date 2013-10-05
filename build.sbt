name := "gremlin-scala-example"

organization := "bleibinhaus"

version := "0.1"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.10" % "1.9.2" % "test"
)

initialCommands := "import bleibinhaus.gremlinscalaexample._"

