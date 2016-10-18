package render

import java.io.PrintWriter

import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom


object DataPropertyAssertion {
  def dataPropAssert(axiom: OWLDataPropertyAssertionAxiom, writer: PrintWriter): Unit = {
    writer.write(
      axiom.getSubject.asOWLNamedIndividual().getIRI.getShortForm + " " +
      axiom.getProperty.asOWLDataProperty().getIRI.getShortForm + " " +
      axiom.getObject.getLiteral
    )
  }
}
