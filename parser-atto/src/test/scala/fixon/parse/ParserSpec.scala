package fixon.parse

import atto.Atto.*
import atto.ParseResult.Done
import fixon.ops.*
import higherkindness.droste.*
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class ParserSpec extends AnyFunSuite with Matchers {
  test("Parser should parse json object") {
    val data = """{"data": {"value": -123.456}}"""

    val sut = parser parseOnly data

    sut.map(scheme.cata(renderToString)) shouldBe Done("", """{"data":{"value":-123.456}}""")
  }
}
