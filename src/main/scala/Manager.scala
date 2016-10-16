import java.io.File
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.{OWLAxiom, OWLOntology}
import org.semanticweb.owlapi.model.parameters.Imports
import scala.collection.JavaConversions._
import render.RenderManager.render
import translator.TranslatorManager.{dl2ml, render => formRender}

/**
  * Created by giovannirescia on 4/10/16.
  */

object Manager{
  /**
    * 
    * Manages an ontology for render it (pretty print),
    * or translate it into modal logic formulae
    * @param args input options in the next order:
    *     ontology axioms: TBox|ABox outputFileName
    */
  def main(args: Array[String]): Unit = {    
    if(args.length == 4){
      val ontologyName = args(0)
      val axiomsType = args(1)
      val rendOrTrans = args(2)
      val outputFileName = args(3)

      val ontologyStr: String = getOntology(ontologyName)

      if (ontologyStr.nonEmpty){
        val file = new File(ontologyStr)
        val manager = OWLManager.createOWLOntologyManager
        /** Load the selected ontology */
        val ontology = manager.loadOntologyFromOntologyDocument(file)
        println("\n" + ontology.getOntologyID.getOntologyIRI.get + "\n")
        /** axioms to work with: TBox or ABox */
        val axioms = getAxioms(ontology, axiomsType)

        if(axioms.nonEmpty){
          workIt(axioms, rendOrTrans, ontology, outputFileName)
        } else {
          help()
        }
      } else {
        help()
      }
    } else {
      help()
    }
  }
  /**
    * Writes a file with the axioms rendered or translated, depending on
    *     option chosen
    * @param axioms A list of OWLAxioms
    * @param renderOrTrans wether render or translate the axioms
    * @param ont An OWL Ontology
    * @param output A string for the output file
    */
  def workIt(axioms: List[OWLAxiom], renderOrTrans: String, ont: OWLOntology, output: String) = renderOrTrans match {
    case "translate" => formRender(dl2ml(axioms), ont, output)
    case "render" => render(axioms, output)
    case _ => help()
  }
  /**
    * Get the TBox or the ABox from an ontology
    * @param ont An OWL Ontology
    * @param axioms A string, could be 'tbox' or 'abox'
    * @return A list of axioms
    */
  def getAxioms(ont: OWLOntology, axioms: String): List[OWLAxiom] = axioms match {
    case "tbox" => ont.getTBoxAxioms(Imports.INCLUDED).toList
    case "abox" => ont.getABoxAxioms(Imports.INCLUDED).toList
    case _ => List.empty
  }
  /**
    * Get an ontology from an existing file
    * @param ontology A string for choose an ontology
    *     it could be: fam, gal, dol, wine
    * @return The full path of the ontology
    */
  def getOntology(ontology: String): String = ontology match {
    case "fam" => getCurrentDirectory + "/ontologies/family_example.owl"
    case "gal" => getCurrentDirectory + "/ontologies/galen.owl"
    case "dol" => getCurrentDirectory + "/ontologies/dolce.owl"
    case "wine" => getCurrentDirectory + "/ontologies/wine_3.owl"
    case _ => ""
  }
  /**
    * @return The full path of this script
    */
  def getCurrentDirectory(): String = {
    new java.io.File(".").getCanonicalPath
  }
  /**
    * Simple log of how to use this script
    */
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
                        translate -> perform a soon-to-be-finished translation into modal logic formulae

                file: an output file name

                =================================

                example:
                        > run gal abox render test_abox_rend

                """
    println(usage)
  }
}
