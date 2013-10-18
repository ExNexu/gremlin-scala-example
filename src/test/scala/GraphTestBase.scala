package bleibinhaus.gremlinscalaexample

import scala.collection.JavaConverters.iterableAsScalaIterableConverter

import org.scalatest.BeforeAndAfter
import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers

import com.tinkerpop.gremlin.scala.ScalaGraph
import com.tinkerpop.gremlin.scala.ScalaGraph.unwrap
import com.tinkerpop.gremlin.scala.ScalaVertex.wrap

abstract class GraphTestBase extends FunSpec with ShouldMatchers with BeforeAndAfter {
  def getGraph: ScalaGraph
  var graph: ScalaGraph = null

  def runTests(dbname: String) =
    describe(s"A $dbname Graph Database") {
      it("should allow to store and retrieve primitive data in a vertex") {
        val vertex = graph.addV()
        vertex.setProperty("stringKey", "string")
        vertex.setProperty("intKey", 2)
        vertex.setProperty("booleanKey", true)
        vertex.setProperty("doubleKey", 1.23)
        graph.v(vertex.getId()).getProperty[String]("stringKey") should be("string")
        graph.v(vertex.getId()).getProperty[Int]("intKey") should be(2)
        graph.v(vertex.getId()).getProperty[Boolean]("booleanKey") should be(true)
        graph.v(vertex.getId()).getProperty[Double]("doubleKey") should be(1.23)
      }

      it("should be able to create and find edges between vertices") {
        val v1 = graph.addV()
        val v2 = graph.addV()
        graph.addE(v1, v2, "label")

        val foundVertices = v1.out("label").toList
        foundVertices.size should be(1)
        foundVertices.get(0) should be(v2)
      }
    }

  before {
    graph = getGraph
  }

  after {
    try {
      graph.V.startPipe.toList map {
        graph.removeVertex(_)
      }
      graph.V.startPipe.toList.isEmpty should be(true)
    }
    graph.shutdown()
  }

  protected implicit def asScalaIterable[T](javaIterable: java.lang.Iterable[T]): Iterable[T] =
    javaIterable.asScala
}
