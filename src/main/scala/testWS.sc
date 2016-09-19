import org.semanticweb.owlapi.model.{OWLAxiom, OWLClassExpression}
import scala.collection.JavaConversions._
import java.io.File

import demo.Peperoni
import demo.LabelMaker.renderManchesterSyntax
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.AxiomType._
val file = new File("/Users/giovannirescia/family_example.owl")

val manager = OWLManager.createOWLOntologyManager
val ontology = manager.loadOntologyFromOntologyDocument(file)

val eqclax = ontology.getAxioms(EQUIVALENT_CLASSES)

val hello = new Peperoni

for (ax <- eqclax) println(renderManchesterSyntax(ax, manager)+"\n")