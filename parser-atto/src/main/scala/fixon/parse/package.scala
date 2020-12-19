package fixon

import atto.Parser
import fixon.ast.Json

package object parse {
  val parser: Parser[Json.J] = Definition.jexpr
}
