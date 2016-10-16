package render

import org.semanticweb.owlapi.functional.renderer.OWLFunctionalSyntaxRenderer
import org.semanticweb.owlapi.model.OWLOntology
import java.io.{FileOutputStream}

object FunctionalRenderer{

  /**
    *
    * @param ontology Ontology to render
    * @param name String
    */
  def renderFuncSyn(ontology: OWLOntology, name: String): Unit ={
    val writer = new FileOutputStream("/Users/giovannirescia/coding/tesis/output/old" + name + ".txt")
    val f = new OWLFunctionalSyntaxRenderer
    f.render(ontology, writer)
    writer.close()
  }
}
