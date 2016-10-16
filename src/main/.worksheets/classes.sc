import java.io.File
import scala.collection.JavaConversions._
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.{OWLAxiom, OWLEquivalentClassesAxiom, OWLOntologyID}
import formulae._
import org.semanticweb.owlapi.model.parameters.Imports
import formulae.FormulaeManager.formulate
import FormClass._
import SubClassFormulae._
val manager = OWLManager.createOWLOntologyManager

val file1 = new File("/Users/giovannirescia/coding/tesis/ontologies/family_example.owl")

val familyOntology = manager.loadOntologyFromOntologyDocument(file1)

val onts: Map[String, OWLOntologyID] = Map(
  "family" -> familyOntology.getOntologyID
)
val l = familyOntology.getTBoxAxioms(Imports.INCLUDED).toList
l.size
var a = l(2)


var b = a.asInstanceOf[OWLEquivalentClassesAxiom].getClassExpressionsAsList
var c = b(1)

var d = matchIntersectionOf(c)

EquivalentClassesFormulae.equivClasses(a.asInstanceOf[OWLEquivalentClassesAxiom]).render()