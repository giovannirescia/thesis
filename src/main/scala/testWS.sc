import org.semanticweb.owlapi.model.{OWLAxiom, OWLClassExpression}
import scala.collection.JavaConversions._
import java.io.{File, FileOutputStream, PrintWriter, StringWriter}
import org.semanticweb.owlapi.model.parameters.Imports
import org.semanticweb.owlapi.functional.renderer.OWLFunctionalSyntaxRenderer
import renders.Peperoni
import renders.LabelMaker.renderManchesterSyntax
import renders.FunctionalRenderer.{renderFuncSyn => renderizador}
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.AxiomType._
import org.semanticweb.owlapi.util.DefaultPrefixManager


val file = new File("/Users/giovannirescia/coding/tesis/ontologies/family_example.owl")
val file2 = new File("/Users/giovannirescia/coding/IdeaProjects/prueba/ontologies/galen/galen.owl")
val manager = OWLManager.createOWLOntologyManager
val ontology2 = manager.loadOntologyFromOntologyDocument(file2)
val ontology = manager.loadOntologyFromOntologyDocument(file)
val eqclax = ontology.getAxioms(EQUIVALENT_CLASSES)
val tbox = ontology.getTBoxAxioms(Imports.INCLUDED)
var axiom = eqclax.head
axiom.getAxiomType
val hello = new Peperoni
val FuncSynTarget = new FileOutputStream("/Users/giovannirescia/coding/tesis/output/FunctionalSyntaxOutput.txt")
val FuncSynTarget2 = new FileOutputStream("/Users/giovannirescia/coding/tesis/output/FunctionalSyntaxOutput2.txt")

val ManchSynTarget = new PrintWriter(new File("/Users/giovannirescia/coding/tesis/output/ManchesterSyntaxOutput.txt"))
for (ax <- eqclax) ManchSynTarget.write(renderManchesterSyntax(ax, manager)+"\n")
ontology.getOntologyID
var f = new OWLFunctionalSyntaxRenderer
val writer = new StringWriter()
var w = f.render(ontology, writer)
f.render(ontology2, FuncSynTarget2)
f.render(ontology, FuncSynTarget)
ManchSynTarget.close()

renderizador(ontology, "galen")

axiom.splitToAnnotatedPairs()

axiom.asPairwiseAxioms()
ontology.getOntologyID.getDefaultDocumentIRI
ontology.getOntologyID.getVersionIRI
val q = ontology.getClassesInSignature(Imports.INCLUDED)
for (ax <- eqclax) {
  //print(ax.getAxiomType)
  var v = ax.getClassExpressions
  //println(v.head.get)
  println(v.toList(1))
}

val iri = ontology2.getOntologyID.getOntologyIRI
val fact = manager.getOWLDataFactory
//val pm = new DefaultPrefixManager(iri+'#')
var ax2 = ontology2.getAxioms(EQUIVALENT_CLASSES).head



println("salamin\n")
eqclax.toList(0).getNamedClasses.head.getIRI.getShortForm
q
for (a <- q){
  println(a.getIRI.getShortForm)
}
// TODO: esta es la posta
axiom.getClassExpressions.head.asOWLClass().getIRI.getShortForm
axiom.getClassExpressions.toList(1).isAnonymous
