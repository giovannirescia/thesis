import java.io.File

import scala.collection.JavaConversions._
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.{OWLAxiom, OWLOntologyID}
import formulae._
import org.semanticweb.owlapi.model.parameters.Imports
import formulae.FormulaeManager.formulate
import FormClass._
val manager = OWLManager.createOWLOntologyManager

val file1 = new File("/Users/giovannirescia/coding/tesis/ontologies/family_example.owl")

val familyOntology = manager.loadOntologyFromOntologyDocument(file1)

val onts: Map[String, OWLOntologyID] = Map(
  "family" -> familyOntology.getOntologyID
)

val l = familyOntology.getTBoxAxioms(Imports.INCLUDED).toList
l.size
var a = l(2)

var x = Prop("hola")
val y = And(x, x)

val z = formulate(l)

for(d <- z) println(d.render())