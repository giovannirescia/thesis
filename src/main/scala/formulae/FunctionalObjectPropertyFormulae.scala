package formulae
import FormClass.{A, Impl, Form, Top, Diam, R, Box}
import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom

/**
  * Created by giovannirescia on 26/9/16.
  */
object FunctionalObjectPropertyFormulae {
  def funcProp(axiom: OWLFunctionalObjectPropertyAxiom): Form ={
    val prop = axiom.getProperty.asOWLObjectProperty().getIRI.getShortForm
    A(Impl(Diam(R(prop), f = Top()), Box(R(prop), f = Top())))
  }
}