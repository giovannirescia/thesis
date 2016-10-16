package render

import java.io.PrintWriter
import org.semanticweb.owlapi.model.{OWLDataPropertyRangeAxiom}

object DataPropertyRange {
  /**
    *
    * @param axiom An OWLDataPropertyRangeAxiom to render
    * @param writer A PrintWriter to write
    */
  def propRange(axiom: OWLDataPropertyRangeAxiom, writer: PrintWriter): Unit ={
    val prop = axiom.getProperty.asOWLDataProperty().getIRI.getShortForm
    val range = axiom.getRange.asOWLDatatype().getIRI.getShortForm
    writer.write(s"$prop Range: $range")
  }
}
