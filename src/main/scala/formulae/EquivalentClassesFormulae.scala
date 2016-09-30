package formulae

import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom
import formulae.SubClassFormulae.subClass
import FormClass.{And, Form}


/**
  * Created by giovannirescia on 26/9/16.
  */
object EquivalentClassesFormulae {
  /**
    *
    * @param axiom An OWL Equivalent Classes Axiom List
    * @return a Formula
    */
  def equivClasses(axiom: OWLEquivalentClassesAxiom): Form ={
    val listExpr = axiom.getClassExpressionsAsList
    val lhs = listExpr.get(0)
    val rhs = listExpr.get(1)
    And(subClass(lhs, rhs, axiom), subClass(rhs, lhs, axiom))
  }
}
