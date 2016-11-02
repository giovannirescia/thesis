package translator

import org.semanticweb.owlapi.model.ClassExpressionType.{OBJECT_MAX_CARDINALITY, OBJECT_MIN_CARDINALITY, OBJECT_EXACT_CARDINALITY  ,OBJECT_COMPLEMENT_OF, OBJECT_HAS_VALUE, OBJECT_SOME_VALUES_FROM, OBJECT_INTERSECTION_OF, OBJECT_ALL_VALUES_FROM,OBJECT_UNION_OF}
import org.phenoscape.scowl._
import org.semanticweb.owlapi.model._
import ModalLogicFormulaClasses.{Diam, R, Box, And, Prop, IDiam, Bot, Or, Neg, A, Impl, MLFormula, Top}
import CustomExceptions.MissingTranslationException

object SubClassFormulae {
  /**
    *
    * @param axiom An OWL Subclass of Axiom
    * @return A Modal Logic formula of the axiom
    */
  def simpleSubClass(axiom: OWLSubClassOfAxiom): MLFormula = {
    val (x, y) = matchClasses(axiom)
    subClass(x.asInstanceOf[OWLClassExpression], y.asInstanceOf[OWLClassExpression], axiom)
  }
  /**
    *
    * @param lhs : Left Hand Side of an OWL Expression, type: OWLClassExpression
    * @param rhs : Right Hand Side of an OWL Expression, type: OWLClassExpression
    * @param axiom: The axiom beign rendered (for debug)
    * @return A Modal Logic formula of the axiom
    */
  def subClass(lhs: OWLClassExpression, rhs: OWLClassExpression, axiom: Any): MLFormula = {
    A(Impl(inspect(lhs, axiom), inspect(rhs, axiom)))
  }
  /**
    * 
    * Here is where all the work gets done
    * @param exp An OWLClassExpression
    * @param axiom The axiom being translated, meant for debug
    * @return A Modal Logic formula
    */
  def inspect(exp: OWLClassExpression, axiom: Any): MLFormula = {
    if (!exp.isAnonymous){
      Prop(exp.asOWLClass().getIRI.getShortForm)
    }
    else {
      exp match {
        case ObjectUnionOf(expressions) => tailRecursiveOr(expressions.toList, axiom)
        case ObjectIntersectionOf(expressions) => tailRecursiveAnd(expressions.toList, axiom)
        case ObjectSomeValuesFrom(prop, filler) => Diam(R (prop.asOWLObjectProperty().getIRI.getShortForm ), f = inspect(filler.asInstanceOf[OWLClassExpression], axiom))
        case ObjectAllValuesFrom(prop, filler) => Box(R (prop.asOWLObjectProperty().getIRI.getShortForm ), f = inspect(filler.asInstanceOf[OWLClassExpression], axiom))
        case ObjectHasValue(prop, filler) => {
          And(Diam(R(prop.asOWLObjectProperty().getIRI.getShortForm), Prop(filler.asInstanceOf[OWLNamedIndividual].getIRI.getShortForm)),
            Box(R(prop.asOWLObjectProperty().getIRI.getShortForm), Prop(filler.asInstanceOf[OWLNamedIndividual].getIRI.getShortForm)))
        }
        case ObjectExactCardinality(n, p, f) => IDiam(R("="+n.toString+p.asOWLObjectProperty().getIRI.getShortForm),inspect(f.asInstanceOf[OWLClassExpression], axiom))
        case ObjectMaxCardinality(n, p, f) => IDiam(R("MAX"+n.toString+p.asOWLObjectProperty().getIRI.getShortForm),inspect(f.asInstanceOf[OWLClassExpression], axiom))
        case ObjectMinCardinality(n, p, f) => IDiam(R("MIN"+n.toString+p.asOWLObjectProperty().getIRI.getShortForm),inspect(f.asInstanceOf[OWLClassExpression], axiom))
        case ObjectComplementOf(op) => Neg(inspect(op.asInstanceOf[OWLClassExpression], axiom))
        /** Unhandled cases */
        case _ => throw new MissingTranslationException(axiom.toString)
      }
    }

  }
  /**
    *
    * Prettier, faster, better
    */
  def tailRecursiveAnd(zs: List[OWLClassExpression], axiom: Any): MLFormula = {
    if (zs.size == 1){
      inspect(zs.head, axiom)
    } else{
      val init = And(inspect(zs(zs.size-2), axiom), inspect(zs.last, axiom))
      def tailRecursiveAndWithAcc(accumulator: MLFormula, ys: List[OWLClassExpression]) : MLFormula = {
        if (ys.isEmpty){
          accumulator
        } else {
          tailRecursiveAndWithAcc(And(accumulator, inspect(ys.last, axiom)), ys.take(ys.size-1))
        }
      }
      tailRecursiveAndWithAcc(init, zs.take(zs.size-2))
    }
  }
  def tailRecursiveOr(zs: List[OWLClassExpression], axiom: Any): MLFormula = {
    if (zs.size == 1){
      inspect(zs.head, axiom)
    } else{
    val init = Or(inspect(zs(zs.size-2), axiom), inspect(zs.last, axiom))
    def tailRecursiveOrWithAcc(accumulator: MLFormula, ys: List[OWLClassExpression]) : MLFormula = {
      if (ys.isEmpty){
        accumulator
      } else {
        tailRecursiveOrWithAcc(Or(accumulator, inspect(ys.last, axiom)), ys.take(ys.size-1))
      }
    }
    tailRecursiveOrWithAcc(init, zs.take(zs.size-2))
    }
  }
  def matchClasses(given: Any): (OWLClassExpression, OWLClassExpression) = given match {
    case SubClassOf(_, x, y) => (x, y)
  }
  @deprecated ("use tailRecursiveAnd", "")
  def recursiveAnd(ys: List[OWLClassExpression], axiom: Any): MLFormula = {
    if (ys.isEmpty) Top()
    else And(inspect(ys.head, axiom), recursiveAnd(ys.tail, axiom))
  }
  @deprecated ("use tailRecursiveOr", "")
  def recursiveOr(xs: List[OWLClassExpression], axiom: Any): MLFormula = {
    if (xs.isEmpty) Bot()
    else Or(inspect(xs.head, axiom), recursiveOr(xs.tail, axiom))
  }
  @deprecated("","")
  def matchCardinality(given: OWLClassExpression): (Int, OWLObjectPropertyExpression, OWLClassExpression) = given match{
    case ObjectMaxCardinality(n, p, f) => (n, p, f)
    case ObjectMinCardinality(n, p, f) => (n, p, f)
    case ObjectExactCardinality(n, p, f) => (n, p, f)
  }
  @deprecated("","")
  def matchAllValuesFrom(given: OWLClassExpression): (OWLObjectPropertyExpression, OWLClassExpression) = given match{
    case ObjectAllValuesFrom(p, f) => (p, f)
  }
  @deprecated("","")
  def matchSomeValuesFrom(given: OWLClassExpression): (OWLObjectPropertyExpression, OWLClassExpression) = given match{
    case ObjectSomeValuesFrom(p, f) => (p, f)
  }
  @deprecated("","")
  def matchIntersectionOf(given: OWLClassExpression):List[OWLClassExpression] = given match{
    case ObjectIntersectionOf(p) => p.toList
  }
  @deprecated("","")
  def matchUnionOf(given: OWLClassExpression):List[OWLClassExpression] = given match{
    case ObjectUnionOf(p) => p.toList
  }
  @deprecated("","")
  def matchHasValue(given: OWLClassExpression): (OWLObjectPropertyExpression, OWLNamedIndividual) = given match{
    case ObjectHasValue(p, i) => (p, i.asInstanceOf[OWLNamedIndividual])
  }
}
