package translator

import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom
import ModalLogicFormulaClasses.{Impl, Diam, R, Top, Prop, MLFormula}


object DataPropertyDomain {
  /**
    * 
    * @param axiom An OWLDATAPropertyDomainAxiom
    * @return A Modal Logic formula of the axiom (which is a Description Logic formula)
    */
  def propDomain(axiom: OWLDataPropertyDomainAxiom): MLFormula ={
    val prop = axiom.getProperty.asOWLDataProperty().getIRI.getShortForm
    val dom = axiom.getDomain.asOWLClass().getIRI.getShortForm
    Impl(Diam(R(prop), f = Top()), Prop(dom))
  }
}
