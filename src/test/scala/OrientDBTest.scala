package bleibinhaus.gremlinscalaexample

import com.tinkerpop.blueprints.impls.orient.OrientGraph
import com.tinkerpop.gremlin.scala.ScalaGraph
import com.tinkerpop.gremlin.scala.wrapScalaGraph.apply

class OrientDBTest extends GraphTestBase {

  runTests("OrientDB")

  override def getGraph = new OrientGraph("memory:gremlin-scala-example")
  // works as well, but needs to shut down the process (sbt) between runs:
  // see https://code.google.com/p/orient/wiki/PerformanceTuning#Parameters -> storage.keepOpen
  // override def getGraph = new OrientGraph("local:/tmp/orientdb")
}
