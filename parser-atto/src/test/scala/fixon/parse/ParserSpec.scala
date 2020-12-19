package fixon.parse

import atto.Atto._
import atto.ParseResult.Done
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class ParserSpec extends AnyFunSuite with Matchers {
  test("Parser should parse json object") {
    val data = """{"data": {"value": -123.456}}"""

    val sut = parser parseOnly data

    info(sut.toString)
    sut shouldBe a [Done[_]]
  }
}
