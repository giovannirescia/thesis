package render

import java.io.PrintWriter

import org.semanticweb.owlapi.model.ClassExpressionType.{OBJECT_COMPLEMENT_OF, OBJECT_INTERSECTION_OF, OBJECT_UNION_OF}
import org.phenoscape.scowl._
import org.semanticweb.owlapi.model.{OWLClassAssertionAxiom, OWLClassExpression, OWLObjectInverseOf, OWLObjectPropertyExpression}
import render.SubClassRenderer._
import scala.collection.JavaConversions._

object ClassAssertion {


  def classAssert(axiom: OWLClassAssertionAxiom, writer: PrintWriter): Unit = {
    writer.write(
    axiom.getIndividual.asOWLNamedIndividual().getIRI.getShortForm + " Type "
    )
    val exp = axiom.getClassExpression
    parse(exp, writer, axiom)
  }
  def parseProp (e: OWLObjectPropertyExpression, writer: PrintWriter) : Unit = {
    if (!e.isAnonymous) {
      writer.write(e.getNamedProperty.getIRI.getShortForm)
    } else {
      e match {
        // TODO: TRANSLATION MISSING
        case ObjectInverseOf(p) => writer.write(" inverse " + "(" + p.getNamedProperty.getIRI.getShortForm + ")")
      }
    }
  }
  def parse(e: OWLClassExpression, writer: PrintWriter, axiom: OWLClassAssertionAxiom): Unit = {

    if (!e.isAnonymous) {
      writer.write(e.asOWLClass().getIRI.getShortForm)
    } else {
      e match {
        case ObjectUnionOf(xs) => {
          for (x <- xs.take(xs.size-1)){
            parse(x, writer, axiom)
            writer.write(" or ")
          }
          parse(xs.last, writer, axiom)
        }
        case ObjectAllValuesFrom(p, f) => {
          parseProp(p, writer)
          writer.write(" only ")
          parse(f, writer, axiom)
        }
        case ObjectSomeValuesFrom(p, f) => {
          parseProp(p, writer)
          writer.write(" some ")
          writer.write("(")
          parse(f, writer, axiom)
          writer.write(")")
        }
        case ObjectHasValue(p, v) => {
          writer.write(p.asOWLObjectProperty().getIRI.getShortForm + " value " + v.asOWLNamedIndividual().getIRI.getShortForm);
        }
      }
    }
  }
}
