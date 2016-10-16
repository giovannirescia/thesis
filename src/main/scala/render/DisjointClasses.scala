package render

import java.io.PrintWriter
import org.semanticweb.owlapi.model.{OWLClassExpression, OWLDisjointClassesAxiom}
import org.semanticweb.owlapi.model.ClassExpressionType.{OBJECT_INTERSECTION_OF, OBJECT_UNION_OF}
import render.SubClassRenderer.{matchIntersectionOrUnionOf}

object DisjointClasses {
  /**
    *
    * @param axiom An OWLDisjointClassesAxiom
    * @param writer A PrintWriter to write on
    */
  def disjClass(axiom: OWLDisjointClassesAxiom, writer: PrintWriter): Unit ={
    val xs = axiom.getClassExpressionsAsList
    parse(xs.get(0), writer)
    writer.write(" DisjointWith ")
    parse(xs.get(1), writer)
  }

  /**
    *
    * @param axiom An OWLClassExpression to inspect
    *              Since it's called from a disjoint classes method,
    *              the only axiom types will be Object Union Of, and
    *              Object Intersection Of.
    * @param writer A PrintWriter to write
    */
  def parse(axiom: OWLClassExpression, writer: PrintWriter): Unit ={
    val expType = axiom.getClassExpressionType
    if(!axiom.isAnonymous) {
      writer.write(axiom.asOWLClass().getIRI.getShortForm)
    }
    else {
      writer.write("(")
      if(expType == OBJECT_UNION_OF) {
        /** Get all its subparts */
        val xs = matchIntersectionOrUnionOf(axiom)
        for (x <- xs.take(xs.size - 1)) {
          parse(x, writer = writer)
          writer.write(" or ")
        }
        parse(xs.last, writer = writer)
      }else if (expType == OBJECT_INTERSECTION_OF) {
        /** Get all its subparts */
        val xs = matchIntersectionOrUnionOf(axiom)
        for (x <- xs.take(xs.size - 1)) {
          parse(x, writer = writer)
          writer.write(" and ")
        }
        parse(xs.last, writer = writer)
      }
      writer.write(")")
    }
  }
}
