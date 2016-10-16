package render

import java.io.{PrintWriter}
import org.semanticweb.owlapi.model._
import render.SubClassRenderer.{subClass}

object EquivalentClassesRenderer {
  /**
    *
    * @param axiom An OWL Equivalent Classes Axiom
    * @param writer A writer for writing stuff
    *
    */
  def equivClasses(axiom: OWLEquivalentClassesAxiom, writer: PrintWriter): Unit ={
      val listExpr = axiom.getClassExpressionsAsList
      val lhs = listExpr.get(0)
      val rhs = listExpr.get(1)
      /** In equivalent sets, each set it's subset of the other */
      subClass(lhs, rhs, writer, axiom)
      // 2227 : AND
      writer.write(" \u2227 ")
      subClass(rhs, lhs, writer, axiom)
  }
}
