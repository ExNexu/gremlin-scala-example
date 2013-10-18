package bleibinhaus.gremlinscalaexample

import com.tinkerpop.blueprints.impls.tg.TinkerGraph
import com.tinkerpop.gremlin.scala.ScalaGraph
import com.tinkerpop.gremlin.scala.wrapScalaGraph.apply

class TinkerGraphTest extends GraphTestBase {

  runTests("TinkerGraph")

  override def getGraph = new TinkerGraph
}
