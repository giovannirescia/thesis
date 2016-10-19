package translator

import org.semanticweb.owlapi.model.ClassExpressionType.{OBJECT_MAX_CARDINALITY, OBJECT_MIN_CARDINALITY, OBJECT_EXACT_CARDINALITY  ,OBJECT_COMPLEMENT_OF, OBJECT_HAS_VALUE, OBJECT_SOME_VALUES_FROM, OBJECT_INTERSECTION_OF, OBJECT_ALL_VALUES_FROM,OBJECT_UNION_OF}
import org.phenoscape.scowl._
import org.semanticweb.owlapi.model._
import ModalLogicFormulaClasses._


object SubClassFormulae {
  /**
    *
    * @param axiom An OWL Subclas of Axiom
    * @return A Modal Logic formula of the axiom
    */
  def simpleSubClass(axiom: OWLSubClassOfAxiom): MLFormula = {
    val (_, x, y) = matchClasses(axiom)
    subClass(x.asInstanceOf[OWLClassExpression], y.asInstanceOf[OWLClassExpression], axiom)
  }
  /**
    *
    * @param lhs : Left Hand Side of a OWL Expression, type: OWL Expression
    * @param rhs : Right Hand Side of a OWL Expression, type: OWL Expression
    * @param axiom: The axiom beign rendered (for debug)
    * @return A Modal Logic formula of the axiom
    */
  def subClass(lhs: OWLClassExpression, rhs: OWLClassExpression, axiom: Any): MLFormula = {
    ModalLogicFormulaClasses.A (Impl(inspect(lhs, axiom), inspect(rhs, axiom)))
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
        /** Unhandled cases */
        case _ => Bot()
      }
    }
    // TODO: TRANSLATIONS MISSING
    /*
    writer.write("(") if (expType == OBJECT_EXACT_CARDINALITY){ val
    (a,b,c) = matchCardinality(exp) writer.write(a + " ")
    writer.write(b.asOWLObjectProperty().getIRI.getShortForm)
    writer.write(": " + c.asOWLClass().getIRI.getShortForm) } else if
    (expType == OBJECT_MIN_CARDINALITY){ val (a,b,c) =
    matchCardinality(exp) writer.write(a + " ")
    writer.write(b.asOWLObjectProperty().getIRI.getShortForm)
    writer.write(": " + c.asOWLClass().getIRI.getShortForm) } else if
    (expType == OBJECT_MAX_CARDINALITY){ val (a,b,c) =
    matchCardinality(exp) writer.write(a + " ")
    writer.write(b.asOWLObjectProperty().getIRI.getShortForm)
    writer.write(": " + c.asOWLClass().getIRI.getShortForm) } else if
    (expType == OBJECT_COMPLEMENT_OF){ val op =
    exp.asInstanceOf[OWLObjectComplementOf].getOperand if
    (!op.isAnonymous) {
    writer.write(op.asOWLClass().getIRI.getShortForm) } else{
    inspect(op.asInstanceOf[OWLClassExpression], axiom) } } else{
    writer.write("@@@@"*20) writer.write("\n\n Axiom: " +
    axiom.toString + "\n\n") writer.write("Couldn't be written because
    of the expression: " + exp.toString + "\n\n")
    writer.write("@@@@"*20) writer.close() } writer.write(")")
    */
  }
  /**
    *
    * Prettier, faster, better
    */
  def tailRecursiveAnd(zs: List[OWLClassExpression], axiom: Any): MLFormula = {
    if (zs.size == 1){
      inspect(zs.head, axiom)
    } else{
      val init = And(inspect(zs.last, axiom), inspect(zs(zs.size-2), axiom))
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
    val init = Or(inspect(zs.last, axiom), inspect(zs(zs.size-2), axiom))
    def tailRecursiveOrWithAcc(accumulator: MLFormula, ys: List[OWLClassExpression]) : MLFormula = {
      if (ys.isEmpty){
        accumulator
      } else {
        tailRecursiveOrWithAcc(And(accumulator, inspect(ys.last, axiom)), ys.take(ys.size-1))
      }
    }
    tailRecursiveOrWithAcc(init, zs.take(zs.size-2))
    }
  }
  @deprecated ("use tailRecursiveAnd")
  def recursiveAnd(ys: List[OWLClassExpression], axiom: Any): MLFormula = {
    if (ys.isEmpty) Top()
    else And(inspect(ys.head, axiom), recursiveAnd(ys.tail, axiom))
  }
  @deprecated ("use tailRecursiveOr")
  def recursiveOr(xs: List[OWLClassExpression], axiom: Any): MLFormula = {
    if (xs.isEmpty) Bot()
    else Or(inspect(xs.head, axiom), recursiveOr(xs.tail, axiom))
  }
  @deprecated
  def matchCardinality(given: OWLClassExpression): (Int, OWLObjectPropertyExpression, OWLClassExpression) = given match{
    case ObjectMaxCardinality(n, p, f) => (n, p, f)
    case ObjectMinCardinality(n, p, f) => (n, p, f)
    case ObjectExactCardinality(n, p, f) => (n, p, f)
  }
  @deprecated
  def matchAllValuesFrom(given: OWLClassExpression): (OWLObjectPropertyExpression, OWLClassExpression) = given match{
    case ObjectAllValuesFrom(p, f) => (p, f)
  }
  @deprecated
  def matchSomeValuesFrom(given: OWLClassExpression): (OWLObjectPropertyExpression, OWLClassExpression) = given match{
    case ObjectSomeValuesFrom(p, f) => (p, f)
  }
  def matchClasses(given: Any): (Set[OWLAnnotation], OWLClassExpression, OWLClassExpression) = given match {
    case SubClassOf(p) => p
  }
  @deprecated
  def matchIntersectionOf(given: OWLClassExpression):List[OWLClassExpression] = given match{
    case ObjectIntersectionOf(p) => p.toList
  }
  @deprecated
  def matchUnionOf(given: OWLClassExpression):List[OWLClassExpression] = given match{
    case ObjectUnionOf(p) => p.toList
  }
  @deprecated
  def matchHasValue(given: OWLClassExpression): (OWLObjectPropertyExpression, OWLNamedIndividual) = given match{
    case ObjectHasValue(p, i) => (p, i.asInstanceOf[OWLNamedIndividual])
  }
}
