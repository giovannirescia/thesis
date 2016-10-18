package render

import java.io.PrintWriter
import scala.collection.JavaConversions._
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom

/**
  * Created by giovannirescia on 17/10/16.
  */
object ClassAssertion {
  def classAssert(axiom: OWLClassAssertionAxiom, writer: PrintWriter): Unit = {
    writer.write(
    axiom.getClassesInSignature.toList.head.getIRI.getShortForm + " Type " +
    axiom.getIndividual.asOWLNamedIndividual().getIRI.getShortForm
    )
  }
}
