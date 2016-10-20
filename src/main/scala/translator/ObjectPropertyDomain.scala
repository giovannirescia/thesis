package translator

import org.semanticweb.owlapi.model.{OWLClassExpression, OWLObjectPropertyDomainAxiom}
import ModalLogicFormulaClasses.{A, Impl, Diam, R, Top, Prop, MLFormula}
import translator.SubClassFormulae.{tailRecursiveOr, tailRecursiveAnd}
import org.phenoscape.scowl.{ObjectUnionOf, ObjectIntersectionOf}


object ObjectPropertyDomain {
  /**
    * The Modal Logic formula version of a Object Property Range Axiom
    * @param axiom An OWLObjectPropertyDomainAxiom
    * @return A Modal Logic formula of the axiom (which is a Description Logic formula)
    */
  def propDomain(axiom: OWLObjectPropertyDomainAxiom): MLFormula ={
    val prop = axiom.getProperty.asOWLObjectProperty().getIRI.getShortForm
    A(Impl(Diam(R(prop), f = Top()), parseDomain(axiom.getDomain)))
  }
  /**
    * Recursively navigate through the expressions to construct the Modal Logic formula
    * @param exp An OWLClassExpression
    * @retun A Modal Logic formula
    */
  def parseDomain(domain: OWLClassExpression) : MLFormula = domain match {
    case ObjectUnionOf(xs) => tailRecursiveOr(xs.toList, "")
    case ObjectIntersectionOf(xs) => tailRecursiveAnd(xs.toList, "")
    case _ => Prop(domain.asOWLClass().getIRI.getShortForm)
  }
}
