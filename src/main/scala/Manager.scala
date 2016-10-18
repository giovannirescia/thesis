import java.io.File
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.{OWLAxiom, OWLOntology}
import org.semanticweb.owlapi.model.parameters.Imports
import scala.collection.JavaConversions._
import render.RenderManager.render
import translator.TranslatorManager.{dl2ml, render => formRender}


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
      val ontologyName = args(1)
      var ontologies: List[File] = List.empty
      val ontSel = getOntology(ontologyName)

      if (ontSel == "all") {
        ontologies = findOntologies(new File("/Users/giovannirescia/Downloads/ontologies/")).toList
      } else if (ontSel.nonEmpty) {
        ontologies = new File(getCurrentDirectory + "/ontologies/" + getOntology(ontologyName)) :: Nil
      }
      if (ontologies.nonEmpty) {
        /** Randomize list because some ontologies have the same ID */
        val randOnts = util.Random.shuffle(ontologies)
        val manager = OWLManager.createOWLOntologyManager
        for (ont <- randOnts) {
          try {
            manager.loadOntologyFromOntologyDocument(ont)
          } catch {
               case _ : Throwable => {}
          }
        }
        val xs = manager.getOntologies().toList
        for (ontology <- xs) {
          /** Load the selected ontology(ies) */
          println("\n" + ontology.getOntologyID.getOntologyIRI.get + "\n")
          /** axioms to work with */
          val abox = ontology.getABoxAxioms(Imports.INCLUDED).toList
          val tbox = ontology.getTBoxAxioms(Imports.INCLUDED).toList

          val name = ontology.getOntologyID.getOntologyIRI.get().toString.replaceAll("/", "-")

          workIt(abox, rendOrTrans, ontology, name + "_ABox")
          workIt(tbox, rendOrTrans, ontology, name + "_TBox")

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
  def getOntology(ontology: String): String = ontology match {
    case "family" => "family_example.owl"
    case "galen" => "galen.owl"
    case "dolce" => "dolce.owl"
    case "wine" => "wine_3.owl"
    case "lubm" => "lubm_1.owl"
    case "modlubm" => "modlubm_3.owl"
    case "semintec" => "semintec_1.owl"
    case "vicodi" => "vicodi_4.owl"
    case "all" => "all"
    case _ => ""
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
    val usage = """
                Usage: <option> <ontology>
                ---------------------------------
                ---------------------------------

                ontologies:  family -> family ontology
                            galen -> galen ontology
                            dolce -> dolce ontology
                            wine -> wine ontology
                            lubm -> lubm ontology
                            modlubm -> modlubm ontology
                            vicodi -> vicodi ontology
                            semintec -> semintec ontology
                            all -> all the ontologies in the ontologies dir

                option: render -> render the axioms in manchester syntax
                        translate -> perform a soon-to-be-finished translation into modal logic formulae

                =================================

                example:
                        > run render galen

                """
    println(usage)
  }
}
