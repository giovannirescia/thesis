package formulae
import org.semanticweb.owlapi.model.OWLDataPropertyRangeAxiom
import FormClass.{Impl, IDiam, R, Prop, Form, Top}

/**
  * Created by giovannirescia on 25/9/16.
  */
object DataPropertyRange {
  def propRange(axiom: OWLDataPropertyRangeAxiom): Form ={
    val prop = axiom.getProperty.asOWLDataProperty().getIRI.getShortForm
    val range = axiom.getRange.asOWLDatatype().getIRI.getShortForm
    Impl(IDiam(R(prop), Top()), Prop(range))
  }
}
