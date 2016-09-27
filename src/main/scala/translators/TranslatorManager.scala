package translators

import java.io.{File, FileOutputStream, PrintWriter}
import org.semanticweb.owlapi.model.AxiomType.{DATA_PROPERTY_DOMAIN, EQUIVALENT_CLASSES, SUBCLASS_OF, DATA_PROPERTY_RANGE, FUNCTIONAL_OBJECT_PROPERTY, CLASS_ASSERTION, OBJECT_PROPERTY_ASSERTION, DATA_PROPERTY_ASSERTION}
import org.semanticweb.owlapi.model._
import translators.EquivalentClassesTranslator._
import translators.SubClassTranslator._
import translators.FunctionalObjectPropertyTranslator._
import translators.ClassAssert._
import translators.PropAssert._
/**
  * Created by giovannirescia on 26/9/16.
  */
object TranslatorManager {
  def translate(axioms: List[OWLAxiom], outFile: String): Unit ={
    val writer = new PrintWriter(new FileOutputStream(new File(s"/Users/giovannirescia/coding/tesis/output/translations/$outFile.txt"),false))
    var notTranslated: Set[String] = Set.empty
    var translated: Set[String] = Set.empty
    var i: Int = 0
    for (axiom <- axioms){
      writer.write("\n\n"+i.toString+" " + axiom.toString+"\n\n")
      val axType = axiom.getAxiomType

      // <TBOX_AXIOMS>
      // SubClassOf Axiom Type
      if (axType == SUBCLASS_OF){
        writer.write(axType.toString + "\n" + "=" * axType.toString.size + "\n")
        simpleSubClass(axiom.asInstanceOf[OWLSubClassOfAxiom], writer)
        writer.write("\n\n" + "------------------------------" * 2 + "\n\n")
        translated += axType.toString
      }
      else if(axType == EQUIVALENT_CLASSES){
        writer.write(axType.toString + "\n" + "=" * axType.toString.size + "\n")
        equivClasses(axiom.asInstanceOf[OWLEquivalentClassesAxiom], writer)
        writer.write("\n\n" + "------------------------------" * 2 + "\n\n")
        translated += axType.toString
      }
      /*
      else if(axType == DATA_PROPERTY_DOMAIN){
        writer.write(axType.toString + "\n" + "=" * axType.toString.size + "\n")
        propDomain(axiom.asInstanceOf[OWLDataPropertyDomainAxiom], writer)
        writer.write("\n\n" + "------------------------------" * 2 + "\n\n")
        translated += axType.toString
      }
      else if(axType == DATA_PROPERTY_RANGE){
        writer.write(axType.toString + "\n" + "=" * axType.toString.size + "\n")
        propRange(axiom.asInstanceOf[OWLDataPropertyRangeAxiom], writer)
        writer.write("\n\n" + "------------------------------" * 2 + "\n\n")
        translated += axType.toString
      }*/
      else if(axType == FUNCTIONAL_OBJECT_PROPERTY){
        writer.write(axType.toString + "\n" + "=" * axType.toString.size + "\n")
        funcProp(axiom.asInstanceOf[OWLFunctionalObjectPropertyAxiom], writer)
        writer.write("\n\n" + "------------------------------" * 2 + "\n\n")
        translated += axType.toString
      }
      // </TBOX_AXIOMS>

      // <ABOX_AXIOMS>
      else if(axType == CLASS_ASSERTION){
        writer.write(axType.toString + "\n" + "=" * axType.toString.size + "\n")
        classAssert(axiom.asInstanceOf[OWLClassAssertionAxiom], writer)
        writer.write("\n\n" + "------------------------------" * 2 + "\n\n")
        translated += axType.toString
      }
      else if(axType == OBJECT_PROPERTY_ASSERTION){
        writer.write(axType.toString + "\n" + "=" * axType.toString.size + "\n")
        propAssert(axiom.asInstanceOf[OWLObjectPropertyAssertionAxiom], writer)
        writer.write("\n\n" + "------------------------------" * 2 + "\n\n")
        translated += axType.toString
      }


      // </ABOX_AXIOMS>
      else {
        notTranslated += axType.toString
        writer.write("@!#"*40 + "\n\n")
        i -= 1
      }
      i += 1
    }
    writer.write("Translated axiom types:\n")
    for (elem <- translated){
      writer.write("\t" + elem + "\n")
    }
    writer.write("Unimplemented axiom types:\n")
    for (elem <- notTranslated){
      writer.write("\t" + elem + "\n")
    }
    writer.write(s"Total axioms translated: $i")
    writer.close()
  }
}
