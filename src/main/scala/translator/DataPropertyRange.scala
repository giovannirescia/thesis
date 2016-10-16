package translator

import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom
import ModalLogicFormulaClasses.{Impl, IDiam, R, Prop, MLFormula, Top}


object DataPropertyRange {
  /**
    * 
    * @param axiom An OWLDataPropertyRangeAxiom 
    * @return A Modal Logic formula of the axiom (which is a Description Logic formula)
    */
  def propRange(axiom: OWLDataPropertyRangeAxiom): MLFormula ={
    val prop = axiom.getProperty.asOWLDataProperty().getIRI.getShortForm
    val range = axiom.getRange.asOWLDatatype().getIRI.getShortForm
    Impl(IDiam(R(prop), Top()), Prop(range))
  }
}
