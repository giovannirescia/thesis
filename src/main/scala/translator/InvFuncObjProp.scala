package translator

import ModalLogicFormulaClasses.{MLFormula, A, IBox, R, Top, Impl, IDiam}
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom

/**
  * Created by giovannirescia on 19/10/16.
  */
object InvFuncObjProp {
  def invFunc(axiom: OWLInverseFunctionalObjectPropertyAxiom): MLFormula = {
      A(Impl(IDiam(R(axiom.getProperty.asOWLObjectProperty().getIRI.getShortForm), Top()),
        IBox(R(axiom.getProperty.asOWLObjectProperty().getIRI.getShortForm), Top())))
  }
}
