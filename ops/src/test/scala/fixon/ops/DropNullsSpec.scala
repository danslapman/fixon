package fixon.ops

import fixon.ast.*
import higherkindness.droste.*
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class DropNullsSpec extends AnyFunSuite with Matchers  {
  test("dropNull should drop nulls") {
    val data = Json.document(
      "f1" -> Json.string("v1"),
      "f2" -> Json.document(
        "if1" -> Json.`null`
      )
    )

    val sut = scheme.cata(dropNulls[Json.J].algebra).apply(data)

    sut shouldBe Json.document(
      "f1" -> Json.string("v1"),
      "f2" -> Json.document(Map.empty)
    )
  }
}
