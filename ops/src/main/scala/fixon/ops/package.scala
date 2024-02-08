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

  def wrap(keyName: String, valName: String): CVCoalgebra[Json, Json.J] = CVCoalgebra[Json, Json.J] {
    case Fix(jo @ JObject(vs)) if vs.keySet == Set(keyName, valName) => (jo: Json[Json.J]).map(Coattr.pure)
    case Fix(JObject(vs)) => JArray(vs.map {
      case (k, v) => Coattr.pure[Json, Json.J](Json.document(keyName -> Json.string(k), valName -> v))
    }.toVector)
    case json => Fix.un(json).map(Coattr.pure)
  }
}
