import java.io.File
import scala.collection.JavaConversions._
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.{OWLAxiom, OWLEquivalentClassesAxiom, OWLOntologyID}
import formulae._
import org.semanticweb.owlapi.model.parameters.Imports
import formulae.FormulaeManager.{formulate, render}
import FormClass._
import SubClassFormulae._
import scala.collection.mutable.ListBuffer


val manager = OWLManager.createOWLOntologyManager
val file1 = new File("/Users/giovannirescia/coding/tesis/ontologies/family_example.owl")
val familyOntology = manager.loadOntologyFromOntologyDocument(file1)

val l = familyOntology.getTBoxAxioms(Imports.INCLUDED).toList

val xs = formulate(l)

val x = xs(0)
//render(ListBuffer[Form](x), familyOntology, "test-family")
render(xs, familyOntology, "test-family")


//var a = l(2)
//val f: Form = EquivalentClassesFormulae.equivClasses(a.asInstanceOf[OWLEquivalentClassesAxiom])
//
//var classes = familyOntology.getClassesInSignature(Imports.INCLUDED).toList
//val individuals = familyOntology.getIndividualsInSignature(Imports.INCLUDED).toList
//val objprop = familyOntology.getObjectPropertiesInSignature(Imports.INCLUDED).toList
//val props = new ListBuffer[String]()
//val rels = new ListBuffer[String]()
//
//for (x <- objprop){
//  rels += x.getIRI.getShortForm
//}
//
//for (x <- classes ++ individuals){
//  props += x.getIRI.getShortForm
//}
//
//
//var propMap: collection.mutable.Map[String, String] = collection.mutable.Map.empty
//var relMap: collection.mutable.Map[String, String] = collection.mutable.Map.empty
//
//var i = 1
//var j = 1
//
//for (x <- rels){
//  relMap += ((x.toString, "R"+j.toString))
//  j += 1
//}
////for (x <- xs){
////  print(x, "P"+i)
////  i += 1
////}
//
//for (x <- props){
//  propMap += ((x.toString , "P"+i.toString))
// // map update (x.toString , "P"+i.toString)
//  i += 1
//}
//
//for ((k, v) <- relMap){
//  println(k, v)
//}