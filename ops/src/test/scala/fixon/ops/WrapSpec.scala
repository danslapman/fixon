package fixon.ops

import fixon.ast.*
import higherkindness.droste.*
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class WrapSpec extends AnyFunSuite with Matchers {
  test("replace object with array of key-values") {
    val data = Json.document(
      "f1" -> Json.string("v1")
    )

    val sut = scheme.cata(wrap("key", "value").algebra).apply(data)

    sut shouldBe Json.array(
      Json.document("key" -> Json.string("f1"), "value" -> Json.string("v1"))
    )
  }
}
