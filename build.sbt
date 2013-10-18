name := "gremlin-scala-example"

organization := "bleibinhaus"

version := "0.1"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.10" % "1.9.2" % "test",
  "com.michaelpollmeier" % "gremlin-scala" % "2.4.1",
  "com.tinkerpop.blueprints" % "blueprints-neo4j-graph" % "2.4.0",
  "com.tinkerpop.blueprints" % "blueprints-orient-graph" % "2.4.0"
)

initialCommands := "import bleibinhaus.gremlinscalaexample._"

