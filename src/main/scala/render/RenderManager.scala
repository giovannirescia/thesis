package render

import java.io.{File, FileOutputStream, PrintWriter}
import org.semanticweb.owlapi.model.AxiomType.{INVERSE_FUNCTIONAL_OBJECT_PROPERTY, DISJOINT_CLASSES, OBJECT_PROPERTY_DOMAIN, OBJECT_PROPERTY_RANGE, DATA_PROPERTY_DOMAIN, EQUIVALENT_CLASSES, SUBCLASS_OF, DATA_PROPERTY_RANGE, FUNCTIONAL_OBJECT_PROPERTY}
import org.semanticweb.owlapi.model._
import render.SubClassRenderer._
import render.EquivalentClassesRenderer.equivClasses
import render.DataPropertyDomain.propDomain
import render.DataPropertyRange.propRange
import render.FunctionalObjectProperty.funcProp
import render.ObjectPropertyDomain.objectPropDom
import render.ObjectPropertyRange.objectPropRange
import render.DisjointClasses.disjClass
import render.InvFuncObjProp.invFunc


object RenderManager {
  /**
    *
    * @param axioms A list of OWLAxioms to render
    * @param outFile A name for the output file. Also will write another file with the axioms that
    *                couldn't be rendered because some case was missing. E.g., if outFile = "test", "testErr"
    *                will be also created
    * @param fullPath Only true when called from a worksheet (change "/Users/giovannirescia/"), otherwise it will
    *                 create the folders output/rendered and write the output file there
    * @param verbose Will also write the axiom before it's rendered if true
    */
  def render(axioms: List[OWLAxiom], outFile: String, fullPath: Boolean = false, verbose: Boolean = false): Unit ={
    val writer = {
      if (fullPath){
        new PrintWriter(new FileOutputStream(new File(s"/Users/giovannirescia/coding/tesis/output/rendered/$outFile.txt"), false))
      }else {
        val dir = new File("output/rendered")
        dir.mkdirs()
        new PrintWriter(new FileOutputStream(new File(dir.toString + s"/$outFile.txt"), false))
      }
    }
    val writerErr = {
      if (fullPath){
        new PrintWriter(new FileOutputStream(new File(s"/Users/giovannirescia/coding/tesis/output/rendered/$outFile"+"_Err.txt"), false))
      }else {
        val dir = new File("output/rendered")
        dir.mkdirs()
        new PrintWriter(new FileOutputStream(new File(dir.toString + s"/$outFile"+"_Err.txt"), false))
      }
    }
    /** All the axiom types that couldn't be rendered*/
    var notRend: Set[String] = Set.empty
    /** All the axiom types that were successfuly rendered*/
    var rend: Set[String] = Set.empty
    var i: Int = 1
    var j: Int = 0
    for (axiom <- axioms){
      val axType = axiom.getAxiomType
      // SubClassOf Axiom Type
      if (axType == SUBCLASS_OF){
        if (verbose) writer.write("\n\n"+"Axiom "+i.toString+": " + axiom.toString+"\n\n")
        writer.write(axType.toString + "\n" + "=" * axType.toString.size + "\n")
        // Class_A <= Class_B
        simpleSubClass(axiom.asInstanceOf[OWLSubClassOfAxiom], writer)
        writer.write("\n\n" + "------------------------------" * 2 + "\n\n")
        rend += axType.toString
      }
      else if(axType == EQUIVALENT_CLASSES){
        if (verbose) writer.write("\n\n"+"Axiom "+i.toString+": " + axiom.toString+"\n\n")
        writer.write(axType.toString + "\n" + "=" * axType.toString.size + "\n")
        equivClasses(axiom.asInstanceOf[OWLEquivalentClassesAxiom], writer)
        writer.write("\n\n" + "------------------------------" * 2 + "\n\n")
        rend += axType.toString
      }
      // TODO: TRANSLATION MISSING
      else if(axType == DATA_PROPERTY_DOMAIN){
        if (verbose) writer.write("\n\n"+"Axiom "+i.toString+": " + axiom.toString+"\n\n")
        writer.write(axType.toString + "\n" + "=" * axType.toString.size + "\n")
        propDomain(axiom.asInstanceOf[OWLDataPropertyDomainAxiom], writer)
        writer.write("\n\n" + "------------------------------" * 2 + "\n\n")
        rend += axType.toString
      }
      // TODO: TRANSLATION MISSING
      else if(axType == INVERSE_FUNCTIONAL_OBJECT_PROPERTY){
        if (verbose) writer.write("\n\n"+"Axiom "+i.toString+": " + axiom.toString+"\n\n")
        writer.write(axType.toString + "\n" + "=" * axType.toString.size + "\n")
        invFunc(axiom.asInstanceOf[OWLInverseFunctionalObjectPropertyAxiom], writer)
        writer.write("\n\n" + "------------------------------" * 2 + "\n\n")
        rend += axType.toString
      }
      // TODO: TRANSLATION MISSING
      else if(axType == DISJOINT_CLASSES){
        if (verbose) writer.write("\n\n"+"Axiom "+i.toString+": " + axiom.toString+"\n\n")
        writer.write(axType.toString + "\n" + "=" * axType.toString.size + "\n")
        disjClass(axiom.asInstanceOf[OWLDisjointClassesAxiom], writer)
        writer.write("\n\n" + "------------------------------" * 2 + "\n\n")
        rend += axType.toString
      }
      // TODO: TRANSLATION MISSING
      else if(axType == DATA_PROPERTY_RANGE) {
        if (verbose) writer.write("\n\n"+"Axiom "+i.toString+": " + axiom.toString+"\n\n")
        writer.write(axType.toString + "\n" + "=" * axType.toString.size + "\n")
        propRange(axiom.asInstanceOf[OWLDataPropertyRangeAxiom], writer)
        writer.write("\n\n" + "------------------------------" * 2 + "\n\n")
        rend += axType.toString
      } // TODO: TRANSLATION MISSING
      else if(axType == OBJECT_PROPERTY_DOMAIN){
        if (verbose) writer.write("\n\n"+"Axiom "+i.toString+": " + axiom.toString+"\n\n")
        writer.write(axType.toString + "\n" + "=" * axType.toString.size + "\n")
        objectPropDom(axiom.asInstanceOf[OWLObjectPropertyDomainAxiom], writer)
        writer.write("\n\n" + "------------------------------" * 2 + "\n\n")
        rend += axType.toString
      }// TODO: TRANSLATION MISSING
      else if(axType == OBJECT_PROPERTY_RANGE){
        if (verbose) writer.write("\n\n"+"Axiom "+i.toString+": " + axiom.toString+"\n\n")
        writer.write(axType.toString + "\n" + "=" * axType.toString.size + "\n")
        objectPropRange(axiom.asInstanceOf[OWLObjectPropertyRangeAxiom], writer)
        writer.write("\n\n" + "------------------------------" * 2 + "\n\n")
        rend += axType.toString
      }
      // TODO: TRANSLATION MISSING
      else if(axType == FUNCTIONAL_OBJECT_PROPERTY){
        if (verbose) writer.write("\n\n"+"Axiom "+i.toString+": " + axiom.toString+"\n\n")
        writer.write(axType.toString + "\n" + "=" * axType.toString.size + "\n")
        funcProp(axiom.asInstanceOf[OWLFunctionalObjectPropertyAxiom], writer)
        writer.write("\n\n" + "------------------------------" * 2 + "\n\n")
        rend += axType.toString
      }
      else {
        writerErr.write("\n\n"+"Axiom "+i.toString+": " + axiom.toString+"\n\n")
        writerErr.write("\n\n" + "------------------------------" * 2 + "\n\n")
        notRend += axType.toString
        j += 1
      }
      i += 1
    }
    if (rend.nonEmpty){
      writer.write("Rendered axiom types:\n")
      for (elem <- rend){
        writer.write("\t" + elem + "\n")
      }
    }
    if (notRend.nonEmpty){
      writer.write("Unimplemented axiom types:\n")
      writerErr.write("Unimplemented axiom types:\n")
      for (elem <- notRend){
        writer.write("\t" + elem + "\n")
        writerErr.write("\t" + elem + "\n")
      }
    }

    writer.write(s"Total axioms rendered: " + (i-j-1).toString + "/" + axioms.size + "\n")
    writer.close()
    writerErr.close()
  }
}
