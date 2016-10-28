import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.collection.mutable.Map

object Mapper{
  def main(args: Array[String]): Unit = {
    val mapF = args(0)
    val symm = args(1)
    
    val regexp = """[0-9]+""".r
    var xs = ListBuffer[String]()
    var ys = ListBuffer[String]()
    var map = Map[String, String]()
    val mapFile = Source.fromFile(mapF)

    for (line <- mapFile.getLines){
      val k = line.split(" ")(0)
      val v = line.split(" ")(1)
      map += (k -> v)
    }
    for (line <- Source.fromFile(symm).getLines){
      ys += regexp.replaceAllIn(line, r => {val x = map.getOrElse(r.toString, r.toString); x.replaceAll("\\$", "USD")})
    }
    ys.foreach(println)
  }
}
