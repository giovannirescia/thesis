package translator

import org.scalatest.FunSuite
import java.io.File

import org.semanticweb.owlapi.model.parameters.Imports
import org.semanticweb.owlapi.apibinding.OWLManager
import ModalLogicFormulaClasses._
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer


/**
  * Created by giovannirescia on 20/10/16.
  */
class TranslatorManager$Test extends FunSuite {
  val f1 = new File(getCurrentDirectory + "/ontologies/family_example.owl")
  val m = OWLManager.createOWLOntologyManager()
  val o = m.loadOntologyFromOntologyDocument(f1)
  val ax = o.getTBoxAxioms(Imports.INCLUDED).toList(9)

  test("testDl2ml") {
    assert(TranslatorManager.dl2ml(ax::Nil) == ListBuffer(A(Impl(Prop("Parent"), Prop("Person")))))
  }
  def getCurrentDirectory: String = {
    new java.io.File(".").getCanonicalPath
  }
}