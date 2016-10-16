package render

import java.io.PrintWriter
import org.semanticweb.owlapi.model.ClassExpressionType.{OBJECT_INTERSECTION_OF, OBJECT_UNION_OF}
import org.semanticweb.owlapi.model.{OWLClassExpression, OWLObjectPropertyRangeAxiom}
import render.SubClassRenderer.{matchIntersectionOrUnionOf}
/**
  * Created by giovannirescia on 4/10/16.
  */
object ObjectPropertyRange {
  /**
    *
    * @param axiom An OWLObjectPropertyRangeAxiom
    * @param writer A PrintWriter to write
    */
  def objectPropRange(axiom: OWLObjectPropertyRangeAxiom, writer: PrintWriter): Unit ={
    val prop = axiom.getProperty.getNamedProperty.getIRI.getShortForm
    writer.write(s"$prop Range ")
    parseRange(axiom.getRange, writer)
  }
  /**
    *
    * @param axiom An OWLClassExpression to inspect
    *              The only axiom types will be Object Union Of, and
    *              Object Intersection Of.
    * @param writer A PrintWriter to write
    */
  def parseRange(axiom: OWLClassExpression, writer: PrintWriter): Unit ={
    val expType = axiom.getClassExpressionType
    if(!axiom.isAnonymous){
      writer.write(axiom.asOWLClass().getIRI.getShortForm)
    }
    else {
      writer.write("(")
      if(expType == OBJECT_UNION_OF) {
        val xs = matchIntersectionOrUnionOf(axiom)
        for (x <- xs.take(xs.size - 1)) {
          parseRange(x, writer)
          writer.write(" or ")
        }
        parseRange(xs.last, writer)
      }else if (expType == OBJECT_INTERSECTION_OF) {
        val xs = matchIntersectionOrUnionOf(axiom)
        for (x <- xs.take(xs.size - 1)) {
          parseRange(x, writer)
          writer.write(" and ")
       }
        parseRange(xs.last, writer)
      }
      writer.write(")")
    }
  }
}