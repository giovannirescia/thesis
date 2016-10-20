package ModalLogicFormulaClasses

import scala.collection.mutable

/**
  *
  * Base for a basic modal formula relation
  */
abstract class MLRelation{
  /**
    *
    * @param map A map between signatures and basic propositional symbols and relations
    *            e.g., Map(Father -> P1, Mother -> P2, hasChild -> R3)
    * @return A string of the rendered formula
    */
  def render(map: mutable.Map[String, String]): String = ""
}
case class R(r: String) extends MLRelation{
  override def render(map: mutable.Map[String, String]): String = {
    if (map contains r){
      map.get(r).head
    } else{
      /** Magic... */
      val aux = map.values.filter(z => z.startsWith("R")).map(y => y.split("R")(1).toInt).max
      map += ((r, "R"+(aux+1).toString))
      "R"+(aux+1).toString
    }
  }
}
