package findpaths

import org.neo4j.graphdb.{GraphDatabaseService, Direction, Node, Relationship, PropertyContainer}
import org.neo4j.kernel.Traversal
import org.neo4j.graphalgo.GraphAlgoFactory

import javax.ws.rs._
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response
import javax.ws.rs.core.MediaType

import scala.collection.JavaConverters._

import net.liftweb.json._
import net.liftweb.json.Extraction._

@Path("/findpaths")
class findpaths {

  implicit val formats = net.liftweb.json.DefaultFormats


  @GET
  @Path("/findpathslen/{id1}/{id2}/{len}/{count}")
  @Produces(Array("application/json"))
  def fof(@PathParam("id1") id1:Long, @PathParam("id2") id2:Long, @PathParam("len") len:Int, @PathParam("count") count:Int, @Context db:GraphDatabaseService) = {
    val node1 = db.getNodeById(id1)
    val node2 = db.getNodeById(id2)
    val pathFinder = GraphAlgoFactory.pathsWithLength(Traversal.pathExpanderForAllTypes(Direction.OUTGOING), len) 
    val pathIterator = pathFinder.findAllPaths(node1,node2).asScala
    Response.ok(compact(render(decompose(pathIterator.take(count).map(_.relationships.asScala.map(text(_)))))), MediaType.APPLICATION_JSON).build()
  }

  def text(value:Any):String = value match {
    case Nil => "<null>"
    case s:String => "\"" + s + "\""
    case n:Node => n.toString + props(n)
    case r:Relationship => ":" + r.getType.name + "[" + r.getId + "] " + props(r)
    case i:java.lang.Iterable[Any] => formatIterator(i.asScala.iterator)
    case a:Array[Any] => formatArray(a)
    case _ => value.toString
  } 
    
  def props(pc:PropertyContainer) = {
    val sb = new collection.mutable.StringBuilder("{")
    val keys = pc.getPropertyKeys.iterator.asScala
    while (keys.hasNext) {
      val prop = keys.next()
      sb.append(prop).append(":")
      val o = pc.getProperty(prop)
      sb.append(text(o))
      if (keys.hasNext) {
        sb.append(",")
      }
    }
    sb.append("}")
    sb.toString
  }

  def formatArray(array:Array[Any]) = {
    val sb = new collection.mutable.StringBuilder("[")
    array.zipWithIndex.foreach{ case (e, idx) => 
      sb.append(text(e))
      if(idx < array.length - 1) sb.append(",")
    }
    sb.toString
  }

  def formatIterator(it:Iterator[Any]) = {
    val sb = new collection.mutable.StringBuilder("[")
    while (it.hasNext) {
      sb.append(text(it.next()));
      if (it.hasNext) {
        sb.append(",")
      }
    }
    sb.append("]")
    sb.toString
  }
}
