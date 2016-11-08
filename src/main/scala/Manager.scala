import java.io.{File, PrintWriter, FileOutputStream}
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
    * or translate it into modal logic formulas
    * @param args input options in the next order:
    *     [render | translate | info] ontologyName
    */
  def main(args: Array[String]): Unit = {
    if (args.length == 2) {

      val opt = args(0)
      var b: Boolean = false
      List("render", "translate", "info").foreach(x => b = b || x.startsWith(opt))
      if (!b){
        println("#"*99)
        println("\nWARNING!!! Wrong option\n")
        println("Available options are:\n\n"+"\tinfo\n\trender\n\ttranslate")
        println("\n"+"#"*99)
        help()
        sys.exit(0)
      }

      val ontologyName = args(1)
      var ontologies: List[File] = getListOfFiles(new File(getCurrentDirectory + "/ontologies"), List("owl", "rdf"))
      val ontSel = getOntologies(ontologyName, ontologies)
      val prefix = "Saving file:\n\n\t" + getCurrentDirectory + "/output/"

      if (ontSel.nonEmpty) {
        val dir = new File("output/general_info")
        dir.mkdirs()
        val info = {
          if (ontologyName == "all") {
            new PrintWriter(new FileOutputStream(new File(dir.toString + "/all_ontologies.csv"), false))
          } else {
            val auxname = ontSel.head._2
            new PrintWriter(new FileOutputStream(new File(dir.toString + s"/$auxname.csv"), false))
          }
        }
        info.write("NAME, SIZE(MB), LOAD_TIME, AXIOM_COUNT, TBOX_COUNT, ABOX_COUNT, TRANSLATED_AXIOM_COUNT, [RENDER_TRANSLATE]_TIME, DL2ML_TOTAL_TIME\n")
        for ((ont, name) <- ontSel) {
          println("="*99)
          info.write(name + "," +
            BigDecimal(ont.length/(1024.0*1024.0)).setScale(3,
              BigDecimal.RoundingMode.HALF_UP).toDouble + ",")
          println("\nOntology:\n\n\t" + name + " - size: " +
            BigDecimal(ont.length/(1024.0*1024.0)).setScale(3,
              BigDecimal.RoundingMode.HALF_UP).toDouble + " M"+ "\n")
          val manager = OWLManager.createOWLOntologyManager
          val t0 = getTime()
          val ontology = manager.loadOntologyFromOntologyDocument(ont)
          val t1 = getTime()

          info.write((t1-t0)/1000.0 +",")
          info.write(ontology.getAxiomCount + ",")
          info.write(ontology.getTBoxAxioms(Imports.INCLUDED).size + ",")
          info.write(ontology.getABoxAxioms(Imports.INCLUDED).size + ",")

          /** Load the selected ontology(ies) */
          /** axioms to work with */
          try{
            if ("render".startsWith(opt)) {
              val t00 = getTime()
              workIt(ontology, opt, name, info)
              val t01 = getTime()
              info.write((t01-t00)/1000.0 + ",")
              println(prefix + "rendered/" + name + "[_ABox | _TBox].txt\n")
            }else if ("translate".startsWith(opt)) {
              val t00 = getTime()
              workIt(ontology, opt, name, info)
              val t01 = getTime()
              info.write((t01-t00)/1000.0 + ",")
              println(prefix + "translations/" + name + "_TBox.intohylo\n")
            }

          } catch {
            case e: NoSuchElementException => println("WARNING! " + e.getMessage)
          }
          if (!(ontologyName == "all")){
            println(prefix + "general_info/" + name + ".csv\n")
          }

          info.write(((getTime()-t0)/1000.0).toString)
          info.write("\n")
          println("="*99)

        }
        //xs.sortWith(_._1 > _._1).take(20).foreach(f => info.write(f + "\n"))
        if (ontologyName == "all"){
          println(prefix + "general_info/all_ontologies.info\n")
        }
        println("Ontologies info format:\n\n\t" + "[NAME, SIZE(MB), LOAD_TIME, AXIOM_COUNT, TBOX_COUNT, ABOX_COUNT, TRANSLATED_AXIOM_COUNT, [RENDER_TRANSLATE]_TIME, DL2ML_TOTAL_TIME]\n")
        info.close()
      } else {
        println("\n\nONTOLOGY NOT FOUND...\n")
        help()
      }
    } else {
      println("Argument(s) missing...\n")
      help()
    }
  }
  def getTime(): Long = {
    System.currentTimeMillis
  }
  /**
    * Writes a file with the axioms rendered or translated, depending on
    *     option chosen
    * @param ontology The ontology to work with
    * @param opt wether render or translate the axioms
    * @param output A string for the output file (the ontology name)
    */
  def workIt(ontology: OWLOntology, opt: String, output: String, info: PrintWriter) = {
    val abox = ontology.getABoxAxioms(Imports.INCLUDED).toList
    val tbox = ontology.getTBoxAxioms(Imports.INCLUDED).toList
    if ("translate".startsWith(opt)){
      formRender(dl2ml(tbox, output, info), ontology, output + "_TBox")
        /** No ABox for now... 
          * 
          * formRender(dl2ml(abox), ontology, output + "_ABox")
          */
    } else if ("render".startsWith(opt)) {
      render(abox, output + "_ABox")
      render(tbox, output + "_TBox")
    } else {
      help()
    }
  }
  /**
    * Get the TBox or the ABox from an ontology
    * @param ont An OWL Ontology
    * @param axioms A string, could be 'tbox' or 'abox'
    * @return A list of axioms
    */
  @deprecated("","")
  def getAxioms(ont: OWLOntology, axioms: String): List[OWLAxiom] = axioms match {
    case "tbox" => ont.getTBoxAxioms(Imports.INCLUDED).toList
    case "abox" => ont.getABoxAxioms(Imports.INCLUDED).toList
    case "ta" =>  ont.getABoxAxioms(Imports.INCLUDED).toList ++ ont.getTBoxAxioms(Imports.INCLUDED).toList
    case _ => List.empty
  }
  /**
    * 
    * Given a string and a list of ontologies names, returns a pair (ontologyPath, ontologyName) inside a list
    * if the string matches with an ontology's name, if there are multiple hits, selects one
    * randomly. Also, if the string is "all", selects all the ontologies and translate (or render) them
    * all. Returns empty if there are no matches.
    * @param matchOntology A string to match an ontology
    * @param ontologies A list of ontologies files to match
    * @return A list of (ontologyPath, ontologyName)
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
  @deprecated("","")
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
    var ontologies: List[File] = getListOfFiles(new File(getCurrentDirectory + "/ontologies"), List("owl", "rdf"))

    val usage = """
                Usage: [render | translate | info] <ontology>
                ---------------------------------
                ---------------------------------
                Example:
                        > run render gal
                        > run i dolc
                """
    println(usage)
    println("\n\n")
    println("Some of the available ontologies: " + "\n=================================")
    for (ont <- ontologies){
      println(ont.getName)
    }
  }
}
