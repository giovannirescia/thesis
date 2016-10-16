package render

import java.io.PrintWriter
import org.semanticweb.owlapi.model.{OWLDataPropertyDomainAxiom}

object DataPropertyDomain {
  /**
    *
    * @param axiom An OWLDataPropertyDomainAxiom to render
    * @param writer A PrintWriter to write
    */
  def propDomain(axiom: OWLDataPropertyDomainAxiom, writer: PrintWriter): Unit ={
    val prop = axiom.getProperty.asOWLDataProperty().getIRI.getShortForm
    val dom = axiom.getDomain.asOWLClass().getIRI.getShortForm
    writer.write(s"$prop Domain: $dom")
  }
}
