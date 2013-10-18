package bleibinhaus.gremlinscalaexample

import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph
import com.tinkerpop.gremlin.scala.ScalaGraph
import com.tinkerpop.gremlin.scala.wrapScalaGraph.apply

class Neo4jTest extends GraphTestBase {
  val neo4jDirectory = "/tmp/neo4j"

  runTests("Neo4j")

  override def getGraph = new Neo4jGraph(neo4jDirectory)
}
