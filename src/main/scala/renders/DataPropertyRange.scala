package renders

import java.io.PrintWriter

import org.semanticweb.owlapi.model.{OWLDataPropertyDomainAxiom, OWLDataPropertyRangeAxiom}

/**
  * Created by giovannirescia on 25/9/16.
  */
object DataPropertyRange {
  def propRange(axiom: OWLDataPropertyRangeAxiom, writer: PrintWriter): Unit ={
    val prop = axiom.getProperty.asOWLDataProperty().getIRI.getShortForm
    val range = axiom.getRange.asOWLDatatype().getIRI.getShortForm
    // 2203: There Exists
    // 2190: ->
    writer.write(s"<~$prop>.T \u2192 $range")
  }

}
