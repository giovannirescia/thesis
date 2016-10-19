import java.io.File
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.{OWLAxiom, OWLOntology}
import org.semanticweb.owlapi.model.parameters.Imports
import scala.collection.JavaConversions._
import render.RenderManager.render
import translator.TranslatorManager.{dl2ml, render => formRender}
import scala.collection.mutable.ListBuffer
import scala.util.Random
import scala.math.BigDecimal

object Manager{
  /**
    * 
    * Manages an ontology for render it (pretty print),
    * or translate it into modal logic formulae
    * @param args input options in the next order:
    *     ontology axioms: TBox|ABox outputFileName
    */
  def main(args: Array[String]): Unit = {
    if (args.length == 2) {

      val rendOrTrans = args(0)
      val isR: Boolean = {
        if (rendOrTrans == "render") true
        else false
      }
      val ontologyName = args(1)
      var ontologies: List[File] = getListOfFiles(new File(getCurrentDirectory + "/ontologies"), List("owl"))

      val ontSel = getOntologies(ontologyName, ontologies)
      val prefix = "Saving file:\n\n\t" + getCurrentDirectory + "/output/"
      if (ontSel.nonEmpty) {
          for ((ont, name) <- ontSel){
            println("="*99)
            println("\nOntology:\n\n\t" + name + " - size: " +
              BigDecimal(ont.length/(1024.0*1024.0)).setScale(3,
                BigDecimal.RoundingMode.HALF_UP).toDouble + " M"+ "\n")
            val manager = OWLManager.createOWLOntologyManager
            val ontology = manager.loadOntologyFromOntologyDocument(ont)
            /** Load the selected ontology(ies) */
            /** axioms to work with */
            val abox = ontology.getABoxAxioms(Imports.INCLUDED).toList
            val tbox = ontology.getTBoxAxioms(Imports.INCLUDED).toList
            if (isR) {
              println(prefix + "rendered/" + name + "[_ABox | _TBox].txt\n")
            }else{
              println(prefix + "translations/intohylo/" + name + "[_ABox | _TBox].intohylo\n")
            }
            println("="*99)
            workIt(abox, rendOrTrans, ontology, name + "_ABox")
            workIt(tbox, rendOrTrans, ontology, name + "_TBox")
          }
      } else {
        println("\n\nONTOLOGY NOT FOUND...\n")
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
  @deprecated
  def getAxioms(ont: OWLOntology, axioms: String): List[OWLAxiom] = axioms match {
    case "tbox" => ont.getTBoxAxioms(Imports.INCLUDED).toList
    case "abox" => ont.getABoxAxioms(Imports.INCLUDED).toList
    case "ta" =>  ont.getABoxAxioms(Imports.INCLUDED).toList ++ ont.getTBoxAxioms(Imports.INCLUDED).toList
    case _ => List.empty
  }
  /**
    * Get an ontology from an existing file
    * @param ontology A string for choose an ontology
    *     it could be: fam, gal, dol, wine
    * @return The full path of the ontology
    */
  def getOntologies(matchOntology: String, ontologies: List[File]): List[(File, String)] = {
    var xs = new ListBuffer[(File, String)]()
    val regexp = {
      if(matchOntology == "all"){
        ""
      }else{
        matchOntology
      }
    }
    for (o <- ontologies){
      if (o.getName.startsWith(regexp)){
        xs += ((o, o.getName))
      }
    }
    if (matchOntology != "all" && xs.nonEmpty){
      Random.shuffle(xs.toList).head::Nil
    } else {
      xs.toList
    }
  }
  /**
    *
    * @param dir The directory where the ontologies are stored
    * @param extensions The ontologies possible extensions, eg, owl, obo, rdf
    * @return A list of fullpaths of ontologies
    */
  def getListOfFiles(dir: File, extensions: List[String]): List[File] = {
    dir.listFiles.filter(_.isFile).toList.filter { file =>
      extensions.exists(file.getName.endsWith(_))
    }
  }

  /**
    *
    * @param dir The directory where the ontologies are store
    * @return An Array of ontologies (the file of)
    */
  def findOntologies(dir: File): Array[File] = {
    val extensions = List("owl", "rdf", "obo")
    val (dirs, files) =  dir.listFiles.partition(_.isDirectory)
    (files ++ dirs.flatMap(findOntologies).toList).filter { file =>
      extensions.exists(file.getName.endsWith(_))
    }
  }

  /**
    * @return The full path of this script
    */
  def getCurrentDirectory: String = {
    new java.io.File(".").getCanonicalPath
  }
  /**
    * Simple log of how to use this script
    */
  def help(): Unit ={
    var ontologies: List[File] = getListOfFiles(new File(getCurrentDirectory + "/ontologies"), List("owl"))

    val usage = """
                Usage: [render | translate] <ontology>
                ---------------------------------
                ---------------------------------
                Example:
                        > run render gal
                """
    println(usage)
    println("\n\n")
    println("Some of the available ontologies: " + "\n=================================")
    for (ont <- ontologies){
      println(ont.getName)
    }
  }
}
