package bleibinhaus.gremlinscalaexample

import scala.collection.JavaConverters.iterableAsScalaIterableConverter

import com.tinkerpop.blueprints.Edge
import com.tinkerpop.blueprints.Vertex
import com.tinkerpop.gremlin.scala.GremlinScalaPipeline
import com.tinkerpop.gremlin.scala.ScalaGraph
import com.tinkerpop.gremlin.scala.ScalaGraph.unwrap
import com.tinkerpop.gremlin.scala.ScalaPipeFunction
import com.tinkerpop.gremlin.scala.ScalaVertex.wrap
import com.tinkerpop.pipes.branch.LoopPipe.LoopBundle
import org.scalatest.BeforeAndAfter
import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers

abstract class GraphTestBase extends FunSpec with ShouldMatchers with BeforeAndAfter {
  def getGraph: ScalaGraph
  var graph: ScalaGraph = null

  val distanceInKm = "distanceInKm"
  val minutesToDrive = "minutesToDrive"

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

      it("should be able to find the shortest path from Auckland to Cape Reinga") {

        def addLocation(location: String) = {
          val vertex = graph.addV()
          vertex.setProperty("name", location)
          vertex
        }

        def addRoad(location1: Vertex, location2: Vertex, distanceKm: Int, minutesDrive: Int) = {
          def edgeLabel(from: Vertex, to: Vertex) =
            from.getProperty[String]("name") + " -> " + to.getProperty[String]("name")
          def setEdgeProperties(edge: Edge) = {
            edge.setProperty(distanceInKm, distanceKm)
            edge.setProperty(minutesToDrive, minutesDrive)
          }
          val edge1Label = edgeLabel(location1, location2)
          val edge1 = graph.addE(location1, location2, edge1Label)
          setEdgeProperties(edge1)
          val edge2Label = edgeLabel(location2, location1)
          val edge2 = graph.addE(location2, location1, edge2Label)
          setEdgeProperties(edge2)
          (edge1, edge2)
        }

        val auckland = addLocation("Auckland")
        val whangarei = addLocation("Whangarei")
        val dargaville = addLocation("Dargaville")
        val kaikohe = addLocation("Kaikohe")
        val kerikeri = addLocation("Kerikeri")
        val kaitaia = addLocation("Kaitaia")
        val capeReinga = addLocation("Cape Reinga")

        addRoad(auckland, whangarei, 158, 117)
        addRoad(whangarei, kaikohe, 85, 66)
        addRoad(kaikohe, kaitaia, 82, 59)
        addRoad(kaitaia, capeReinga, 111, 83)
        addRoad(whangarei, kerikeri, 85, 66)
        addRoad(kerikeri, kaitaia, 88, 64)
        addRoad(auckland, dargaville, 175, 131)
        addRoad(dargaville, kaikohe, 77, 69)
        addRoad(kaikohe, kerikeri, 36, 29)

        def aucklandToCapeReingaLoop(
          gremlinScalaPipeline: GremlinScalaPipeline[Vertex, Vertex],
          startstep: String) =
          gremlinScalaPipeline.loop(
            startstep,
            (loopBundle: LoopBundle[Vertex]) ⇒ {
              loopBundle.getLoops() < 7 &&
                !loopBundle.getObject.getId().toString
                  .equals(capeReinga.getId().toString)
            },
            (loopBundle: LoopBundle[Vertex]) ⇒ {
              loopBundle.getObject.getId().toString
                .equals(capeReinga.getId().toString)
            }
          )

        val aucklandOut = auckland.->.as("auckland").out()
        val aucklandToCR = aucklandToCapeReingaLoop(aucklandOut, "auckland")
        val aucklandToCRLocations = aucklandToCR.path(
          new ScalaPipeFunction[Vertex, String](
            (v: Vertex) ⇒ v.getProperty[String]("name")
          )
        ).toList

        aucklandToCRLocations.size should be(29)

        val aucklandOutEdgesInVertices = auckland.->.as("auckland").outE().inV()
        val aucklandToCREdgesAndVertices =
          aucklandToCapeReingaLoop(aucklandOutEdgesInVertices, "auckland")
        val aucklandToCRTimeAndLocations =
          aucklandToCREdgesAndVertices.path.map(
            (elements: java.util.List[_]) ⇒
              (
                elements.filter(_.isInstanceOf[Edge]).map {
                  case e: Edge ⇒ e.getProperty[Int](minutesToDrive)
                }
                .reduce(_ + _),
                (
                  elements.filter(_.isInstanceOf[Vertex]).map {
                    case v: Vertex ⇒ v.getProperty[String]("name")
                  }
                )
              )
          ).toScalaList.sortBy(_._1)

        aucklandToCRTimeAndLocations(0)._1 should be(325)

        aucklandToCRTimeAndLocations.foreach {
          case (totalMinutesToDrive, locations) ⇒
            println(s"$totalMinutesToDrive min: ${locations.mkString(" -> ")}")
        }
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
