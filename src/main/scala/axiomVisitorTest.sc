import java.io.{File, StringWriter}
import java.util.HashMap

import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterOWLSyntaxObjectRenderer
import org.semanticweb.owlapi.model.AxiomType._
import org.semanticweb.owlapi.model.{OWLAxiom, OWLAxiomVisitor, OWLClassExpression, OWLEquivalentClassesAxiom}
import org.semanticweb.owlapi.util.AnnotationValueShortFormProvider
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom
import scala.collection.JavaConverters._
import scala.collection.JavaConversions._
import java.io.{File, FileOutputStream, PrintWriter, StringWriter}

import org.semanticweb.owlapi.functional.renderer.OWLFunctionalSyntaxRenderer
import renders.Peperoni
import renders.LabelMaker.renderManchesterSyntax
import renders.FunctionalRenderer.{renderFuncSyn => renderizador}
import renders.ManchesterRenderer.renderManchSyn
import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model.AxiomType._
import org.semanticweb.owlapi.model.AxiomType.SUBCLASS_OF
val file = new File("/Users/giovannirescia/family_example.owl")
val manager = OWLManager.createOWLOntologyManager
val ontology = manager.loadOntologyFromOntologyDocument(file)
var eqclax = ontology.getAxioms(SUBCLASS_OF)
var ax: OWLSubClassOfAxiom =  eqclax.head

val writer1 = new StringWriter()
val writer2 = new StringWriter()
val rdfsLabel = OWLManager.getOWLDataFactory.getRDFSLabel
val labelProvider = new AnnotationValueShortFormProvider(List(rdfsLabel).asJava, new HashMap(), manager)
val renderer = new ManchesterOWLSyntaxObjectRenderer(writer1, labelProvider)
val renderer2 = new ManchesterOWLSyntaxObjectRenderer(writer2, labelProvider)

renderer.visit(ax: OWLSubClassOfAxiom)
renderer2.visit(ax: OWLSubClassOfAxiom)
renderManchSyn(ax, manager, "manchesterSplit")
ax.getNNF.accept(renderer:OWLAxiomVisitor)
writer1
ax.accept(renderer2:OWLAxiomVisitor)
writer2