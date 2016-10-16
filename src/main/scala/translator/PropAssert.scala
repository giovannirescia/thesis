package translator

import java.io.PrintWriter
import org.phenoscape.scowl.ObjectPropertyAssertion
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom

/**
  * Created by giovannirescia on 27/9/16.
  */
object PropAssert {
  def propAssert(axiom: OWLObjectPropertyAssertionAxiom, writer: PrintWriter): Unit = {
    val (prop: String, ind_1: String, ind_2: String) = matchClasses(axiom)
    writer.write(s"@_$ind_1 <$prop> $ind_2")
  }
  def matchClasses(given: OWLObjectPropertyAssertionAxiom): (String, String, String) = given match {
    case ObjectPropertyAssertion(p) =>
      (p._2.asOWLObjectProperty().getIRI.getShortForm,
      p._3.asOWLNamedIndividual().getIRI.getShortForm,
      p._4.asOWLNamedIndividual().getIRI.getShortForm)
  }
}

