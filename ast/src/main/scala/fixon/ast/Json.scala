package fixon.ast

import alleycats.std.map.*
import cats.*
import cats.syntax.traverse.*
import higherkindness.droste.data.*

sealed trait Json[T]
final case class JNull[T]() extends Json[T]
final case class JBoolean[T](b: Boolean) extends Json[T]
final case class JNumber[T](n: BigDecimal) extends Json[T]
final case class JString[T](s: String) extends Json[T]
final case class JArray[T](js: Vector[T]) extends Json[T]
object JArray {
  def apply[T](v: T, vs: T*): JArray[T] = JArray((v +: vs).to(Vector))
}
final case class JObject[T](vs: Map[String, T]) extends Json[T]
object JObject {
  def apply[T](vs: (String, T)*): JObject[T] = JObject[T](Map(vs*))
}

object Json {
  type J = Fix[Json]

  val `null`: J = Fix(JNull[J]())
  def boolean(value: Boolean): J = Fix(JBoolean[J](value))
  def string(value: String): J = Fix(JString[J](value))
  def number(value: BigDecimal): J = Fix(JNumber[J](value))
  def array(values: Seq[J]): J = Fix(JArray(values.to(Vector)))
  def array(h: J, t: J*):J = Fix(JArray(h, t*))
  def document(values: Map[String, J]): J = Fix(JObject(values))
  def document(pairs: Seq[(String, J)]): J = Fix(JObject(pairs *))
  def document(h: (String,J), t: (String, J)*): J = Fix(JObject((h +: t) *))

  implicit val jsonInstances: Traverse[Json] = new Traverse[Json] {
    override def map[A, B](fa: Json[A])(f: A => B): Json[B] =
      fa match {
        case JNull() => JNull()
        case JBoolean(b) => JBoolean(b)
        case JNumber(n) => JNumber(n)
        case JString(s) => JString(s)
        case JArray(js) => JArray(js.map(f))
        case JObject(vs) => JObject(vs.view.mapValues(f).toMap)
      }

    def foldLeft[A, B](fa: Json[A], b: B)(f: (B, A) => B): B =
      fa match {
        case JNull()      => b
        case JBoolean(_)  => b
        case JString(_)   => b
        case JNumber(_)   => b
        case JArray(js)   => js.foldLeft(b)(f)
        case JObject(vs)  => vs.foldLeft(b) { case (eb, (_, a)) => f(eb, a) }
      }

    def foldRight[A, B](fa: Json[A], lb: Eval[B])(f: (A, Eval[B]) => Eval[B]): Eval[B] =
      fa match {
        case JNull()      => lb
        case JBoolean(_)  => lb
        case JNumber(_)   => lb
        case JString(_)   => lb
        case JArray(js)   => js.foldRight(lb)(f)
        case JObject(vs)  => vs.foldRight(lb) { case ((_, a), eb) => f(a, eb) }
      }

    def traverse[G[_], A, B](fa: Json[A])(f: A => G[B])(implicit G: Applicative[G]): G[Json[B]] =
      fa match {
        case JNull()           => G.pure(JNull())
        case b @ JBoolean(_) => G.pure(b.asInstanceOf[Json[B]])
        case n @ JNumber(_)  => G.pure(n.asInstanceOf[Json[B]])
        case s @ JString(_)  => G.pure(s.asInstanceOf[Json[B]])
        case JArray(js)    => G.map(js.traverse(f))(js2 => JArray(js2))
        case JObject(vs) => G.map(vs.traverse(f))(vs2 => JObject(vs2))
      }
  }
}