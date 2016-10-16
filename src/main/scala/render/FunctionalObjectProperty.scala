package render

import java.io.PrintWriter
import org.semanticweb.owlapi.model.{OWLFunctionalObjectPropertyAxiom}

object FunctionalObjectProperty {
  /**
    *
    * @param axiom An OWLFunctionalObjectPropertyAxiom
    * @param writer A PrintWriter to write
    */
  def funcProp(axiom: OWLFunctionalObjectPropertyAxiom, writer: PrintWriter): Unit ={
    val prop = axiom.getProperty.asOWLObjectProperty().getIRI.getShortForm
    // 2192: ->
    writer.write(s"A(<$prop>T \u2192 [$prop]T)")
  }
}