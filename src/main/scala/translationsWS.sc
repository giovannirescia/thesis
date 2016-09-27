import java.io.File
import scala.collection.JavaConversions._
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.OWLOntologyID
import org.semanticweb.owlapi.model.parameters.Imports
import translators.TranslatorManager.translate
import renders.RendererManager.render

val manager = OWLManager.createOWLOntologyManager

val file1 = new File("/Users/giovannirescia/coding/tesis/ontologies/family_example.owl")
val file2 = new File("/Users/giovannirescia/coding/tesis/ontologies/galen.owl")
val file3 = new File("/Users/giovannirescia/coding/tesis/ontologies/dolce.owl")
val file4 = new File("/Users/giovannirescia/coding/tesis/ontologies/wine_3.owl")

val familyOntology = manager.loadOntologyFromOntologyDocument(file1)
val galenOntology = manager.loadOntologyFromOntologyDocument(file2)
val dolceOntology = manager.loadOntologyFromOntologyDocument(file3)
val wineOntology = manager.loadOntologyFromOntologyDocument(file4)

val onts: Map[String, OWLOntologyID] = Map(
  "family" -> familyOntology.getOntologyID,
  "galen" -> galenOntology.getOntologyID,
  "dolce" -> dolceOntology.getOntologyID,
  "wine" -> wineOntology.getOntologyID
)

// ABox
translate(manager.getOntology(onts("family")).getABoxAxioms(Imports.EXCLUDED).toList, "test_family_ABox_000")
translate(manager.getOntology(onts("wine")).getABoxAxioms(Imports.EXCLUDED).toList, "test_wine_ABox_000")
translate(manager.getOntology(onts("galen")).getABoxAxioms(Imports.EXCLUDED).toList, "test_galen_ABox_000")
translate(manager.getOntology(onts("dolce")).getABoxAxioms(Imports.EXCLUDED).toList, "test_dolce_ABox_000")
// TBox
translate(manager.getOntology(onts("family")).getTBoxAxioms(Imports.EXCLUDED).toList, "test_family_TBox_000")
translate(manager.getOntology(onts("wine")).getTBoxAxioms(Imports.EXCLUDED).toList, "test_wine_TBox_000")
translate(manager.getOntology(onts("galen")).getTBoxAxioms(Imports.EXCLUDED).toList, "test_galen_TBox_000")
translate(manager.getOntology(onts("dolce")).getTBoxAxioms(Imports.EXCLUDED).toList, "test_dolce_TBox_000")

//render(manager.getOntology(onts("family")).getTBoxAxioms(Imports.EXCLUDED).toList, "test_family_002")
//render(manager.getOntology(onts("galen")).getTBoxAxioms(Imports.EXCLUDED).toList, "test_galen_002")
//render(manager.getOntology(onts("dolce")).getTBoxAxioms(Imports.EXCLUDED).toList, "test_dolce_002")
//render(manager.getOntology(onts("wine")).getTBoxAxioms(Imports.EXCLUDED).toList, "test_wine_3_002")

