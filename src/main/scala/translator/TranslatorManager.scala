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
import CustomExceptions.MissingTranslationException


object TranslatorManager {
  /**
    *
    * Description Logic to Modal Logic, dl2ml for shorten
    * Given a list of axioms, translate them into Modal Logic formulas
    * 
    * @param axioms A list of OWLAxioms
    * @return A list of Modal Logic formulas
    */
  def dl2ml(axioms: List[OWLAxiom], output: String, info: PrintWriter, warnings: Boolean = false): ListBuffer[MLFormula] = {
    if (axioms.nonEmpty) {
      var result = new ListBuffer[MLFormula]()
      var count = 0
      var ax_translated = Set[String]()
      var ax_left = Set[String]()
      var ax_unhandled = Set[String]()

      val dir = new File("output/general_info/axioms_types")
      dir.mkdirs()

      val ax_t_writer = new PrintWriter(new FileOutputStream(new File(dir.toString + "/translated_" + s"$output"), false))
      val ax_l_writer = new PrintWriter(new FileOutputStream(new File(dir.toString + "/left_" + s"$output"), false))
      val ax_uh_writer = new PrintWriter(new FileOutputStream(new File(dir.toString + "/unhandled_" + s"$output"), false))

      for (axiom <- axioms) {
        count += 1
        val axType = axiom.getAxiomType
        /** TBOX AXIOMS */
        try{
          axType match {
            case SUBCLASS_OF => {ax_translated+= axType.toString; result += simpleSubClass(axiom.asInstanceOf[OWLSubClassOfAxiom])}
            case EQUIVALENT_CLASSES => {ax_translated+= axType.toString; result += equivClasses(axiom.asInstanceOf[OWLEquivalentClassesAxiom])}
            case FUNCTIONAL_OBJECT_PROPERTY => {ax_translated+= axType.toString; result += funcProp(axiom.asInstanceOf[OWLFunctionalObjectPropertyAxiom])}
            case OBJECT_PROPERTY_DOMAIN => {ax_translated+= axType.toString; result += propDomain(axiom.asInstanceOf[OWLObjectPropertyDomainAxiom])}
            case OBJECT_PROPERTY_RANGE => {ax_translated+= axType.toString; result += propRange(axiom.asInstanceOf[OWLObjectPropertyRangeAxiom])}
            case INVERSE_FUNCTIONAL_OBJECT_PROPERTY => {ax_translated+= axType.toString; result += invFunc(axiom.asInstanceOf[OWLInverseFunctionalObjectPropertyAxiom])}
            case DISJOINT_CLASSES => {ax_translated+= axType.toString; result += disjClass(axiom.asInstanceOf[OWLDisjointClassesAxiom])}
            /** Left on purpose */
            case DATA_PROPERTY_DOMAIN => ax_left += axType.toString
            case DATA_PROPERTY_RANGE => ax_left += axType.toString
            /** Unhandled cases
              * ABox and some other cases
              * */
            case _ => {ax_unhandled += axType.toString; count -= 1}
          }
        } catch {
          case c: MissingTranslationException => {count -= 1; if (warnings) println("WARNING! This axiom couldn't be translated: " + c.getMessage + "\n")}
        }
      }

      info.write("\nAxioms translated:\n\n\t" + count + "\n")

      if (ax_translated.nonEmpty) {
        ax_t_writer.write("Axiom Types Translated:\n")
        for (a <- ax_translated){
          ax_t_writer.write("\t" + a + "\n")
        }
      }
      if (ax_unhandled.nonEmpty) {
        ax_uh_writer.write("Axiom Types Unhandled:\n")
        for (a <- ax_unhandled){
          ax_uh_writer.write("\t" + a + "\n")
        }
      }
      if (ax_left.nonEmpty) {
        ax_l_writer.write("Axiom Types Left:\n")
        for (a <- ax_left){
          ax_l_writer.write("\t" + a + "\n")
        }
      }
      ax_l_writer.close()
      ax_t_writer.close()
      ax_uh_writer.close()

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
      val mapWritter = {
         if (fullPath){
          new PrintWriter(new FileOutputStream(new File(s"/Users/giovannirescia/coding/tesis/output/mappings/$outFile.mapping"), false))
        }else {
          val dir = new File("output/mappings")
          dir.mkdirs()
          new PrintWriter(new FileOutputStream(new File(dir.toString + s"/$outFile.mapping"), false))
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
      var propMap: collection.mutable.Map[String, String] = collection.mutable.Map(("string", "P2"))
      var i = 3
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
//      propMap += (("string", "P"+(props.length+2).toString))
      /** Dump the propMap for later use */
      for ((k, v) <- propMap.take(propMap.size-1)){
        mapWritter.write(v.tail + " " + k + "\n")
      }
      mapWritter.write(propMap.last._2.tail + " " + propMap.last._1)
      mapWritter.close()
      /** Main Mapping */
      mainMap = propMap ++ relMap
      /** Render the Modal Logic formulas into intoHylo format */
      writer.write("begin\n")
//      mainMap.foreach(k => writer.write(k +  "\n"))
      for (form <- forms.take(forms.size-1)){
//        writer.write(form+ "\n")
        writer.write(form.render(mainMap) + ";\n")
        writer.write("")
      }

      writer.write(forms.last.render(mainMap) + "\n")
      writer.write("end")
      writer.close()
      //mainMap.filter{case (k,v)=>v.startsWith("R")}.foreach(println)
    } else {
      throw new NoSuchElementException("The formula list is empty")
    }
  }
}
