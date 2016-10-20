package translator

import ModalLogicFormulaClasses.{MLFormula, A, IBox, R, Top, Impl, IDiam}
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom


object InvFuncObjProp {
  /**
    * 
    * The Modal Logic formula version of a Inverse Function Object Property Axiom
    * @param axiom The axiom to translate
    * @return A Modal Logic formula 
    */
  def invFunc(axiom: OWLInverseFunctionalObjectPropertyAxiom): MLFormula = {
      A(Impl(IDiam(R(axiom.getProperty.asOWLObjectProperty().getIRI.getShortForm), Top()),
        IBox(R(axiom.getProperty.asOWLObjectProperty().getIRI.getShortForm), Top())))
  }
}
