package translators
import java.io.PrintWriter
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom
import translators.SubClassTranslator._

/**
  * Created by giovannirescia on 26/9/16.
  */
object EquivalentClassesTranslator {
  /**
    *
    * @param axiom An OWL Equivalent Classes Axiom List
    * @param writer A writer for writing stuff
    *
    */
  def equivClasses(axiom: OWLEquivalentClassesAxiom, writer: PrintWriter): Unit ={
    val listExpr = axiom.getClassExpressionsAsList
    val lhs = listExpr.get(0)
    val rhs = listExpr.get(1)
    // Class_A <= Class_B
    subClass(lhs, rhs, writer, axiom)
    // 2227 : AND
    writer.write(" \u2227 ")
    // Class_B <= Class_A
    subClass(rhs, lhs, writer, axiom)
  }
}
