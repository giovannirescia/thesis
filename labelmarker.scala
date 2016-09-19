package foo

import java.io.StringWriter
import java.util.HashMap

import scala.collection.JavaConverters._

import org.semanticweb.owlapi.functional._
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxObjectRenderer
import org.semanticweb.owlapi.model.OWLAxiom
import org.semanticweb.owlapi.model.OWLAxiomVisitor
import org.semanticweb.owlapi.model.OWLOntologySetProvider
import org.semanticweb.owlapi.util.AnnotationValueShortFormProvider

object LabelMaker extends App{

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