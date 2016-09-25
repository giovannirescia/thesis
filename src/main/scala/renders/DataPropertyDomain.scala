package renders

import java.io.PrintWriter

import org.semanticweb.owlapi.model.{OWLDataPropertyDomainAxiom, OWLEquivalentClassesAxiom}

/**
  * Created by giovannirescia on 25/9/16.
  */
object DataPropertyDomain {
  def propDomain(axiom: OWLDataPropertyDomainAxiom, writer: PrintWriter): Unit ={
    val prop = axiom.getProperty.asOWLDataProperty().getIRI.getShortForm
    val dom = axiom.getDomain.asOWLClass().getIRI.getShortForm
    // 2203: There Exists
    // 2190: ->
    writer.write(s"<$prop>.T \u2192 $dom")
  }

}
