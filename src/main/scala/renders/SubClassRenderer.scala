package renders

import java.io.PrintWriter

import org.phenoscape.scowl._
import org.semanticweb.owlapi.model.ClassExpressionType.{OBJECT_HAS_VALUE, OBJECT_SOME_VALUES_FROM, OBJECT_INTERSECTION_OF}
import org.semanticweb.owlapi.model._
/**
  * Created by giovannirescia on 24/9/16.
  */
object SubClassRenderer {
  // Only for simple subclass axioms
  def simpleSubClass(axiom: OWLSubClassOfAxiom, writer: PrintWriter): Unit = {
    val (_, x, y) = matchClasses(axiom)

    subClass(x.asInstanceOf[OWLClassExpression], y.asInstanceOf[OWLClassExpression], writer)

//    writer.write(" [ ")
//    writer.write(x.asOWLClass().getIRI.getShortForm)
//    writer.write(" \u2286 ")
//    writer.write(y.asOWLClass().getIRI.getShortForm)
//    writer.write(" ] ")
  }

  // Only for equivalence classes
  def subClass(lhs: OWLClassExpression, rhs: OWLClassExpression, writer: PrintWriter): Unit = {
    writer.write(" [ ")

    if (!lhs.isAnonymous) {
      writer.write(lhs.asOWLClass().getIRI.getShortForm)
    }else{
      inspect(lhs, writer)
    }
    // 2286: Subset ( <= )
    writer.write(" \u2286 ")
    if (!rhs.isAnonymous) {
      writer.write(rhs.asOWLClass().getIRI.getShortForm)
    }
    //else if (rhs.getClassExpressionType == OBJECT_INTERSECTION_OF) {
    else{
      inspect(rhs, writer)
    }
    writer.write(" ] ")

    //writer.write("\n")
  }

  def inspect(exp: OWLClassExpression, writer: PrintWriter): Unit = {
    writer.write("(")

    val expType = exp.getClassExpressionType

    if (expType == OBJECT_SOME_VALUES_FROM){
      val ys = matchSomeValuesFrom(exp)
      val ysLhs = ys._1
      val ysRhs = ys._2
      if(!ysLhs.isAnonymous) {
        writer.write("" + ysLhs.asOWLObjectProperty().getIRI.getShortForm)
      }
      else{
        inspect(ysLhs.asInstanceOf[OWLClassExpression], writer)
      }
      writer.write(" some ")
      if (!ysRhs.isAnonymous){
         writer.write(ysRhs.asOWLClass().getIRI.getShortForm + "")}
      else{
        writer.write("")
        inspect(ysRhs, writer)
        writer.write("")
      }
    }else if (expType == OBJECT_HAS_VALUE){
      val ys = matchHasValue(exp)
      val ysLhs = ys._1
      val ysRhs = ys._2
      if(!ysLhs.isAnonymous) {
        writer.write("" + ysLhs.asOWLObjectProperty().getIRI.getShortForm)
      }else{
        inspect(ysLhs.asInstanceOf[OWLClassExpression], writer)
      }
      writer.write(" value: ")
      if (!ysRhs.isAnonymous){
        writer.write(ysRhs.getIRI.getShortForm + "")
      }else{
        inspect(ysRhs.asInstanceOf[OWLClassExpression], writer)
      }
    }
    else{
      val xs = matchIntersectionOF(exp)
      val xsLhs = xs(0)
      val xsRhs = xs(1)
      if (!xsLhs.isAnonymous) {
        writer.write(xsLhs.asOWLClass().getIRI.getShortForm)
      }
      if (!xsRhs.isAnonymous) {
        writer.write(xsRhs.asOWLClass().getIRI.getShortForm)
      }
      else {
        for (x <- xs.tail) {
          // 2229: Intersection
          writer.write(" \u2229 ")
          if (x.getClassExpressionType == OBJECT_HAS_VALUE) {
             inspect(x, writer)
          } else if (x.getClassExpressionType == OBJECT_SOME_VALUES_FROM) {
             inspect(x, writer)
          }

        }

      }
    }
    writer.write(")")

  }

  def matchSomeValuesFrom(given: OWLClassExpression): Tuple2[OWLObjectPropertyExpression, OWLClassExpression] = given match{
    // p: Property
    // f: Filler
    case ObjectSomeValuesFrom(p, f) => (p, f)
  }
  def matchClasses(given: Any): (Set[OWLAnnotation], OWLClassExpression, OWLClassExpression) = given match {
    case SubClassOf(p) => p
  }

  def matchIntersectionOF(given: OWLClassExpression):List[OWLClassExpression] = given match{
    case ObjectIntersectionOf(p) => p.toList
  }
  def matchHasValue(given: OWLClassExpression): Tuple2[OWLObjectPropertyExpression, OWLNamedIndividual] = given match{
    // p: Property
    // i: Individual
    case ObjectHasValue(p, i) => (p, i.asInstanceOf[OWLNamedIndividual])
  }
  // Dives into the subclasses
}
