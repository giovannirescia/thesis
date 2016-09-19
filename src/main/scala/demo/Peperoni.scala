package demo

import java.io.StringWriter
import java.util.HashMap
import scala.collection.JavaConverters._

import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxObjectRenderer
import org.semanticweb.owlapi.model.{OWLAxiom, OWLAxiomVisitor, OWLOntologySetProvider}
import org.semanticweb.owlapi.util.AnnotationValueShortFormProvider

/**
  * Created by giovannirescia on 18/9/16.
  */
class Peperoni {
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
