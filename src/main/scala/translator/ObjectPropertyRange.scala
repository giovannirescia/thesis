package translator

import org.semanticweb.owlapi.model.{OWLClassExpression, OWLObjectPropertyRangeAxiom}
import ModalLogicFormulaClasses.{A, Impl, IDiam, R, Top, MLFormula, Prop}
import translator.SubClassFormulae.{tailRecursiveAnd, tailRecursiveOr}
import org.phenoscape.scowl.{ObjectIntersectionOf, ObjectUnionOf}


object ObjectPropertyRange {
  /**
    * The Modal Logic formula version of a Object Property Range Axiom
    * @param axiom An OWLObjectPropertyRangeAxiom 
    * @return A Modal Logic formula of the axiom (which is a Description Logic formula)
    */
  def propRange(axiom: OWLObjectPropertyRangeAxiom): MLFormula ={
    val prop = axiom.getProperty.asOWLObjectProperty().getIRI.getShortForm
    A(Impl(IDiam(R(prop), Top()), parseRange(axiom.getRange)))
  }
  /**
    * Recursively navigate through the expressions to construct the Modal Logic formula
    * @param exp An OWLClassExpression
    * @return A Modal Logic formula
    */
  def parseRange(range: OWLClassExpression) : MLFormula = range match {
    case ObjectUnionOf(expressions) => tailRecursiveOr(expressions.toList, "")
    case ObjectIntersectionOf(expressions) => tailRecursiveAnd(expressions.toList, "")
    case _ => Prop(range.asOWLClass().getIRI.getShortForm)
  }
}
