package render

import java.io.{File, FileOutputStream, PrintWriter, StringWriter}
import java.util.HashMap

import scala.collection.JavaConverters._
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxObjectRenderer
import org.semanticweb.owlapi.model.{OWLAxiom, OWLAxiomVisitor, OWLOntologySetProvider}
import org.semanticweb.owlapi.util.AnnotationValueShortFormProvider

/**
  * Created by giovannirescia on 20/9/16.
  */
object ManchesterRenderer {
  /**
    * @param axioms Axiom to render
    * @param context Ontoloyy provider, e.g. OWLOntologyManager
    * @return
    */
  def renderManchesterSyntax(axioms: List[OWLAxiom], context: OWLOntologySetProvider, output: String): Unit = {
    val outputFile = new PrintWriter(new FileOutputStream(new File(s"/Users/giovannirescia/coding/tesis/output/rendered/manchester/new/$output.txt"),false))
    val writer = new StringWriter()
    val rdfsLabel = OWLManager.getOWLDataFactory.getRDFSLabel
    val labelProvider = new AnnotationValueShortFormProvider(List(rdfsLabel).asJava, new HashMap(), context)
    val renderer = new ManchesterOWLSyntaxObjectRenderer(writer, labelProvider)
    var i = 0
    for (axiom<-axioms) {
      writer.write(i + "\n")
      writer.write(axiom.toString + "\n\n")
      axiom.accept(renderer: OWLAxiomVisitor)
      writer.write("\n\n" +"-------------------------------------\n\n")
      i += 1
    }
    writer.close()
    outputFile.write(writer.toString)
    outputFile.close()
  }
}
