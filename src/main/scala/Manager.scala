import java.io.File

import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.{OWLAxiom, OWLOntology}
import org.semanticweb.owlapi.model.parameters.Imports
import scala.collection.JavaConversions._
import renders.RendererManager.render
import translators.TranslatorManager.translate
import formulae.FormulaeManager.{formulate, render => formRender}

/**
  * Created by giovannirescia on 4/10/16.
  */

object Manager{
  def main(args: Array[String]): Unit = {
    if(args.length != 4) help()
    else{
      val ontologyStr = ont(args(0))
      if (ontologyStr.nonEmpty){
        val file = new File(ontologyStr)
        val manager = OWLManager.createOWLOntologyManager
        val ontology = manager.loadOntologyFromOntologyDocument(file)
        println("\n" + ontology.getOntologyID.getOntologyIRI.get + "\n")
        val axioms = getAxioms(ontology, args(1))
        if(axioms.nonEmpty) workIt(axioms, args(2), ontology, args(3))
        else help()
        }
      else help()
    }
  }

  def workIt(axioms: List[OWLAxiom], x: String, ont: OWLOntology, output: String) = x match {
    case "translate" => formRender(formulate(axioms), ont, output)
    case "render" => render(axioms, output)
    case _ => help()
  }

  def getAxioms(ont: OWLOntology, k: String): List[OWLAxiom] = k match {
    case "tbox" => ont.getTBoxAxioms(Imports.INCLUDED).toList
    case "abox" => ont.getABoxAxioms(Imports.INCLUDED).toList
    case _ => List.empty
  }

  def ont(x: String): String = x match {
    case "fam" => "/Users/giovannirescia/coding/tesis/ontologies/family_example.owl"
    case "gal" => "/Users/giovannirescia/coding/tesis/ontologies/galen.owl"
    case "dol" => "/Users/giovannirescia/coding/tesis/ontologies/dolce.owl"
    case "wine" => "/Users/giovannirescia/coding/tesis/ontologies/wine_3.owl"
    case _ => ""
  }
  def help(): Unit ={
    val usage = """
                Usage: ontology axiom option file
                ---------------------------------
                ---------------------------------

                ontologie:  fam -> family ontology
                            gal -> galen ontology
                            dol -> dolce ontology
                            wine -> wine ontology

                axiom:  tbox -> the axioms in the tbox
                        abox -> the axioms in the abox

                option: render -> render the axioms in manchester syntax
                        translate -> perform a soon-to-be-finished translation

                file: an output file name

                =================================

                example:
                        > run gal abox render test_abox_rend

                """
    println(usage)
  }
}
