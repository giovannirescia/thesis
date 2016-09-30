package formulae
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom
import FormClass.{Impl, Diam, R, Prop, Form, Top}

/**
  * Created by giovannirescia on 25/9/16.
  */
object DataPropertyRange {
  def propRange(axiom: OWLDataPropertyRangeAxiom): Form ={
    val prop = axiom.getProperty.asOWLDataProperty().getIRI.getShortForm
    val range = axiom.getRange.asOWLDatatype().getIRI.getShortForm
    Impl(Diam(R(prop), isInv = true, Top()), Prop(range))
  }
}
