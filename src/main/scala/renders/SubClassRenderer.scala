package renders

import java.io.PrintWriter

import org.phenoscape.scowl._
import org.semanticweb.owlapi.model.ClassExpressionType.{OBJECT_MAX_CARDINALITY, OBJECT_MIN_CARDINALITY, OBJECT_EXACT_CARDINALITY  ,OBJECT_COMPLEMENT_OF, OBJECT_HAS_VALUE, OBJECT_SOME_VALUES_FROM, OBJECT_INTERSECTION_OF, OBJECT_ALL_VALUES_FROM,OBJECT_UNION_OF}
import org.semanticweb.owlapi.model._
/**
  * Created by giovannirescia on 24/9/16.
  */
object SubClassRenderer {
  // Only for simple subclass axioms
  /**
    *
    * @param axiom : a OWl Subclas of Axiom
    * @param writer : a writer to write stuff
    */
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
  /**
    *
    * @param lhs : Left Hand Side of a OWL Expression, type: OWL Expression
    * @param rhs : Right Hand Side of a OWL Expression, type: OWL Expression
    * @param writer : a writer to write stuff
    */
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
    else{
      inspect(rhs, writer)
    }
    writer.write(" ] ")

    //writer.write("\n")
  }

  def inspect(exp: OWLClassExpression, writer: PrintWriter): Unit = {
    writer.write("(")
    val expType = exp.getClassExpressionType
    if (expType == OBJECT_EXACT_CARDINALITY){
      val (a,b,c) = matchExactCardinality(exp)
      writer.write(a + " ")
      writer.write("" + b.asOWLObjectProperty().getIRI.getShortForm)
      writer.write(": " + c.asOWLClass().getIRI.getShortForm)
    }
    else if (expType == OBJECT_MIN_CARDINALITY){
      val (a,b,c) = matchMinCardinality(exp)
      writer.write(a + " ")
      writer.write("" + b.asOWLObjectProperty().getIRI.getShortForm)
      writer.write(": " + c.asOWLClass().getIRI.getShortForm)
    }
    else if (expType == OBJECT_MAX_CARDINALITY){
      val (a,b,c) = matchMaxCardinality(exp)
      writer.write(a + " ")
      writer.write("" + b.asOWLObjectProperty().getIRI.getShortForm)
      writer.write(": " + c.asOWLClass().getIRI.getShortForm)
    }
    else if (expType == OBJECT_COMPLEMENT_OF){
      val op = exp.asInstanceOf[OWLObjectComplementOf].getOperand
      if (!op.isAnonymous) {
        writer.write(op.asOWLClass().getIRI.getShortForm)
      } else{
        inspect(op.asInstanceOf[OWLClassExpression], writer)
      }

    }
    else if (expType == OBJECT_SOME_VALUES_FROM){
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
    }else if (expType == OBJECT_ALL_VALUES_FROM) {
      val ys = matchAllValuesFrom(exp)
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
    } else if (expType == OBJECT_HAS_VALUE){
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
    }else if (expType == OBJECT_INTERSECTION_OF){
      val xs = matchIntersectionOf(exp)
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
          }else if (x.getClassExpressionType == OBJECT_ALL_VALUES_FROM){
            inspect(x, writer)
          }

        }

      }
    }else if (expType == OBJECT_UNION_OF){
      val xs = matchUnionOf(exp)
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
          writer.write(" \u222A ")
          if (x.getClassExpressionType == OBJECT_HAS_VALUE) {
            inspect(x, writer)
          } else if (x.getClassExpressionType == OBJECT_SOME_VALUES_FROM) {
            inspect(x, writer)
          }else if (x.getClassExpressionType == OBJECT_ALL_VALUES_FROM){
            inspect(x, writer)
          }

        }

      }
    }else{
      writer.close()
    }
    writer.write(")")

  }
  def matchExactCardinality(given: OWLClassExpression): Tuple3[Int, OWLObjectPropertyExpression, OWLClassExpression] = given match{
    case ObjectExactCardinality(n, p, f) => (n, p, f)
  }
  def matchMinCardinality(given: OWLClassExpression): Tuple3[Int, OWLObjectPropertyExpression, OWLClassExpression] = given match{
    case ObjectMinCardinality(n, p, f) => (n, p, f)
  }
  def matchMaxCardinality(given: OWLClassExpression): Tuple3[Int, OWLObjectPropertyExpression, OWLClassExpression] = given match{
    case ObjectMaxCardinality(n, p, f) => (n, p, f)
  }
  def matchAllValuesFrom(given: OWLClassExpression): Tuple2[OWLObjectPropertyExpression, OWLClassExpression] = given match{
    // p: Property
    // f: Filler
    case ObjectAllValuesFrom(p, f) => (p, f)
  }
  def matchSomeValuesFrom(given: OWLClassExpression): Tuple2[OWLObjectPropertyExpression, OWLClassExpression] = given match{
    // p: Property
    // f: Filler
    case ObjectSomeValuesFrom(p, f) => (p, f)
  }
  def matchClasses(given: Any): (Set[OWLAnnotation], OWLClassExpression, OWLClassExpression) = given match {
    case SubClassOf(p) => p
  }

  def matchIntersectionOf(given: OWLClassExpression):List[OWLClassExpression] = given match{
    case ObjectIntersectionOf(p) => p.toList
  }
  def matchUnionOf(given: OWLClassExpression):List[OWLClassExpression] = given match{
    case ObjectUnionOf(p) => p.toList
  }
  def matchHasValue(given: OWLClassExpression): Tuple2[OWLObjectPropertyExpression, OWLNamedIndividual] = given match{
    // p: Property
    // i: Individual
    case ObjectHasValue(p, i) => (p, i.asInstanceOf[OWLNamedIndividual])
  }
  // Dives into the subclasses
}
