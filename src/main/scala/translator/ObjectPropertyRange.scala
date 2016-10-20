package translator

import org.semanticweb.owlapi.model.{OWLClassExpression, OWLObjectPropertyRangeAxiom}
import ModalLogicFormulaClasses._
import SubClassFormulae.{tailRecursiveAnd, tailRecursiveOr}
import org.phenoscape.scowl._

object ObjectPropertyRange {
  /**
    * 
    * @param axiom An OWLDataPropertyRangeAxiom 
    * @return A Modal Logic formula of the axiom (which is a Description Logic formula)
    */
  def propRange(axiom: OWLObjectPropertyRangeAxiom): MLFormula ={
    val prop = axiom.getProperty.asOWLObjectProperty().getIRI.getShortForm
    A(Impl(IDiam(R(prop), Top()), parseRange(axiom.getRange)))
  }

  def parseRange(range: OWLClassExpression) : MLFormula = range match {
    case ObjectUnionOf(xs) => tailRecursiveOr(xs.toList, "")
    case ObjectIntersectionOf(xs) => tailRecursiveAnd(xs.toList, "")
    case _ => Prop(range.asOWLClass().getIRI.getShortForm)
  }
}