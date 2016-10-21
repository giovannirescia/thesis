package translator
import sys.process._
import org.scalatest.FunSuite
import java.io._

import scala.language.postfixOps
import org.semanticweb.owlapi.model.parameters.Imports
import org.semanticweb.owlapi.apibinding.OWLManager
import ModalLogicFormulaClasses._
import org.semanticweb.owlapi.model.AxiomType._
import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer


class TranslatorManager$Test extends FunSuite {
  val f1 = new File(getCurrentDirectory + "family_example.owl")
  val f2 = new File(getCurrentDirectory + "wine_3.owl")
  val f3 = new File(getCurrentDirectory + "dolce.owl")
  val m = OWLManager.createOWLOntologyManager()

  val fam = m.loadOntologyFromOntologyDocument(f1)
  val wine = m.loadOntologyFromOntologyDocument(f2)
  val dolce = m.loadOntologyFromOntologyDocument(f3)

  val famtbox = fam.getAxioms(SUBCLASS_OF, Imports.INCLUDED).toIndexedSeq
  val winetbox = wine.getTBoxAxioms(Imports.INCLUDED).toList
  val dolcetbox = dolce.getTBoxAxioms(Imports.INCLUDED).toList


  val classes = fam.getClassesInSignature(Imports.INCLUDED).toList ++
    wine.getClassesInSignature(Imports.INCLUDED).toList ++
    dolce.getClassesInSignature(Imports.INCLUDED).toList
  val individuals = fam.getIndividualsInSignature(Imports.INCLUDED).toList ++
    wine.getIndividualsInSignature(Imports.INCLUDED).toList ++
    dolce.getIndividualsInSignature(Imports.INCLUDED).toList
  val objprop = dolce.getObjectPropertiesInSignature(Imports.INCLUDED).toList++
    wine.getObjectPropertiesInSignature(Imports.INCLUDED).toList++
    fam.getObjectPropertiesInSignature(Imports.INCLUDED).toList
  val dataprop = dolce.getDataPropertiesInSignature(Imports.INCLUDED).toList++
    fam.getDataPropertiesInSignature(Imports.INCLUDED).toList++
    wine.getDataPropertiesInSignature(Imports.INCLUDED).toList

  val props = new ListBuffer[String]()
  val rels = new ListBuffer[String]()

  for (x <- objprop ++ dataprop){
    rels += x.getIRI.getShortForm
  }
  for (x <- classes ++ individuals){
    props += x.getIRI.getShortForm
  }

  /** The maps vars */
  var mainMap: collection.mutable.Map[String, String] = collection.mutable.Map.empty
  var relMap: collection.mutable.Map[String, String] = collection.mutable.Map.empty
  // for initialization
  var propMap: collection.mutable.Map[String, String] = collection.mutable.Map("string" -> "P1")
  var i = 2
  var j = 1
  /** The mappings */
  for (x <- rels){
    relMap += ((x.toString, "R"+j.toString))
    j += 1
  }
  for (x <- props){
    propMap += ((x.toString , "P"+i.toString))
    i += 1
  }

  mainMap = propMap ++ relMap

  /** Axioms to translate... */
  val f9 = famtbox.get(0)
  println(f9)
  val w121 = winetbox(121)
  val d221 = dolcetbox(221)
 // val d241 = dolcetbox(241)
  val d376 = dolcetbox(376)
  /** Hand-made translations */
  val fam9t = A(Impl(Prop("Parent"), Prop("Person")))
  val dolce221t = A(Impl(Prop("physical-body"), Box(R("proper-part"), Diam(R("member-of"),
    And(Prop("collection"), Box(R("unified-by"), Neg(Or(Prop("plan"), Prop("project")))))))))
  val aux1: MLFormula = Prop("physical-realization")
  val aux2: MLFormula = And(Prop("spatio-temporal-particular"),
    And(Diam(R("realizes"),Prop("non-physical-object")), Or(Prop("physical-endurant"),
      Or(Prop("physical-quality"), Or(And(Prop("perdurant"), Diam(R("participant"),
        Prop("physical-endurant"))), And(Prop("situation"), Diam(R("setting-for"),
        Or(Prop("physical-endurant"), Or(Prop("physical-quality"), Diam(R("participant"),
          Prop("physical-endurant")))))))))))
  val dolce376t: MLFormula =
    And(A(Impl(aux1,aux2)), A(Impl(aux2,aux1)))

  val mapCheck: List[(MLFormula, MLFormula)] = List(
    (TranslatorManager.dl2ml(d376::Nil).head , dolce376t),
    (TranslatorManager.dl2ml(f9::Nil).head , fam9t),
    (TranslatorManager.dl2ml(d221::Nil).head , dolce221t)
  )

  var ys : collection.mutable.ListBuffer[Int] = collection.mutable.ListBuffer.empty

  //  val r = "rm n1p.bliss n1p.map n1p.stats n2p.bliss n2p.map n2p.stats" !!;


  test("testDl2ml") {
    for ((t1, t2) <- mapCheck){
     check(t1, t2)
    }
    val q = "mv n1p.bliss n1p.map n1p.stats n2p.bliss n2p.map n2p.stats output/test/" !;
  }
  def check(a: MLFormula,b:MLFormula):Unit= {
    val writer1 = new PrintWriter(new FileOutputStream(new File("output/test/n1.txt")))
    val writer2 = new PrintWriter(new FileOutputStream(new File("output/test/n2.txt")))

    writer1.write("begin\n"+a.render(mainMap) + "\nend"); writer1.close()
    writer2.write("begin\n"+b.render(mainMap) + "\nend"); writer2.close()

    val res1 = "./tools/builds/tools/kcnf_converter output/test/n1.txt" !!;
    val res2 = "./tools/builds/tools/kcnf_converter output/test/n2.txt" !!;


    val w1 = new PrintWriter(new FileOutputStream(new File("output/test/n1p.txt")))
    val w2 = new PrintWriter(new FileOutputStream(new File("output/test/n2p.txt")))
    w1.write(res1); w1.close()
    w2.write(res2); w2.close()

    val r1 = "./tools/builds/tools/sy4ncl -f output/test/n1p.txt -t 0" !!;
    val r2 = "./tools/builds/tools/sy4ncl -f output/test/n2p.txt -t 0" !!;
    val o1 = "cmp n1p.stats n2p.stats"!;
    val o2 = "cmp n1p.map n2p.map"!;

    assert(o1 == 0)
    assert(o2 == 0)
  }
  def getCurrentDirectory: String = {
    new java.io.File(".").getCanonicalPath + "/ontologies/"
  }
}
