package formulae
import java.io.{File, FileOutputStream, PrintWriter}

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import org.semanticweb.owlapi.model.AxiomType.{CLASS_ASSERTION, DATA_PROPERTY_ASSERTION, DATA_PROPERTY_DOMAIN, DATA_PROPERTY_RANGE, EQUIVALENT_CLASSES, FUNCTIONAL_OBJECT_PROPERTY, OBJECT_PROPERTY_ASSERTION, SUBCLASS_OF}
import org.semanticweb.owlapi.model._
import formulae.SubClassFormulae._
import formulae.EquivalentClassesFormulae._
import formulae.FunctionalObjectPropertyFormulae._
import formulae.DataPropertyDomain._
import FormClass._
import formulae.DataPropertyRange._
import org.semanticweb.owlapi.model.parameters.Imports
/**
  * Created by giovannirescia on 26/9/16.
  */
object FormulaeManager {
  def formulate(axioms: List[OWLAxiom]): ListBuffer[Form] ={
    var result = new ListBuffer[Form]()

    for (axiom <- axioms){
      val axType = axiom.getAxiomType

      // <TBOX_AXIOMS>
      // SubClassOf Axiom Type
      if (axType == SUBCLASS_OF){
        result += simpleSubClass(axiom.asInstanceOf[OWLSubClassOfAxiom])
      }
      else if(axType == EQUIVALENT_CLASSES){
        result += equivClasses(axiom.asInstanceOf[OWLEquivalentClassesAxiom])
      }
      else if(axType == FUNCTIONAL_OBJECT_PROPERTY){
        result += funcProp(axiom.asInstanceOf[OWLFunctionalObjectPropertyAxiom])
      }

      else if(axType == DATA_PROPERTY_DOMAIN){
        result += propDomain(axiom.asInstanceOf[OWLDataPropertyDomainAxiom])
      }
      else if(axType == DATA_PROPERTY_RANGE){
        result += propRange(axiom.asInstanceOf[OWLDataPropertyRangeAxiom])
      }
      /*
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
          */
    }
    result
  }

  def render(forms: ListBuffer[Form], ontology: OWLOntology, outFile: String, fullPath: Boolean = false): Unit ={
    val classes = ontology.getClassesInSignature(Imports.INCLUDED).toList
    val individuals = ontology.getIndividualsInSignature(Imports.INCLUDED).toList
    val objprop = ontology.getObjectPropertiesInSignature(Imports.INCLUDED).toList
    val dataprop = ontology.getDataPropertiesInSignature(Imports.INCLUDED).toList
    val props = new ListBuffer[String]()
    val rels = new ListBuffer[String]()

    val writer = {
      if (fullPath){
        new PrintWriter(new FileOutputStream(new File(s"/Users/giovannirescia/coding/tesis/output/translations/intohylo/$outFile.intohylo"), false))
      }else {
        val dir = new File("output/translations/intohylo")
        dir.mkdirs()
        new PrintWriter(new FileOutputStream(new File(dir.toString + s"/$outFile.intohylo"), false))
      }
    }
    for (x <- objprop ++ dataprop){
      rels += x.getIRI.getShortForm
    }
    for (x <- classes ++ individuals){
      props += x.getIRI.getShortForm
    }
    var relMap: collection.mutable.Map[String, String] = collection.mutable.Map.empty
    var mainMap: collection.mutable.Map[String, String] = collection.mutable.Map.empty
    // for initialization
    var propMap: collection.mutable.Map[String, String] = collection.mutable.Map("string" -> "P1")
    var i = 2

    var j = 1

    for (x <- rels){
      relMap += ((x.toString, "R"+j.toString))
      j += 1
    }

    for (x <- props){
      propMap += ((x.toString , "P"+i.toString))
      i += 1
    }
    mainMap = propMap ++ relMap
    writer.write("begin\n")
    for (form <- forms.take(forms.size-1)){
      writer.write(form.render(mainMap) + ";\n")
    }
    writer.write(forms.last.render(mainMap) + "\n")
    writer.write("end")
    writer.close()
  }
}
