package translator

import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom
import translator.SubClassFormulae.subClass
import ModalLogicFormulaClasses.{And, MLFormula}


object EquivalentClassesFormulae {
  /**
    *
    * @param axiom An OWL Equivalent Classes Axiom
    * @return A Modal Logic formula of the axiom (which is a Description Logic formula)
    */
  def equivClasses(axiom: OWLEquivalentClassesAxiom): MLFormula ={
    val listExpr = axiom.getClassExpressionsAsList
    val lhs = listExpr.get(0)
    val rhs = listExpr.get(1)
    And(subClass(lhs, rhs, axiom), subClass(rhs, lhs, axiom))
  }
}
