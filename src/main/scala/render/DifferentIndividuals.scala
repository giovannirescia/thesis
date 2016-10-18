package render

import java.io.PrintWriter
import scala.collection.JavaConversions._
import org.phenoscape.scowl._
import org.semanticweb.owlapi.model.OWLIndividual
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom

/**
  * Created by giovannirescia on 17/10/16.
  */
object DifferentIndividuals {
  def diffIndiv(axiom: OWLDifferentIndividualsAxiom, writer: PrintWriter): Unit = {
    val xs = axiom.getIndividualsAsList
    writer.write("DifferentIndividuals: ")
    for (individual <- xs.take(xs.size()-1) ){
      writer.write(individual.asOWLNamedIndividual().getIRI.getShortForm + ", ")
    }
    writer.write(xs.last.asOWLNamedIndividual().getIRI.getShortForm)
  }
}
