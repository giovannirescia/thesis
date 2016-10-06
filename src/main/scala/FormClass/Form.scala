package FormClass
import scala.collection.mutable

/**
  * Created by giovannirescia on 28/9/16.
  */
abstract class Form {
  def render(map: mutable.Map[String, String]): String = ""
}
case class Prop(arg: String) extends Form {
  override def render(map: mutable.Map[String, String]): String = map.get(arg).head
}
case class And(f1: Form, f2: Form) extends Form{
  override def render(map: mutable.Map[String, String]): String = {
    "( " + f1.render(map) + " ^ " + f2.render(map) + " )"
  }
}
case class Or(f1: Form, f2: Form) extends Form{
  override def render(map: mutable.Map[String, String]): String = {
    "( " + f1.render(map) + " v " + f2.render(map) + " )"
  }
}
case class Impl(f1: Form, f2: Form) extends Form{
  override def render(map: mutable.Map[String, String]): String = {
    "( " + f1.render(map) + " --> " + f2.render(map) + " )"
  }
}
case class Neg(form: Form) extends Form{
  override def render(map: mutable.Map[String, String]): String = {
    "( - " + form.render(map) + " )"
  }
}
case class Diam(r: Rel, f: Form) extends Form{
  override def render(map: mutable.Map[String, String]): String = {
      s"( <${r.render(map)}>${f.render(map)} )"
    }
}
case class Box(r: Rel, f: Form) extends Form{
  override def render(map: mutable.Map[String, String]): String = {
      s"( [${r.render(map)}]${f.render(map)} )"
    }
}


case class IDiam(r: Rel, f: Form) extends Form{
  override def render(map: mutable.Map[String, String]): String = {
      // TODO: FIX
      s"( <-${r.render(map)}>${f.render(map)} )"
  }
}
case class IBox(r: Rel, f: Form) extends Form{
  override def render(map: mutable.Map[String, String]): String = {
    // TODO: FIX
    s"( -[${r.render(map)}]${f.render(map)} )"
    }
}

case class A(f: Form) extends Form{
  override def render(map: mutable.Map[String, String]): String = {
    // TODO: FIX UNIVERSAL OPERATOR
    //s"( A ${f.render(map)} )"
    s"( A${f.render(map)} )"
  }
}
case class Top() extends Form{
  override def render(map: mutable.Map[String, String]): String = {
    " true "
  }
}
case class Bot() extends Form{
  override def render(map: mutable.Map[String, String]): String = {
    " false "
  }
}