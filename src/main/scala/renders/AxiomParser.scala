package renders
import org.semanticweb.owlapi.model.ClassExpressionType.{OBJECT_HAS_VALUE, OBJECT_INTERSECTION_OF, OBJECT_SOME_VALUES_FROM}
import org.semanticweb.owlapi.model._
import org.phenoscape.scowl._
/**
  * Created by giovannirescia on 24/9/16.
  */
object AxiomParser {
  /**
    *
    * @param axiom An OWL Equivalent Classes Axiom
    */
  def snipp(axiom: OWLEquivalentClassesAxiom): Unit ={
    val listExpr = axiom.getClassExpressionsAsList
    val lfs = listExpr.get(0)
    val rhs = listExpr.get(1)

    if (!lfs.isAnonymous){
      println(lfs.asOWLClass().getIRI.getShortForm)
    }
    println(" <- \u2263 ->")
    if (!rhs.isAnonymous){
      println(rhs.asOWLClass().getIRI.getShortForm)
    }
    else if (rhs.getClassExpressionType == OBJECT_INTERSECTION_OF){
      val xs = matchIntersectionOF(rhs)
      val xsLhs = xs(0)
      val xsRhs = xs(1)
      if (!xsLhs.isAnonymous){
        println(xsLhs.asOWLClass().getIRI.getShortForm)
      }


      if (!xsRhs.isAnonymous){
        println(xsRhs.asOWLClass().getIRI.getShortForm)
      }
      else{
        for(x <- xs.tail){
          println(" <- " + "\u2229" + " -> ")

          if (x.getClassExpressionType == OBJECT_HAS_VALUE){
            val ys = matchHasValue(x)
            val ysLhs = ys._1
            val ysRhs = ys._2

            println(ysLhs.asOWLObjectProperty().getIRI.getShortForm)

            println(" <-  Value:  -> ")

            println(ysRhs.getIRI.getShortForm)

          }else if(x.getClassExpressionType == OBJECT_SOME_VALUES_FROM){
            val ys = matchSomeValuesFrom(x)
            val ysLhs = ys._1
            val ysRhs = ys._2

            println(ysLhs.asOWLObjectProperty().getIRI.getShortForm)

            println(" <- Some -> ")

            println(ysRhs.asOWLClass().getIRI.getShortForm)
          }

        }

      }
    }
  }

  def matchSomeValuesFrom(given: OWLClassExpression): Tuple2[OWLObjectPropertyExpression, OWLClassExpression] = given match{
    case ObjectSomeValuesFrom(p, f) => (p, f)
  }

  def matchIntersectionOF(given: OWLClassExpression):List[OWLClassExpression] = given match{
    case ObjectIntersectionOf(p) => p.toList
  }
  def matchHasValue(given: OWLClassExpression): Tuple2[OWLObjectPropertyExpression, OWLNamedIndividual] = given match{
    case ObjectHasValue(pp, ff) => (pp, ff.asInstanceOf[OWLNamedIndividual])
  }
}
