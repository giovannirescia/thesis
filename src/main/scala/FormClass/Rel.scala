package FormClass

import scala.collection.mutable

/**
  * Created by giovannirescia on 28/9/16.
  */
abstract class Rel{
  def render(map: mutable.Map[String, String]): String = ""
}
case class R(r: String) extends Rel{
  override def render(map: mutable.Map[String, String]): String = {
    map.get(r).head
  }
}