package render

import java.io.PrintWriter
import org.semanticweb.owlapi.model.{OWLClassExpression, OWLObjectPropertyDomainAxiom}
import org.semanticweb.owlapi.model.ClassExpressionType.{OBJECT_INTERSECTION_OF, OBJECT_UNION_OF}
import render.SubClassRenderer.{matchIntersectionOrUnionOf}


object ObjectPropertyDomain {
  /**
    *
    * @param axiom An OWLObjectPropertyDomainAxiom
    * @param writer A PrintWriter to write
    */
  def objectPropDom(axiom: OWLObjectPropertyDomainAxiom, writer: PrintWriter): Unit ={
    val prop = axiom.getProperty.getNamedProperty.getIRI.getShortForm
    writer.write(s"$prop Domain ")
    parseDomain(axiom.getDomain, writer)
  }
  /**
    *
    * @param axiom An OWLClassExpression to inspect
    *              The only axiom types will be Object Union Of, and
    *              Object Intersection Of.
    * @param writer A PrintWriter to write
    */
  def parseDomain(axiom: OWLClassExpression, writer: PrintWriter): Unit ={
    val expType = axiom.getClassExpressionType
    if(!axiom.isAnonymous){
      writer.write(axiom.asOWLClass().getIRI.getShortForm)
    }
    else {
      writer.write("(")
      if(expType == OBJECT_UNION_OF) {
        val xs = matchIntersectionOrUnionOf(axiom)
        for (x <- xs.take(xs.size - 1)) {
          parseDomain(x, writer)
          writer.write(" or ")
        }
        parseDomain(xs.last, writer)
      }else if (expType == OBJECT_INTERSECTION_OF) {
        val xs = matchIntersectionOrUnionOf(axiom)
        for (x <- xs.take(xs.size - 1)) {
          parseDomain(x, writer)
          writer.write(" and ")
        }
        parseDomain(xs.last, writer)
      }
      writer.write(")")
    }
  }
}