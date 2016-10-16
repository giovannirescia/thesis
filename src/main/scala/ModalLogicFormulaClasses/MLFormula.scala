package ModalLogicFormulaClasses

import scala.collection.mutable

/**
  *
  * Base for all the modal logic formulas
  */
abstract class MLFormula {
  /**
    *
    * Render a formula in to IntoHyLo format
    *   [[https://hackage.haskell.org/package/hylolib-1.5.3/docs/src/HyLo-Formula.html#Formula]]
    * @param map A map between signatures and basic propositional symbols and relations
    *            e.g., Map(Father -> P1, Mother -> P2, hasChild -> R3)
    * @return A string of the rendered formula
    */
  def render(map: mutable.Map[String, String]): String = ""
}
case class Prop(arg: String) extends MLFormula {
  override def render(map: mutable.Map[String, String]): String = map.get(arg).head
}
case class And(f1: MLFormula, f2: MLFormula) extends MLFormula{
  override def render(map: mutable.Map[String, String]): String = {
    "( " + f1.render(map) + " ^ " + f2.render(map) + " )"
  }
}
case class Or(f1: MLFormula, f2: MLFormula) extends MLFormula{
  override def render(map: mutable.Map[String, String]): String = {
    "( " + f1.render(map) + " v " + f2.render(map) + " )"
  }
}
case class Impl(f1: MLFormula, f2: MLFormula) extends MLFormula{
  override def render(map: mutable.Map[String, String]): String = {
    "( " + f1.render(map) + " --> " + f2.render(map) + " )"
  }
}
case class Neg(form: MLFormula) extends MLFormula{
  override def render(map: mutable.Map[String, String]): String = {
    "( - " + form.render(map) + " )"
  }
}
case class Diam(r: MLRelation, f: MLFormula) extends MLFormula{
  override def render(map: mutable.Map[String, String]): String = {
      s"( <${r.render(map)}>${f.render(map)} )"
    }
}
case class Box(r: MLRelation, f: MLFormula) extends MLFormula{
  override def render(map: mutable.Map[String, String]): String = {
      s"( [${r.render(map)}]${f.render(map)} )"
    }
}
case class IDiam(r: MLRelation, f: MLFormula) extends MLFormula{
  override def render(map: mutable.Map[String, String]): String = {
      s"( <-${r.render(map)}>${f.render(map)} )"
  }
}
case class IBox(r: MLRelation, f: MLFormula) extends MLFormula{
  override def render(map: mutable.Map[String, String]): String = {
    s"( -[${r.render(map)}]${f.render(map)} )"
    }
}
case class A(f: MLFormula) extends MLFormula{
  override def render(map: mutable.Map[String, String]): String = {
    s"( A${f.render(map)} )"
  }
}
case class Top() extends MLFormula{
  override def render(map: mutable.Map[String, String]): String = {
    " true "
  }
}
case class Bot() extends MLFormula{
  override def render(map: mutable.Map[String, String]): String = {
    " false "
  }
}