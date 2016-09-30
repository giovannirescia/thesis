package formulae
import FormClass.Form
import org.semanticweb.owlapi.model.OWLDataPropertyDomainAxiom
import FormClass.{Impl, Diam, R, Top, Prop}
/**
  * Created by giovannirescia on 29/9/16.
  */
object DataPropertyDomain {
  def propDomain(axiom: OWLDataPropertyDomainAxiom): Form ={
    val prop = axiom.getProperty.asOWLDataProperty().getIRI.getShortForm
    val dom = axiom.getDomain.asOWLClass().getIRI.getShortForm
    Impl(Diam(R(prop), f = Top()), Prop(dom))
  }
}