import java.io.{File, FileOutputStream, PrintWriter}

import scala.collection.JavaConversions._
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model._
import org.semanticweb.owlapi.model.parameters.Imports
import renders.LabelMaker._
import translators.TranslatorManager.translate
import renders.RendererManager.render
import renders.ManchesterRenderer.renderManchSyn
import renders.ObjectPropertyRange.objectPropRange

val manager = OWLManager.createOWLOntologyManager

val file1 = new File("/Users/giovannirescia/coding/tesis/ontologies/family_example.owl")
val file2 = new File("/Users/giovannirescia/coding/tesis/ontologies/galen.owl")
val file3 = new File("/Users/giovannirescia/coding/tesis/ontologies/dolce.owl")
val file4 = new File("/Users/giovannirescia/coding/tesis/ontologies/wine_3.owl")

val familyOntology = manager.loadOntologyFromOntologyDocument(file1)
val galenOntology = manager.loadOntologyFromOntologyDocument(file2)
val dolceOntology = manager.loadOntologyFromOntologyDocument(file3)
val wineOntology = manager.loadOntologyFromOntologyDocument(file4)


val xs = dolceOntology.getTBoxAxioms(Imports.INCLUDED).toList

val writer = new PrintWriter(new FileOutputStream(new File(s"/Users/giovannirescia/coding/tesis/output/old/null2.txt"),false))
//for (x <- xs) writer.write(x +"\n\n"+ renderManchesterSyntax(x, manager)+"\n\n")

val x = xs(374).asInstanceOf[OWLInverseFunctionalObjectPropertyAxiom]

x.getProperty.asOWLObjectProperty().getIRI.getShortForm


val onts: Map[String, OWLOntologyID] = Map(
  "family" -> familyOntology.getOntologyID,
  "galen" -> galenOntology.getOntologyID,
  "dolce" -> dolceOntology.getOntologyID,
  "wine" -> wineOntology.getOntologyID
)
var d = familyOntology.getClassesInSignature(Imports.INCLUDED).toList
//var c = familyOntology.getObjectPropertiesInSignature(Imports.INCLUDED).toList
//var b = familyOntology.getDataPropertiesInSignature(Imports.INCLUDED).toList
//var a = familyOntology.getIndividualsInSignature(Imports.INCLUDED).toList
d.foreach(f => print(f.getIRI.getShortForm + " "))


//// ABox
//translate(manager.getOntology(onts("family")).getABoxAxioms(Imports.EXCLUDED).toList, "test_family_ABox_000", fullPath = true)
//translate(manager.getOntology(onts("wine")).getABoxAxioms(Imports.EXCLUDED).toList, "test_wine_ABox_000", fullPath = true)
//translate(manager.getOntology(onts("galen")).getABoxAxioms(Imports.EXCLUDED).toList, "test_galen_ABox_000", fullPath = true)
//translate(manager.getOntology(onts("dolce")).getABoxAxioms(Imports.EXCLUDED).toList, "test_dolce_ABox_000", fullPath = true)
//// TBox
//translate(manager.getOntology(onts("family")).getTBoxAxioms(Imports.EXCLUDED).toList, "test_family_TBox_000", fullPath = true)
//translate(manager.getOntology(onts("wine")).getTBoxAxioms(Imports.EXCLUDED).toList, "test_wine_TBox_000", fullPath = true)
//translate(manager.getOntology(onts("galen")).getTBoxAxioms(Imports.EXCLUDED).toList, "test_galen_TBox_000", fullPath = true)
//translate(manager.getOntology(onts("dolce")).getTBoxAxioms(Imports.EXCLUDED).toList, "test_dolce_TBox_000", fullPath = true)

render(manager.getOntology(onts("family")).getTBoxAxioms(Imports.EXCLUDED).toList, "test_family_002", fullPath = true)
render(manager.getOntology(onts("galen")).getTBoxAxioms(Imports.EXCLUDED).toList, "test_galen_002", fullPath = true)
render(manager.getOntology(onts("dolce")).getTBoxAxioms(Imports.EXCLUDED).toList, "test_dolce_002", fullPath = true)
render(manager.getOntology(onts("wine")).getTBoxAxioms(Imports.EXCLUDED).toList, "test_wine_3_002", fullPath = true)

// Meant for debug
//render(manager.getOntology(onts("dolce")).getTBoxAxioms(Imports.EXCLUDED).toList, "WWWWWW", fullPath = true, verbose = false)
