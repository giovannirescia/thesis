package translators

import java.io.PrintWriter

import org.semanticweb.owlapi.model.OWLFunctionalObjectPropertyAxiom

/**
  * Created by giovannirescia on 26/9/16.
  */
object FunctionalObjectPropertyTranslator {
  def funcProp(axiom: OWLFunctionalObjectPropertyAxiom, writer: PrintWriter): Unit ={
    val prop = axiom.getProperty.asOWLObjectProperty().getIRI.getShortForm
    // 2192: ->
    writer.write(s"A(<$prop>T \u2192 [$prop]T)")
  }
}
