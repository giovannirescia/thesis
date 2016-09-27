import org.semanticweb.owlapi.model.{OWLObjectIntersectionOf, _}
import org.phenoscape.scowl._
import scala.collection.JavaConversions._
import java.io.{File, FileOutputStream, PrintWriter, StringWriter}
import org.semanticweb.owlapi.model.parameters.Imports
import renders.LabelMaker.renderManchesterSyntax
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.ClassExpressionType.{OBJECT_INTERSECTION_OF,OBJECT_SOME_VALUES_FROM, OBJECT_HAS_VALUE, OBJECT_ALL_VALUES_FROM}
import translators.TranslatorManager.translate

val manager = OWLManager.createOWLOntologyManager

val file1 = new File("/Users/giovannirescia/coding/tesis/ontologies/family_example.owl")
val file2 = new File("/Users/giovannirescia/coding/tesis/ontologies/galen.owl")
val file3 = new File("/Users/giovannirescia/coding/tesis/ontologies/dolce.owl")
val file4 = new File("/Users/giovannirescia/coding/tesis/ontologies/wine_3.owl")

val familyOntology = manager.loadOntologyFromOntologyDocument(file1)
val galenOntology = manager.loadOntologyFromOntologyDocument(file2)
val dolceOntology = manager.loadOntologyFromOntologyDocument(file3)
val wineOntology = manager.loadOntologyFromOntologyDocument(file4)

val ontologies: List[OWLOntologyID] = List(familyOntology.getOntologyID, galenOntology.getOntologyID, dolceOntology.getOntologyID, wineOntology.getOntologyID)
val onts: Map[String, OWLOntologyID] = Map(
  "family" -> familyOntology.getOntologyID,
  "galen" -> galenOntology.getOntologyID,
  "dolce" -> dolceOntology.getOntologyID,
  "wine" -> wineOntology.getOntologyID
  )

val abox = manager.getOntology(onts("family")).getABoxAxioms(Imports.INCLUDED).toList


val l = abox(1).asInstanceOf[OWLObjectPropertyAssertionAxiom]

l.getProperty
val a = l.getAnonymousIndividuals


def matchClasses(given: Any): (Any,  Any, String) = given match {
  case ObjectPropertyAssertion(p) => (p._1, p._2, p._3.getIndividualsInSignature.head.getIRI.getShortForm)
}


val writer = new PrintWriter(new FileOutputStream(new File(s"/Users/giovannirescia/coding/tesis/output/old/null.txt"),false))
for (x <- abox) writer.write(renderManchesterSyntax(x, manager)+"\n\n")
writer.close()

//val ontology1 = manager.loadOntologyFromOntologyDocument(file1)
//val factory = manager.getOWLDataFactory
//val equiv_class = ontology.getAxioms(EQUIVALENT_CLASSES)
//val sub_class = ontology.getAxioms(SUBCLASS_OF).toList



//val tbox = ontology.getTBoxAxioms(Imports.EXCLUDED).toList
//val l = tbox(14)
//
//val l = tbox(740)

//
//val (_, x, z) = matchClasses(l)
//
//val y = z.asInstanceOf[OWLObjectAllValuesFrom]
//
//y.getFiller
//y.getProperty

//simpleSubClass(l.asInstanceOf[OWLSubClassOfAxiom], writer)
//for (x <- tbox) writer.write(renderManchesterSyntax(x, manager)+"\n\n")
//
//



//def pepe(given: Any): (Set[OWLAnnotation], OWLClassExpression, OWLClassExpression) = given match {
//  // p: Property
//  // f: Filler
//  case SubClassOf(p) => p
//}
//
//

//for (t <- tbox) println(t)
////
//val l = tbox(15)
//
//var (x, y, z) = pepe(l)
//
//y.asOWLClass().getIRI.getShortForm
//z.asOWLClass().getIRI.getShortForm

//l.getProperty.asOWLObjectProperty().getIRI.getShortForm

//l.getProperty.asOWLDataProperty().getIRI.getShortForm
//l.getDomain.asOWLClass().getIRI.getShortForm
//var s: Set[String] = Set.empty
//s = s.+("hola peperino")
//s = s.+("hola peperino")
//s = s.+("hola peperina")
//s += "dasd"
//s
//
//var ax1 = equiv_class.toList
//
//equivClasses(ax1, "test01")

//val exprAx1 = ax1.getClassExpressionsAsList
////
////// Left hand side
//////
//////val lhs = exprAx1(0)
//////if (! lhs.isAnonymous){
//////  println("its class is: " + lhs.asOWLClass().getIRI.getShortForm)
//////}else{println("is not class")}
////////
//////// Right hand side
//val rhs = exprAx1(1).asConjunctSet().toList
////rhs.tail
//////
//////if (rhs.getClassExpressionType == OBJECT_INTERSECTION_OF){
//////  println("Hurray!")
//////}
//// left hand side
//var a1 = rhs(0)
//// right hand side
//var aux = rhs.tail
//var a2: OWLObjectSomeValuesFrom = rhs.asConjunctSet().toList(1).asInstanceOf[OWLObjectSomeValuesFrom]

//if(!a1.isAnonymous){
//  println("its class is: "+ a1.asOWLClass().getIRI.getShortForm)
//}
//a2.getFiller.asOWLClass().getIRI.getShortForm
//a2.getProperty.asOWLObjectProperty.getIRI.getShortForm
//a2.getObjectPropertiesInSignature
//a2.getDataPropertiesInSignature
////val oev = OWLClassExpressionVisitor
////a2.accept(oev)
//
////def hola match{
////  case OWLObjectSomeValuesFrom => print("hola")
////  case _ => print("chau")
////}
//
//aux.getClassExpressionType

//def matchSomeValuesFrom(given: OWLClassExpression): Tuple2[OWLObjectPropertyExpression, OWLClassExpression] = given match{
//  case ObjectSomeValuesFrom(p, f) => (p, f.asInstanceOf[OWLClassExpression])
//}
//def matchHasValue(given: OWLClassExpression): Tuple2[OWLObjectPropertyExpression, OWLNamedIndividual] = given match{
//  case ObjectHasValue(pp, ff) => (pp, ff.asInstanceOf[OWLNamedIndividual])
//}
////
//val y = aux(1)
//
//val p = matchHasValue(y)



//val x = matchHasValue(y)._2
//x.getIRI.getShortForm
//
//def matchIntersectionOF(given: OWLClassExpression):List[OWLClassExpression] = given match{
//  case ObjectIntersectionOf(p) => p.toList
//}
//
//val myt = matchSomeValuesFrom(aux)
//
//myt._1.asOWLObjectProperty.getIRI.getShortForm
//myt._2.asOWLClass.getIRI.getShortForm
//
//var s = rhs.asInstanceOf[OWLObjectIntersectionOf].getOperandsAsList
//
//var p = matchIntersectionOF(rhs)
//
//p(0).isAnonymous
//matchSomeValuesFrom(p(1))._1.asOWLObjectProperty().getIRI.getShortForm
//
//p(1).getClassExpressionType
