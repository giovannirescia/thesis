package translators
import org.semanticweb.owlapi.model.ClassExpressionType.{OBJECT_MAX_CARDINALITY, OBJECT_MIN_CARDINALITY, OBJECT_EXACT_CARDINALITY  ,OBJECT_COMPLEMENT_OF, OBJECT_HAS_VALUE, OBJECT_SOME_VALUES_FROM, OBJECT_INTERSECTION_OF, OBJECT_ALL_VALUES_FROM,OBJECT_UNION_OF}
import java.io.PrintWriter
import org.phenoscape.scowl._
import org.semanticweb.owlapi.model._

/**
  * Created by giovannirescia on 26/9/16.
  */
object SubClassTranslator {
  /**
    *
    * @param axiom : a OWl Subclas of Axiom
    * @param writer : a writer to write stuff
    */
  def simpleSubClass(axiom: OWLSubClassOfAxiom, writer: PrintWriter): Unit = {
    val (_, x, y) = matchClasses(axiom)
    subClass(x.asInstanceOf[OWLClassExpression], y.asInstanceOf[OWLClassExpression], writer, axiom)
  }
  /**
    *
    * @param lhs : Left Hand Side of a OWL Expression, type: OWL Expression
    * @param rhs : Right Hand Side of a OWL Expression, type: OWL Expression
    * @param writer : a writer to write stuff
    * @param axiom: The axiom beign rendered (for debug)
    */
  def subClass(lhs: OWLClassExpression, rhs: OWLClassExpression, writer: PrintWriter, axiom: Any ): Unit = {
    writer.write(" A ( ")
    if (!lhs.isAnonymous) {
      writer.write(lhs.asOWLClass().getIRI.getShortForm)
    }else{
      inspect(lhs, writer, axiom)
    }
    // 2192: Implies
    writer.write(" \u2192 ")
    if (!rhs.isAnonymous) {
      writer.write(rhs.asOWLClass().getIRI.getShortForm)
    }
    else{
      inspect(rhs, writer, axiom)
    }
    writer.write(" ) ")
  }

  def inspect(exp: OWLClassExpression, writer: PrintWriter, axiom: Any): Unit = {
    writer.write("(")
    val expType = exp.getClassExpressionType
    if(!exp.isAnonymous){
      writer.write(exp.asOWLClass().getIRI.getShortForm)
    }
    else if (expType == OBJECT_EXACT_CARDINALITY){
      val (a,b,c) = matchCardinality(exp)
      writer.write(a + " ")
      writer.write(b.asOWLObjectProperty().getIRI.getShortForm)
      writer.write(": " + c.asOWLClass().getIRI.getShortForm)
    }
    else if (expType == OBJECT_MIN_CARDINALITY){
      val (a,b,c) = matchCardinality(exp)
      writer.write(a + " ")
      writer.write(b.asOWLObjectProperty().getIRI.getShortForm)
      writer.write(": " + c.asOWLClass().getIRI.getShortForm)
    }
    else if (expType == OBJECT_MAX_CARDINALITY){
      val (a,b,c) = matchCardinality(exp)
      writer.write(a + " ")
      writer.write(b.asOWLObjectProperty().getIRI.getShortForm)
      writer.write(": " + c.asOWLClass().getIRI.getShortForm)
    }
    else if (expType == OBJECT_COMPLEMENT_OF){
      val op = exp.asInstanceOf[OWLObjectComplementOf].getOperand
      if (!op.isAnonymous) {
        writer.write(op.asOWLClass().getIRI.getShortForm)
      } else{
        inspect(op.asInstanceOf[OWLClassExpression], writer, axiom)
      }

    }
    else if (expType == OBJECT_SOME_VALUES_FROM){
      val ys = matchSomeValuesFrom(exp)
      val ysLhs = ys._1
      val ysRhs = ys._2
      if(!ysLhs.isAnonymous) {
        writer.write(ysLhs.asOWLObjectProperty().getIRI.getShortForm)
      }
      else{
        inspect(ysLhs.asInstanceOf[OWLClassExpression], writer, axiom)
      }
      writer.write(" some ")
      if (!ysRhs.isAnonymous){
        writer.write(ysRhs.asOWLClass().getIRI.getShortForm + "")}
      else{
        inspect(ysRhs, writer, axiom)
      }
    }else if (expType == OBJECT_ALL_VALUES_FROM) {
      val ys = matchAllValuesFrom(exp)
      val ysLhs = ys._1
      val ysRhs = ys._2
      if(!ysLhs.isAnonymous) {
        writer.write(ysLhs.asOWLObjectProperty().getIRI.getShortForm)
      }
      else{
        inspect(ysLhs.asInstanceOf[OWLClassExpression], writer, axiom)
      }
      writer.write(" all ")
      if (!ysRhs.isAnonymous){
        writer.write(ysRhs.asOWLClass().getIRI.getShortForm + "")}
      else{
        inspect(ysRhs, writer, axiom)
      }
    } else if (expType == OBJECT_HAS_VALUE){
      val ys = matchHasValue(exp)
      val ysLhs = ys._1
      val ysRhs = ys._2
      if(!ysLhs.isAnonymous) {
        writer.write(ysLhs.asOWLObjectProperty().getIRI.getShortForm)
      }else{
        inspect(ysLhs.asInstanceOf[OWLClassExpression], writer, axiom)
      }
      writer.write(" value: ")
      if (!ysRhs.isAnonymous){
        writer.write(ysRhs.getIRI.getShortForm)
      }else{
        inspect(ysRhs.asInstanceOf[OWLClassExpression], writer, axiom)
      }
    }else if (expType == OBJECT_INTERSECTION_OF){
      val xs = matchIntersectionOf(exp)
      for (x <- xs.take(xs.size-1)) {
        // 2229: Intersection
        inspect(x, writer, axiom)
        writer.write(" \u2229 ")
      }
      inspect(xs.last, writer, axiom)
    }
    else if (expType == OBJECT_UNION_OF){
      val xs = matchUnionOf(exp)
      for (x <- xs.take(xs.size-1)) {
        // 2229: Intersection
        inspect(x, writer, axiom)
        writer.write(" \u222A ")
      }
      inspect(xs.last, writer, axiom)
    }else{
      writer.write("@@@@"*20)
      writer.write("\n\n Axiom: " + axiom.toString + "\n\n")
      writer.write("Couldn't be written because of the expression: " + exp.toString + "\n\n")
      writer.write("@@@@"*20)
      writer.close()
    }
    writer.write(")")
  }
  def matchCardinality(given: OWLClassExpression): (Int, OWLObjectPropertyExpression, OWLClassExpression) = given match{
    case ObjectMaxCardinality(n, p, f) => (n, p, f)
    case ObjectMinCardinality(n, p, f) => (n, p, f)
    case ObjectExactCardinality(n, p, f) => (n, p, f)
  }
  def matchAllValuesFrom(given: OWLClassExpression): (OWLObjectPropertyExpression, OWLClassExpression) = given match{
    case ObjectAllValuesFrom(p, f) => (p, f)
  }
  def matchSomeValuesFrom(given: OWLClassExpression): (OWLObjectPropertyExpression, OWLClassExpression) = given match{
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
  def matchHasValue(given: OWLClassExpression): (OWLObjectPropertyExpression, OWLNamedIndividual) = given match{
    case ObjectHasValue(p, i) => (p, i.asInstanceOf[OWLNamedIndividual])
  }
}