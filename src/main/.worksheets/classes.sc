import java.io.File
import java.io.{File, FileOutputStream, PrintWriter, StringWriter}

import scala.collection.JavaConversions._
import org.phenoscape.scowl._
import org.semanticweb.owlapi.model.parameters.Imports

import scala.collection.JavaConversions._
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.OWLAxiom
import render.ManchesterRenderer.{renderManchesterSyntax => lmr}
val manager = OWLManager.createOWLOntologyManager

def getListOfFiles(dir: File, extensions: List[String]): List[File] = {
  dir.listFiles.filter(_.isFile).toList.filter { file =>
    extensions.exists(file.getName.endsWith(_))
  }
}
def findFiles(d: File): Array[File] = {
  val (dirs, files) =  d.listFiles.partition(_.isDirectory)
  files ++ dirs.flatMap(findFiles)
}

val ontologies = getListOfFiles(new File("/Users/giovannirescia/coding/tesis/ontologies/"), List("owl"))
ontologies.last
val xs = ontologies.last :: Nil
for (o <- ontologies){
  val m = OWLManager.createOWLOntologyManager
  val ontology = m.loadOntologyFromOntologyDocument(o)
  val name = o.getName
  println(name)
  val abox = ontology.getABoxAxioms(Imports.INCLUDED).toList
  val tbox = ontology.getTBoxAxioms(Imports.INCLUDED).toList
  lmr(abox, m, name + "_ABox")
  lmr(tbox, m, name + "_TBox")
}