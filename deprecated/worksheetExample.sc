import org.phenoscape.scowl._
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.{OWLAxiom, OWLClassExpression}
import org.semanticweb.owlapi.model.parameters.Imports
import org.semanticweb.owlapi.model.AxiomType._
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import java.io.File

import java.io.StringWriter
import java.util.HashMap

import scala.collection.JavaConverters._
import org.semanticweb.owlapi.functional.renderer.OWLFunctionalSyntaxRenderer
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxObjectRenderer
import org.semanticweb.owlapi.model.OWLAxiom
import org.semanticweb.owlapi.model.OWLAxiomVisitor
import org.semanticweb.owlapi.model.OWLOntologySetProvider
import org.semanticweb.owlapi.util.AnnotationValueShortFormProvider
import org.semanticweb.owlapi.functional.renderer._

val file = new File("/Users/giovannirescia/family_example.owl")

val manager = OWLManager.createOWLOntologyManager
val ontology = manager.loadOntologyFromOntologyDocument(file)

val eqclax = ontology.getAxioms(EQUIVALENT_CLASSES)

object LabelMaker{

  /**
    * @param axiom Axiom to render
    * @param context Ontoloyy provider, e.g. OWLOntologyManager
    * @return
    */
  def renderManchesterSyntax(axiom: OWLAxiom, context: OWLOntologySetProvider): String = {
    val writer = new StringWriter()
    val rdfsLabel = OWLManager.getOWLDataFactory.getRDFSLabel
    val labelProvider = new AnnotationValueShortFormProvider(List(rdfsLabel).asJava, new HashMap(), context)
    val renderer = new ManchesterOWLSyntaxObjectRenderer(writer, labelProvider)
    axiom.accept(renderer: OWLAxiomVisitor)
    writer.close()
    writer.toString
  }

}


var x = OWLManager.getOWLDataFactory.getRDFSLabel
List(x).asJava

//for (ax <- eqclax) println(LabelMaker.renderManchesterSyntax(ax, manager))