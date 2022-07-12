package fixon

import fixon.ast.*
import higherkindness.droste.*
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
}
