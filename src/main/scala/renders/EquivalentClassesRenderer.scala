package renders
import java.io.{File, FileWriter, PrintWriter, StringWriter}
import javafx.print.Printer
import java.io.FileOutputStream
import org.semanticweb.owlapi.model.ClassExpressionType.{OBJECT_HAS_VALUE, OBJECT_INTERSECTION_OF, OBJECT_SOME_VALUES_FROM}
import org.semanticweb.owlapi.model._
import org.phenoscape.scowl._
import renders.SubClassRenderer.{subClass}
/**
  * Created by giovannirescia on 24/9/16.
  */
object EquivalentClassesRenderer {
  /**
    *
    * @param axiom An OWL Equivalent Classes Axiom List
    * @param writer A writer for writing stuff
    *
    */
  def equivClasses(axiom: OWLEquivalentClassesAxiom, writer: PrintWriter): Unit ={
      // Class_A == Class_B => Class_A <= Class_B && Class_B <= Class_A
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
