package translator

import java.io.{File, FileOutputStream, PrintWriter}
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import org.semanticweb.owlapi.model.AxiomType.{CLASS_ASSERTION, DATA_PROPERTY_ASSERTION, DATA_PROPERTY_DOMAIN, DATA_PROPERTY_RANGE, EQUIVALENT_CLASSES, FUNCTIONAL_OBJECT_PROPERTY, OBJECT_PROPERTY_ASSERTION, SUBCLASS_OF}
import org.semanticweb.owlapi.model._
import translator.SubClassFormulae._
import translator.EquivalentClassesFormulae._
import translator.FunctionalObjectPropertyFormulae._
import translator.DataPropertyDomain._
import ModalLogicFormulaClasses._
import translator.DataPropertyRange._
import org.semanticweb.owlapi.model.parameters.Imports


object TranslatorManager {
  /**
    *
    * Description Logic to Modal Logic, dl2ml for shorten
    * Given a list of axioms, translate them into Modal Logic formulas
    * 
    * @param axioms A list of OWLAxioms
    * @return A list of Modal Logic formulas
    */
  def dl2ml(axioms: List[OWLAxiom]): ListBuffer[MLFormula] ={
    var result = new ListBuffer[MLFormula]()

    for (axiom <- axioms){
      val axType = axiom.getAxiomType
      // <TBOX_AXIOMS>
      axType match{
        case SUBCLASS_OF => result += simpleSubClass(axiom.asInstanceOf[OWLSubClassOfAxiom])
        case EQUIVALENT_CLASSES => result += equivClasses(axiom.asInstanceOf[OWLEquivalentClassesAxiom])
        case FUNCTIONAL_OBJECT_PROPERTY => result += funcProp(axiom.asInstanceOf[OWLFunctionalObjectPropertyAxiom])
        case DATA_PROPERTY_DOMAIN => result += propDomain(axiom.asInstanceOf[OWLDataPropertyDomainAxiom])
        case DATA_PROPERTY_RANGE => result += propRange(axiom.asInstanceOf[OWLDataPropertyRangeAxiom])
        /** Unhandled cases */
        case _ => {}
      }
    }
    result
  }

  /**
    *
    * @param forms A list of Modal Logic formulas to render
    * @param ontology The OWLONtology from where the axioms where translated into MLformulas
    * @param outFile A name for the output file. Also will write another file with the axioms that
    *                couldn't be rendered because some case was missing. E.g., if outFile = "test", "testErr"
    *                will be also created
    * @param fullPath Only true when called from a worksheet (change "/Users/giovannirescia/"), otherwise it will
    *                 create the folders output/rendered and write the output file there
    */

  def render(forms: ListBuffer[MLFormula], ontology: OWLOntology, outFile: String, fullPath: Boolean = false): Unit ={
    /** Check if there are formulas to render */
    if (forms.nonEmpty){
      val classes = ontology.getClassesInSignature(Imports.INCLUDED).toList
      val individuals = ontology.getIndividualsInSignature(Imports.INCLUDED).toList
      val objprop = ontology.getObjectPropertiesInSignature(Imports.INCLUDED).toList
      val dataprop = ontology.getDataPropertiesInSignature(Imports.INCLUDED).toList
      val props = new ListBuffer[String]()
      val rels = new ListBuffer[String]()
      /** Output file */
      val writer = {
        if (fullPath){
          new PrintWriter(new FileOutputStream(new File(s"/Users/giovannirescia/coding/tesis/output/translations/$outFile.intohylo"), false))
        }else {
          val dir = new File("output/translations")
          dir.mkdirs()
          new PrintWriter(new FileOutputStream(new File(dir.toString + s"/$outFile.intohylo"), false))
        }
      }
      /**
        * 
        * This two loops are meant for creating a map
        * between the classes and propositional symbols and relations
        * e.g., Father -> P1, Daughter -> P22, hasChild -> R3
        */
      for (x <- objprop ++ dataprop){
        rels += x.getIRI.getShortForm
      }
      for (x <- classes ++ individuals){
        props += x.getIRI.getShortForm
      }
      /** The maps vars */
      var mainMap: collection.mutable.Map[String, String] = collection.mutable.Map.empty
      var relMap: collection.mutable.Map[String, String] = collection.mutable.Map.empty
      // for initialization
      var propMap: collection.mutable.Map[String, String] = collection.mutable.Map("string" -> "P1")
      var i = 2
      var j = 1
      /** The mappings */
      for (x <- rels){
        relMap += ((x.toString, "R"+j.toString))
        j += 1
      }
      for (x <- props){
        propMap += ((x.toString , "P"+i.toString))
        i += 1
      }
      /** Main Mapping */
      mainMap = propMap ++ relMap
      /** Render the Modal Logic formulas in to intoHylo format */
      writer.write("begin\n")
      for (form <- forms.take(forms.size-1)){
        writer.write(form.render(mainMap) + ";\n")
      }
      writer.write(forms.last.render(mainMap) + "\n")
      writer.write("end")
      writer.close()
    }
  }
}
