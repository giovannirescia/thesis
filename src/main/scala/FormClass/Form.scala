package FormClass

/**
  * Created by giovannirescia on 28/9/16.
  */
abstract class Form {
  def render(): String = ""
}
case class Prop(arg: String) extends Form {
  override def render(): String = arg
}
case class And(f1: Form, f2: Form) extends Form{
  override def render(): String = {
    "( " + f1.render() + " AND " + f2.render() + " )"
  }
}
case class Or(f1: Form, f2: Form) extends Form{
  override def render(): String = {
    "( " + f1.render() + " OR " + f2.render() + " )"
  }
}
case class Impl(f1: Form, f2: Form) extends Form{
  override def render(): String = {
    "( " + f1.render() + " IMPLIES " + f2.render() + " )"
  }
}
case class Neg(form: Form) extends Form{
  override def render(): String = {
    "( NOT " + form.render() + " )"
  }
}
case class Diam(r: Rel, isInv: Boolean = false, f: Form) extends Form{
  override def render(): String = {
    if (!isInv){
      s"( <$r> ${f.render()} )"
  }else{
      s"( <$r~> ${f.render()} )"
    }
  }
}
case class Box(r: Rel, isInv: Boolean = false, f: Form) extends Form{
  override def render(): String = {
    if (!isInv){
      s"( [$r] ${f.render()} )"
    }else{
      s"( [$r~] ${f.render()} )"
    }
  }
}
case class A(f: Form) extends Form{
  override def render(): String = {
    s"( A ${f.render()} )"
  }
}
case class Top() extends Form{
  override def render(): String = {
    "\u22A4"
  }
}
case class Bot() extends Form{
  override def render(): String = {
    "\u22A5"
  }
}