package renders

import java.io.{File, PrintWriter, StringWriter}
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
    * @param axiom Axiom to render
    * @param context Ontoloyy provider, e.g. OWLOntologyManager
    * @return
    */
  def renderManchSyn(axiom: OWLAxiom, context: OWLOntologySetProvider, outputName: String): Unit ={
    val ManchSynTarget = new PrintWriter(new File("/Users/giovannirescia/coding/tesis/output/" + outputName + ".txt"))
    val writer = new StringWriter()
    val rdfsLabel = OWLManager.getOWLDataFactory.getRDFSLabel
    val labelProvider = new AnnotationValueShortFormProvider(List(rdfsLabel).asJava, new HashMap(), context)
    val renderer = new ManchesterOWLSyntaxObjectRenderer(writer, labelProvider)
    axiom.accept(renderer: OWLAxiomVisitor)
    writer.close()
    ManchSynTarget.write(writer.toString.split(" ").foreach(println).toString)
    ManchSynTarget.close()
  }

}
