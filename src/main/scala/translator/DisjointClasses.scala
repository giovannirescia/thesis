package translator

import org.semanticweb.owlapi.model.{OWLClassExpression, OWLDisjointClassesAxiom}
import ModalLogicFormulaClasses.{A, Iif, Neg, Prop, MLFormula}
import translator.SubClassFormulae.{tailRecursiveAnd, tailRecursiveOr}
import org.phenoscape.scowl.{ObjectIntersectionOf, ObjectUnionOf}


object DisjointClasses {
  /**
    * 
    * The Modal Logic formula version of a Disjoint Classes Axiom
    * @param axiom The axiom to translate
    * @return A Modal Logic formula 
    */
  def disjClass(axiom: OWLDisjointClassesAxiom) : MLFormula = {
    val xs = axiom.getClassExpressionsAsList
    A(Iif(parse(xs.get(0)), Neg(parse(xs.get(1)))))
  }
  /**
    * Recursively navigate through the expressions to construct the Modal Logic formula
    * @param exp An OWLClassExpression
    * @retun A Modal Logic formula
    */
  def parse(exp: OWLClassExpression) : MLFormula = exp match {
    case ObjectUnionOf(xs) => tailRecursiveOr(xs.toList, "")
    case ObjectIntersectionOf(xs) => tailRecursiveAnd(xs.toList, "")
    case _ => Prop(exp.asOWLClass().getIRI.getShortForm)
  }
}
