package fixon.ast

import cats.*
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

  implicit val functorForJson: Functor[Json] = new Functor[Json] {
    override def map[A, B](fa: Json[A])(f: A => B): Json[B] =
      fa match {
        case JNull() => JNull()
        case JBoolean(b) => JBoolean(b)
        case JNumber(n) => JNumber(n)
        case JString(s) => JString(s)
        case JArray(js) => JArray(js.map(f))
        case JObject(vs) => JObject(vs.view.mapValues(f).toMap)
      }
  }
}