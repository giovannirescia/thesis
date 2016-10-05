package renders

import java.io.PrintWriter
import org.semanticweb.owlapi.model.OWLInverseFunctionalObjectPropertyAxiom

/**
  * Created by giovannirescia on 4/10/16.
  */
object invFuncObjProp {
  def invFunc(axiom: OWLInverseFunctionalObjectPropertyAxiom, writer: PrintWriter): Unit ={
    writer.write("Inverse Functional: " + axiom.getProperty.asOWLObjectProperty().getIRI.getShortForm)
  }
}