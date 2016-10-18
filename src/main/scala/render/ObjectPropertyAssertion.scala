package render

import java.io.PrintWriter
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom

object ObjectPropertyAssertion {
  /**
    *
    * @param axiom An OWLObjectPropertyAssertionAxiom to render
    * @param writer A writer to write
    */
  def objPropAssert(axiom: OWLObjectPropertyAssertionAxiom, writer: PrintWriter) : Unit = {
    writer.write(axiom.getSubject.asOWLNamedIndividual().getIRI.getShortForm + " " +
      axiom.getProperty.asOWLObjectProperty().getIRI.getShortForm + " " +
      axiom.getObject.asOWLNamedIndividual().getIRI.getShortForm
    )
  }
}
