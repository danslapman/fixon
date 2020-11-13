package fixon.ast

import cats._
import higherkindness.droste.data._

sealed trait Json[T]
final case class JNull[T]() extends Json[T]
final case class JBoolean[T](b: Boolean) extends Json[T]
final case class JNumber[T](n: BigDecimal) extends Json[T]
final case class JString[T](s: String) extends Json[T]
final case class JArray[T](js: Seq[T]) extends Json[T]
object JArray {
  def apply[T](v: T, vs: T*): JArray[T] = JArray(v +: vs)
}
final case class JObject[T](vs: Map[String, T]) extends Json[T]
object JObject {
  def apply[T](vs: (String, T)*): JObject[T] = JObject[T](Map(vs:_*))
}

object Json {
  type J = Fix[Json]

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