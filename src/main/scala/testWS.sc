import org.semanticweb.owlapi.model.{OWLAxiom, OWLClassExpression}

import scala.collection.JavaConversions._
import java.io.{File, FileOutputStream, PrintWriter, StringWriter}

import org.semanticweb.owlapi.functional.renderer.OWLFunctionalSyntaxRenderer
import renders.Peperoni
import renders.LabelMaker.renderManchesterSyntax
import renders.FunctionalRenderer.{renderFuncSyn => renderizador}
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.AxiomType._
val file = new File("/Users/giovannirescia/family_example.owl")

val manager = OWLManager.createOWLOntologyManager
val ontology = manager.loadOntologyFromOntologyDocument(file)

val eqclax = ontology.getAxioms(EQUIVALENT_CLASSES)

val hello = new Peperoni
val FuncSynTarget = new FileOutputStream("/Users/giovannirescia/coding/tesis/output/FunctionalSyntaxOutput.txt")
val ManchSynTarget = new PrintWriter(new File("/Users/giovannirescia/coding/tesis/output/ManchesterSyntaxOutput.txt"))
for (ax <- eqclax) ManchSynTarget.write(renderManchesterSyntax(ax, manager)+"\n")

var f = new OWLFunctionalSyntaxRenderer
val writer = new StringWriter()
var w = f.render(ontology, writer)
f.render(ontology, FuncSynTarget)
ManchSynTarget.close()

renderizador(ontology, "averahora")
