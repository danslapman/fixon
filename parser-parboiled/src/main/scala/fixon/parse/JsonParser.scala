package fixon.parse

import fixon.ast.Json as Fixon
import org.parboiled2._
import scala.annotation.switch

/** This is a feature-complete JSON parser implementation that almost directly
 * models the JSON grammar presented at http://www.json.org as a parboiled2 PEG parser.
 */
class JsonParser(val input: ParserInput) extends Parser with StringBuilding {
  import CharPredicate.{Digit, Digit19, HexDigit}
  import JsonParser._

  // the root rule
  def Json: Rule1[Fixon.J] = rule(WhiteSpace ~ Value ~ EOI)

  def JsonObject: Rule1[Fixon.J] =
    rule {
      ws('{') ~ zeroOrMore(Pair).separatedBy(ws(',')) ~ ws('}') ~> ((fields: Seq[(String, Fixon.J)]) => Fixon.document(fields))
    }

  def Pair: Rule1[(String, Fixon.J)] = rule(JsonStringUnwrapped ~ ws(':') ~ Value ~> ((s: String, v: Fixon.J) => (s, v)))

  def Value: Rule1[Fixon.J] =
    rule {
      // as an optimization of the equivalent rule:
      //   JsonString | JsonNumber | JsonObject | JsonArray | JsonTrue | JsonFalse | JsonNull
      // we make use of the fact that one-char lookahead is enough to discriminate the cases
      run {
        (cursorChar: @switch) match {
          case '"'                                                             => JsonString
          case '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' | '-' => JsonNumber
          case '{'                                                             => JsonObject
          case '['                                                             => JsonArray
          case 't'                                                             => JsonTrue
          case 'f'                                                             => JsonFalse
          case 'n'                                                             => JsonNull
          case _                                                               => MISMATCH
        }
      }
    }

  def JsonString: Rule1[Fixon.J] = rule(JsonStringUnwrapped ~> ((s: String) => Fixon.string(s)))

  def JsonStringUnwrapped: Rule1[String] = rule('"' ~ clearSB() ~ Characters ~ ws('"') ~ push(sb.toString))

  def JsonNumber: Rule1[Fixon.J] = rule(capture(Integer ~ optional(Frac) ~ optional(Exp)) ~> ((s: String) => Fixon.number(BigDecimal(s))) ~ WhiteSpace)

  def JsonArray: Rule1[Fixon.J] = rule(ws('[') ~ zeroOrMore(Value).separatedBy(ws(',')) ~ ws(']') ~> ((jseq: Seq[Fixon.J]) => Fixon.array(jseq)))

  def Characters: Rule0 = rule(zeroOrMore(NormalChar | '\\' ~ EscapedChar))

  def NormalChar: Rule0 = rule(!QuoteBackslash ~ ANY ~ appendSB())

  def EscapedChar: Rule0 =
    rule(
      QuoteSlashBackSlash ~ appendSB()
        | 'b' ~ appendSB('\b')
        | 'f' ~ appendSB('\f')
        | 'n' ~ appendSB('\n')
        | 'r' ~ appendSB('\r')
        | 't' ~ appendSB('\t')
        | Unicode ~> { (code: Int) => sb.append(code.asInstanceOf[Char]); () }
    )

  def Unicode: Rule1[Int] = rule('u' ~ capture(HexDigit ~ HexDigit ~ HexDigit ~ HexDigit) ~> (java.lang.Integer.parseInt(_: String, 16)))

  def Integer: Rule0 = rule(optional('-') ~ (Digit19 ~ Digits | Digit))

  def Digits: Rule0 = rule(oneOrMore(Digit))

  def Frac: Rule0 = rule("." ~ Digits)

  def Exp: Rule0 = rule(ignoreCase('e') ~ optional(anyOf("+-")) ~ Digits)

  def JsonTrue: Rule1[Fixon.J] = rule(capture("true") ~ WhiteSpace ~> ((_: String) => Fixon.boolean(true)))

  def JsonFalse: Rule1[Fixon.J] = rule(capture("false") ~ WhiteSpace ~> ((_: String) => Fixon.boolean(false)))

  def JsonNull: Rule1[Fixon.J] = rule(capture("null") ~ WhiteSpace ~> ((_: String) => Fixon.`null`))

  def WhiteSpace: Rule0 = rule(zeroOrMore(WhiteSpaceChar))

  def ws(c: Char): Rule0 = rule(c ~ WhiteSpace)
}

object JsonParser {
  val WhiteSpaceChar      = CharPredicate(" \n\r\t\f")
  val QuoteBackslash      = CharPredicate("\"\\")
  val QuoteSlashBackSlash = QuoteBackslash ++ "/"
}
