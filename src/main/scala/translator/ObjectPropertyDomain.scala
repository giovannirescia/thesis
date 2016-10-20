package translator

import org.semanticweb.owlapi.model.{OWLClassExpression, OWLObjectPropertyDomainAxiom}
import ModalLogicFormulaClasses._
import translator.SubClassFormulae.{tailRecursiveOr, tailRecursiveAnd}
import org.phenoscape.scowl._

object ObjectPropertyDomain {
  /**
    * 
    * @param axiom An OWLDATAPropertyDomainAxiom
    * @return A Modal Logic formula of the axiom (which is a Description Logic formula)
    */
  def propDomain(axiom: OWLObjectPropertyDomainAxiom): MLFormula ={
    val prop = axiom.getProperty.asOWLObjectProperty().getIRI.getShortForm
    A(Impl(Diam(R(prop), f = Top()), parseDomain(axiom.getDomain)))
  }

  def parseDomain(domain: OWLClassExpression) : MLFormula = domain match {
    case ObjectUnionOf(xs) => tailRecursiveOr(xs.toList, "")
    case ObjectIntersectionOf(xs) => tailRecursiveAnd(xs.toList, "")
    case _ => Prop(domain.asOWLClass().getIRI.getShortForm)
  }
}
