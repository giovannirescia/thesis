package formulae

import org.semanticweb.owlapi.model.ClassExpressionType.{OBJECT_MAX_CARDINALITY, OBJECT_MIN_CARDINALITY, OBJECT_EXACT_CARDINALITY  ,OBJECT_COMPLEMENT_OF, OBJECT_HAS_VALUE, OBJECT_SOME_VALUES_FROM, OBJECT_INTERSECTION_OF, OBJECT_ALL_VALUES_FROM,OBJECT_UNION_OF}
import org.phenoscape.scowl._
import org.semanticweb.owlapi.model._
import FormClass._

/**
  * Created by giovannirescia on 26/9/16.
  */
object SubClassFormulae {
  /**
    *
    * @param axiom : an OWl Subclas of Axiom
    */
  def simpleSubClass(axiom: OWLSubClassOfAxiom): Form = {
    val (_, x, y) = matchClasses(axiom)
    subClass(x.asInstanceOf[OWLClassExpression], y.asInstanceOf[OWLClassExpression], axiom)
  }
  /**
    *
    * @param lhs : Left Hand Side of a OWL Expression, type: OWL Expression
    * @param rhs : Right Hand Side of a OWL Expression, type: OWL Expression
    * @param axiom: The axiom beign rendered (for debug)
    */
  def subClass(lhs: OWLClassExpression, rhs: OWLClassExpression, axiom: Any): Form = {
    FormClass.A (Impl(inspect(lhs, axiom), inspect(rhs, axiom)))
  }

  def inspect(exp: OWLClassExpression, axiom: Any): Form = {
    val expType = exp.getClassExpressionType

    if(!exp.isAnonymous){
      Prop(exp.asOWLClass().getIRI.getShortForm)
    }
    else if (expType == OBJECT_UNION_OF) {
      val xs = matchUnionOf(exp)
      val xsLhs = xs(0)
      val xsRhs = xs(1)
      Or(inspect(xsLhs, axiom), inspect(xsRhs, axiom))
    }else if (expType == OBJECT_INTERSECTION_OF){
      val xs = matchIntersectionOf(exp)
      val xsLhs = xs(0)
      val xsRhs = xs(1)
      And(inspect(xsLhs, axiom), inspect(xsRhs, axiom))
    }
    else{
      Bot()
    }
    // TODO: TRANSLATIONS MISSING
    /*
    writer.write("(")
    if (expType == OBJECT_EXACT_CARDINALITY){
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
        inspect(op.asInstanceOf[OWLClassExpression], axiom)
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
        inspect(ysLhs.asInstanceOf[OWLClassExpression], axiom)
      }
      writer.write(" some ")
      if (!ysRhs.isAnonymous){
        writer.write(ysRhs.asOWLClass().getIRI.getShortForm + "")}
      else{
        inspect(ysRhs, axiom)
      }
    }else if (expType == OBJECT_ALL_VALUES_FROM) {
      val ys = matchAllValuesFrom(exp)
      val ysLhs = ys._1
      val ysRhs = ys._2
      if(!ysLhs.isAnonymous) {
        writer.write(ysLhs.asOWLObjectProperty().getIRI.getShortForm)
      }
      else{
        inspect(ysLhs.asInstanceOf[OWLClassExpression], axiom)
      }
      writer.write(" all ")
      if (!ysRhs.isAnonymous){
        writer.write(ysRhs.asOWLClass().getIRI.getShortForm + "")}
      else{
        inspect(ysRhs, axiom)
      }
    } else if (expType == OBJECT_HAS_VALUE){
      val ys = matchHasValue(exp)
      val ysLhs = ys._1
      val ysRhs = ys._2
      if(!ysLhs.isAnonymous) {
        writer.write(ysLhs.asOWLObjectProperty().getIRI.getShortForm)
      }else{
        inspect(ysLhs.asInstanceOf[OWLClassExpression], axiom)
      }
      writer.write(" value: ")
      if (!ysRhs.isAnonymous){
        writer.write(ysRhs.getIRI.getShortForm)
      }else{
        inspect(ysRhs.asInstanceOf[OWLClassExpression], axiom)
      }
    }
    else{
      writer.write("@@@@"*20)
      writer.write("\n\n Axiom: " + axiom.toString + "\n\n")
      writer.write("Couldn't be written because of the expression: " + exp.toString + "\n\n")
      writer.write("@@@@"*20)
      writer.close()
    }
    writer.write(")")
    */
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