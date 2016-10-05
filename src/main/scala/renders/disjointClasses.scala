package renders

import java.io.PrintWriter

import org.semanticweb.owlapi.model.{OWLClassExpression, OWLDisjointClassesAxiom}
import org.semanticweb.owlapi.model.ClassExpressionType.{OBJECT_INTERSECTION_OF, OBJECT_UNION_OF}
import renders.SubClassRenderer.{matchIntersectionOf, matchUnionOf}

/**
  * Created by giovannirescia on 4/10/16.
  */
object disjointClasses {
  def disjClass(axiom: OWLDisjointClassesAxiom, writer: PrintWriter): Unit ={
    val xs = axiom.getClassExpressionsAsList
    parse(xs.get(0), writer)
    writer.write(" DisjointWith ")
    parse(xs.get(1), writer)
  }

  def parse(axiom: OWLClassExpression, writer: PrintWriter): Unit ={
    val expType = axiom.getClassExpressionType
    if(!axiom.isAnonymous) writer.write(axiom.asOWLClass().getIRI.getShortForm)
    else {
      writer.write("(")
      if(expType == OBJECT_UNION_OF) {
        val xs = matchUnionOf(axiom)
        for (x <- xs.take(xs.size - 1)) {
          parse(x, writer = writer)
          writer.write(" or ")
        }
        parse(xs.last, writer = writer)
      }else if (expType == OBJECT_INTERSECTION_OF) {
        val xs = matchIntersectionOf(axiom)
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
