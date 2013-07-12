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
    val jsonMap = pathIterator.take(count).map(p => obj(p))
    Response.ok(compact(render(decompose(jsonMap))), MediaType.APPLICATION_JSON).build()
  }

  def obj(value:Any):Any = value match {
    case Nil => null
    case s:String => "\"" + s + "\""
    case n:Node => Map("id" -> n.getId, "props" -> props(n))
    case r:Relationship => Map("id" -> r.getId, "relType" -> r.getType.name, "props" -> props(r))
    case i:java.lang.Iterable[Any] => i.asScala.map(obj(_))
    case i:Iterable[Any] => i.map(obj(_))
    case a:Array[Any] => a.map(obj(_))
    case _ => value.toString
  }

  def props(pc:PropertyContainer) = {
    var m = Map[String,Any]()
    for (prop <- pc.getPropertyKeys.iterator.asScala) {
      m = m + (prop -> pc.getProperty(prop)) 
    }
    m
  }

}
