package formulae

import scala.collection.mutable.ListBuffer
import org.semanticweb.owlapi.model.AxiomType.{DATA_PROPERTY_DOMAIN, EQUIVALENT_CLASSES, SUBCLASS_OF, DATA_PROPERTY_RANGE, FUNCTIONAL_OBJECT_PROPERTY, CLASS_ASSERTION, OBJECT_PROPERTY_ASSERTION, DATA_PROPERTY_ASSERTION}
import org.semanticweb.owlapi.model._
import formulae.SubClassFormulae._
import formulae.EquivalentClassesFormulae._
import formulae.FunctionalObjectPropertyFormulae._
import formulae.DataPropertyDomain._
import FormClass._
import formulae.DataPropertyRange._
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
}
