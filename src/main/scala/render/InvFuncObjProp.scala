package render

import java.io.PrintWriter
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom

object InvFuncObjProp {
  /**
    *
    * @param axiom An OWLInverseFunctionalObjectPropertyAxiom
    * @param writer A PrintWriter to write
    */
  def invFunc(axiom: OWLInverseFunctionalObjectPropertyAxiom, writer: PrintWriter): Unit ={
    writer.write("Inverse Functional: " + axiom.getProperty.asOWLObjectProperty().getIRI.getShortForm)
  }
}