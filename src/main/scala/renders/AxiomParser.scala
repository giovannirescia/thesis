package renders
import java.io.{File, FileWriter, PrintWriter, StringWriter}
import javafx.print.Printer
import java.io.FileOutputStream
import org.semanticweb.owlapi.model.ClassExpressionType.{OBJECT_HAS_VALUE, OBJECT_INTERSECTION_OF, OBJECT_SOME_VALUES_FROM}
import org.semanticweb.owlapi.model._
import org.phenoscape.scowl._
/**
  * Created by giovannirescia on 24/9/16.
  */
object AxiomParser {
  /**
    *
    * @param axioms An OWL Equivalent Classes Axioms List
    * @param outFile A path to the new output file
    *
    */
  def snipp(axioms: List[OWLEquivalentClassesAxiom], outFile: String): Unit ={
    val writer = new PrintWriter(new FileOutputStream(new File(s"/Users/giovannirescia/coding/tesis/output/$outFile.txt"),true))
    for (axiom <- axioms) {
      val listExpr = axiom.getClassExpressionsAsList
      val lfs = listExpr.get(0)
      val rhs = listExpr.get(1)

      if (!lfs.isAnonymous) {
        writer.write(lfs.asOWLClass().getIRI.getShortForm)
      }
      writer.write(" \u2263 ")
      if (!rhs.isAnonymous) {
        writer.write(rhs.asOWLClass().getIRI.getShortForm)
      }
      else if (rhs.getClassExpressionType == OBJECT_INTERSECTION_OF) {
        val xs = matchIntersectionOF(rhs)
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
            writer.write(" \u2229 ")

            if (x.getClassExpressionType == OBJECT_HAS_VALUE) {
              val ys = matchHasValue(x)
              val ysLhs = ys._1
              val ysRhs = ys._2

              writer.write("(" + ysLhs.asOWLObjectProperty().getIRI.getShortForm)

              writer.write(" value: ")

              writer.write(ysRhs.getIRI.getShortForm + ")")

            } else if (x.getClassExpressionType == OBJECT_SOME_VALUES_FROM) {
              val ys = matchSomeValuesFrom(x)
              val ysLhs = ys._1
              val ysRhs = ys._2

              writer.write("(" + ysLhs.asOWLObjectProperty().getIRI.getShortForm)

              writer.write(" some ")

              writer.write(ysRhs.asOWLClass().getIRI.getShortForm + ")")
            }

          }

        }

      }
     writer.write("\n")

    }
    writer.close()
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
