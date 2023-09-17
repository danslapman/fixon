package fixon.parse

import fixon.ops.*
import higherkindness.droste.*
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.util.Success

class JsonParserSpec extends AnyFunSuite with Matchers {
  test("Parser should parse json object") {
    val data = """{"data": {"value": -123.456}}"""

    val sut = new JsonParser(data).Json.run()

    sut.map(scheme.cata(renderToString)) shouldBe Success("""{"data":{"value":-123.456}}""")
  }
}
