package renders

import org.semanticweb.owlapi.functional.renderer.OWLFunctionalSyntaxRenderer
import org.semanticweb.owlapi.model.OWLOntology
import java.io.{File, FileOutputStream, PrintWriter, StringWriter}

/**
  * Created by giovannirescia on 19/9/16.
  */
object FunctionalRenderer{

  /**
    * @param ontology Ontology to render
    * @param name String
    * @return void
    */
  def renderFuncSyn(ontology: OWLOntology, name: String): Unit ={
    val writer = new FileOutputStream("/Users/giovannirescia/coding/tesis/output/" + name + ".txt")
    val f = new OWLFunctionalSyntaxRenderer
    f.render(ontology, writer)
    writer.close()
  }
}
