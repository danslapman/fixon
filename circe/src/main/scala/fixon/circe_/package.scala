package fixon

import fixon.ast.*
import io.circe.Json as CirceJson
import higherkindness.droste.*

package object circe_ {
  val fixon2Circe: Algebra[Json, CirceJson] = Algebra[Json, CirceJson] {
    case JNull() => CirceJson.Null
    case JBoolean(b) => CirceJson.fromBoolean(b)
    case JNumber(n) => CirceJson.fromBigDecimal(n)
    case JString(s) => CirceJson.fromString(s)
    case JArray(js) => CirceJson.fromValues(js)
    case JObject(vs) => CirceJson.fromFields(vs)
  }

  val circe2Fixon: Coalgebra[Json, CirceJson] =
    Coalgebra[Json, CirceJson] { _.fold[Json[CirceJson]](
      JNull(),
      b => JBoolean(b),
      n => JNumber(n.toBigDecimal.get),
      s => JString(s),
      arr => JArray(arr),
      obj => JObject(obj.toMap)
    )}
}
