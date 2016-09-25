import org.semanticweb.owlapi.model.{OWLObjectIntersectionOf, _}
import org.phenoscape.scowl._
import scala.collection.JavaConversions._
import java.io.{File, FileOutputStream, PrintWriter, StringWriter}
//import renders.EquivalentClassesRenderer.equivClasses
import org.semanticweb.owlapi.model.parameters.Imports
import org.semanticweb.owlapi.functional.renderer.OWLFunctionalSyntaxRenderer
import renders.LabelMaker.renderManchesterSyntax
import renders.FunctionalRenderer.{renderFuncSyn => renderizador}
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.AxiomType._
import org.semanticweb.owlapi.model.AxiomType.DATA_PROPERTY_DOMAIN
import org.semanticweb.owlapi.util.DefaultPrefixManager
import org.semanticweb.owlapi.model.ClassExpressionType.{OBJECT_INTERSECTION_OF,OBJECT_SOME_VALUES_FROM, OBJECT_HAS_VALUE, OBJECT_ALL_VALUES_FROM}
val file1 = new File("/Users/giovannirescia/coding/tesis/ontologies/family_example.owl")
val file2 = new File("/Users/giovannirescia/coding/tesis/ontologies/galen.owl")
val file3 = new File("/Users/giovannirescia/coding/tesis/ontologies/dolce.owl")
val file4 = new File("/Users/giovannirescia/coding/tesis/ontologies/wine_3.owl")

val manager1 = OWLManager.createOWLOntologyManager
val manager2 = OWLManager.createOWLOntologyManager
val manager3 = OWLManager.createOWLOntologyManager
val manager4 = OWLManager.createOWLOntologyManager

//val ontology1 = manager.loadOntologyFromOntologyDocument(file1)
//val factory = manager.getOWLDataFactory
//val equiv_class = ontology.getAxioms(EQUIVALENT_CLASSES)
//val sub_class = ontology.getAxioms(SUBCLASS_OF).toList

import renders.RendererManager._
import renders.SubClassRenderer._
import renders.LabelMaker.renderManchesterSyntax
//val tbox = ontology.getTBoxAxioms(Imports.EXCLUDED).toList
//val l = tbox(14)
val writer = new PrintWriter(new FileOutputStream(new File(s"/Users/giovannirescia/coding/tesis/output/null.txt"),false))
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
render(manager1.loadOntologyFromOntologyDocument(file1).getTBoxAxioms(Imports.EXCLUDED).toList, "test_family_002")
render(manager2.loadOntologyFromOntologyDocument(file2).getTBoxAxioms(Imports.EXCLUDED).toList, "test_galen_002")
render(manager3.loadOntologyFromOntologyDocument(file3).getTBoxAxioms(Imports.EXCLUDED).toList, "test_dolce_002")
render(manager4.loadOntologyFromOntologyDocument(file4).getTBoxAxioms(Imports.EXCLUDED).toList, "test_wine_3_002")
writer.close()

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
