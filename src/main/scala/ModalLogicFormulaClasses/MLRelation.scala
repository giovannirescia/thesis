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
    map.get(r).head
  }
}