package fixon

import cats.syntax.functor.*
import fixon.ast.*
import higherkindness.droste.*
import higherkindness.droste.data.*
import org.apache.commons.text.StringEscapeUtils

package object ops {
  val renderToString: Algebra[Json, String] = Algebra[Json, String] {
    case JNull() => "null"
    case JBoolean(b) => b.toString
    case JNumber(n) => n.toString()
    case JString(s) => s"\"${StringEscapeUtils.escapeJson(s)}\""
    case JArray(js) => js.mkString("[", ",", "]")
    case JObject(vs) => vs.map { case (k, v) => s"\"$k\":$v"}.mkString("{", ",", "}")
  }

  def dropNulls[A]: Trans[Json, Json, A] = Trans[Json, Json, A] {
    case JObject(vs) => JObject(vs.filterNot {
      case (_, JNull()) => true
      case _ => false
    })
    case other => other
  }

  def wrap[A](keyName: String, valName: String): Trans[Json, Json, Json.J] = Trans[Json, Json, Json.J] {
    case JObject(vs) if vs.keySet == Set(keyName, valName) => JObject(vs)
    case JObject(vs) => JArray(vs.map { case (k, v) => Json.document(keyName -> Json.string(k), valName -> v)}.toVector)
    case other => other
  }
}
