package translator

import ModalLogicFormulaClasses.{A, Impl, MLFormula, Top, Diam, R, Box}
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom


object FunctionalObjectPropertyFormulae {
  /**
    *
    * @param axiom An OWLFunctionalObjectPropertyAxiom
    * @return A Modal Logic formula of the axiom (which is a Description Logic formula)
    */
  def funcProp(axiom: OWLFunctionalObjectPropertyAxiom): MLFormula ={
    val prop = axiom.getProperty.asOWLObjectProperty().getIRI.getShortForm
    A(Impl(Diam(R(prop), f = Top()), Box(R(prop), f = Top())))
  }
}
