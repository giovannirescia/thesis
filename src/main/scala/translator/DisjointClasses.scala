package translator

import org.semanticweb.owlapi.model.{OWLClassExpression, OWLDisjointClassesAxiom}
import ModalLogicFormulaClasses._
import SubClassFormulae.{tailRecursiveAnd, tailRecursiveOr}
import org.phenoscape.scowl._

object DisjointClasses {
  def disjClass(axiom: OWLDisjointClassesAxiom) : MLFormula = {
    val xs = axiom.getClassExpressionsAsList
    A(And(Impl(parse(xs.get(0)), Neg(parse(xs.get(1)))),Impl(Neg(parse(xs.get(1))), parse(xs.get(0)))))
  }

  def parse(exp: OWLClassExpression) : MLFormula = exp match {
    case ObjectUnionOf(xs) => tailRecursiveOr(xs.toList, "")
    case ObjectIntersectionOf(xs) => tailRecursiveAnd(xs.toList, "")
    case _ => Prop(exp.asOWLClass().getIRI.getShortForm)
  }
}