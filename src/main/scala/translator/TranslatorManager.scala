package translator

import java.io.{File, FileOutputStream, PrintWriter}
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import org.semanticweb.owlapi.model.AxiomType._
import org.semanticweb.owlapi.model._
import translator.SubClassFormulae._
import translator.EquivalentClassesFormulae._
import translator.FunctionalObjectPropertyFormulae._
import translator.ObjectPropertyDomain._
import ModalLogicFormulaClasses._
import translator.ObjectPropertyRange._
import org.semanticweb.owlapi.model.parameters.Imports
import translator.InvFuncObjProp.invFunc
import translator.DisjointClasses.disjClass


object TranslatorManager {
  /**
    *
    * Description Logic to Modal Logic, dl2ml for shorten
    * Given a list of axioms, translate them into Modal Logic formulas
    * 
    * @param axioms A list of OWLAxioms
    * @return A list of Modal Logic formulas
    */
  def dl2ml(axioms: List[OWLAxiom]): ListBuffer[MLFormula] = {
    if (axioms.nonEmpty) {
      var result = new ListBuffer[MLFormula]()
      for (axiom <- axioms) {
        val axType = axiom.getAxiomType
        /** TBOX AXIOMS */
        axType match {
          case SUBCLASS_OF => result += simpleSubClass(axiom.asInstanceOf[OWLSubClassOfAxiom])
          case EQUIVALENT_CLASSES => result += equivClasses(axiom.asInstanceOf[OWLEquivalentClassesAxiom])
          case FUNCTIONAL_OBJECT_PROPERTY => result += funcProp(axiom.asInstanceOf[OWLFunctionalObjectPropertyAxiom])
          case OBJECT_PROPERTY_DOMAIN => result += propDomain(axiom.asInstanceOf[OWLObjectPropertyDomainAxiom])
          case OBJECT_PROPERTY_RANGE => result += propRange(axiom.asInstanceOf[OWLObjectPropertyRangeAxiom])
          case INVERSE_FUNCTIONAL_OBJECT_PROPERTY => result += invFunc(axiom.asInstanceOf[OWLInverseFunctionalObjectPropertyAxiom])
          case DISJOINT_CLASSES => result += disjClass(axiom.asInstanceOf[OWLDisjointClassesAxiom])
          /** Unhandled cases
            * ABox and some other cases
            * */
          case _ => {}
        }
      }
      result
    } else {
      throw new NoSuchElementException("The axiom list is empty")
    }
  }
  

  /**
    * 
    * KCNF
    */
  def ml2kcnf(form: MLFormula): MLFormula = form match {
    case Prop(p) => Prop(p)
    case Neg(Prop(p)) => Neg(Prop(p))
    case Neg(Top()) => And(Prop("string"), Neg(Prop("string")))
    case Neg(Neg(f: MLFormula)) => ml2kcnf(f)
    case Top() => Or(Prop("string"), Neg(Prop("string")))
    case And(f1, f2) => And(ml2kcnf(f1), ml2kcnf(f2))
    case Neg(And(f1,f2)) => Or(ml2kcnf(Neg(f1)), ml2kcnf(Neg(f2)))
    case Or(f1, f2) => Or(ml2kcnf(f1), ml2kcnf(f2))
    case Neg(Or(f1, f2)) => And(ml2kcnf(Neg(f1)), ml2kcnf(Neg(f2)))
    case Impl(f1, f2) => Or(ml2kcnf(Neg(f1)), ml2kcnf(f2))
    case Neg(Impl(f1, f2)) => And(ml2kcnf(f1), ml2kcnf(Neg(f2)))
    case Iif(f1, f2) => And(ml2kcnf(Impl(f1, f2)), ml2kcnf(Impl(f2, f1)))
    case Neg(Iif(f1, f2)) => Or(ml2kcnf(Neg(Impl(f1,f2))), ml2kcnf(Neg(Impl(f2,f1))))
    case Diam(r, f) => Neg(Box(r, ml2kcnf(Neg(f))))
    case Neg(Diam(r, f)) => Box(r, ml2kcnf(Neg(f)))
    case Box(r, f) => Box(r, ml2kcnf(f))
    case Neg(Box(r, f)) => Neg(Box(r, ml2kcnf(f)))
    case A(f) => A(ml2kcnf(f))
    case IDiam(r, f) => Neg(IBox(r, ml2kcnf(Neg(f))))
    case Neg(IDiam(r, f)) => IBox(r, ml2kcnf(Neg(f)))
    case IBox(r, f) => IBox(r, ml2kcnf(f))
    case Neg(IBox(r, f)) => Neg(IBox(r, ml2kcnf(f)))
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
    if (forms.nonEmpty) {
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
      /** Render the Modal Logic formulas into intoHylo format */
      writer.write("begin\n")
      for (form <- forms.take(forms.size-1)){
        writer.write(form.render(mainMap) + ";\n")
      }

      writer.write(forms.form.render(mainMap) + "\n")
      writer.write("end")
      writer.close()
      //mainMap.filter{case (k,v)=>v.startsWith("R")}.foreach(println)
    } else {
      throw new NoSuchElementException("The formula list is empty")
    }
  }
}
