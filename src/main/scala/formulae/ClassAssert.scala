package formulae

import java.io.PrintWriter
import org.phenoscape.scowl.ClassAssertion
import org.semanticweb.owlapi.model.{OWLAnnotation, OWLClassAssertionAxiom, OWLClassExpression, OWLIndividual}

/**
  * Created by giovannirescia on 27/9/16.
  */
object ClassAssert {
  def classAssert(axiom: OWLClassAssertionAxiom, writer: PrintWriter): Unit = {
    val (xType: String, xIndividual: String) = matchClasses(axiom)
    // TODO: TRANSLATION NOT COMPLETED
    writer.write(s"$xIndividual type: $xType")
  }

  def matchClasses(given: Any): (String, String) = given match {
    case ClassAssertion(p) => (p._2.asOWLClass().getIRI.getShortForm, p._3.asOWLNamedIndividual().getIRI.getShortForm)
  }
}
