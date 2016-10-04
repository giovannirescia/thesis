package renders

import java.io.{File, FileOutputStream, PrintWriter}

import org.semanticweb.owlapi.model.AxiomType.{DATA_PROPERTY_DOMAIN, EQUIVALENT_CLASSES, SUBCLASS_OF, DATA_PROPERTY_RANGE, FUNCTIONAL_OBJECT_PROPERTY}
import org.semanticweb.owlapi.model._
import renders.SubClassRenderer._
import renders.EquivalentClassesRenderer._
import renders.DataPropertyDomain._
import renders.DataPropertyRange._
import renders.FunctionalObjectProperty._
/**
  * Created by giovannirescia on 24/9/16.
  */
object RendererManager {
  def render(axioms: List[OWLAxiom], outFile: String): Unit ={
    val dir = new File("output-log/rendered")
    dir.mkdirs()
    val writer = new PrintWriter(new FileOutputStream(new File(dir.toString +  s"/$outFile.txt"),false))
    var notRend: Set[String] = Set.empty
    var rend: Set[String] = Set.empty
    var i: Int = 0
    for (axiom <- axioms){
      writer.write("\n\n"+i.toString+" " + axiom.toString+"\n\n")
      val axType = axiom.getAxiomType
      // SubClassOf Axiom Type
      if (axType == SUBCLASS_OF){

        writer.write(axType.toString + "\n" + "=" * axType.toString.size + "\n")
        // Class_A <= Class_B
        simpleSubClass(axiom.asInstanceOf[OWLSubClassOfAxiom], writer)

        writer.write("\n\n" + "------------------------------" * 2 + "\n\n")
        rend += axType.toString

      }
      else if(axType == EQUIVALENT_CLASSES){
        writer.write(axType.toString + "\n" + "=" * axType.toString.size + "\n")
        equivClasses(axiom.asInstanceOf[OWLEquivalentClassesAxiom], writer)


        writer.write("\n\n" + "------------------------------" * 2 + "\n\n")
        rend += axType.toString

      }
      else if(axType == DATA_PROPERTY_DOMAIN){
        writer.write(axType.toString + "\n" + "=" * axType.toString.size + "\n")
        propDomain(axiom.asInstanceOf[OWLDataPropertyDomainAxiom], writer)
        writer.write("\n\n" + "------------------------------" * 2 + "\n\n")
        rend += axType.toString

      }
      else if(axType == DATA_PROPERTY_RANGE){
        writer.write(axType.toString + "\n" + "=" * axType.toString.size + "\n")
        propRange(axiom.asInstanceOf[OWLDataPropertyRangeAxiom], writer)
        writer.write("\n\n" + "------------------------------" * 2 + "\n\n")
        rend += axType.toString

      }
      else if(axType == FUNCTIONAL_OBJECT_PROPERTY){
        writer.write(axType.toString + "\n" + "=" * axType.toString.size + "\n")
        funcProp(axiom.asInstanceOf[OWLFunctionalObjectPropertyAxiom], writer)
        writer.write("\n\n" + "------------------------------" * 2 + "\n\n")
        rend += axType.toString

      }
      else {
        notRend += axType.toString
        writer.write("@!#"*40 + "\n\n")
      }
      i += 1
    }
    writer.write("Rendered axiom types:\n")
    for (elem <- rend){
      writer.write("\t" + elem + "\n")
    }
    writer.write("Unimplemented axiom types:\n")
    for (elem <- notRend){
      writer.write("\t" + elem + "\n")
    }
    writer.write(s"Total axioms rendered: $i")
    writer.close()
  }
}
