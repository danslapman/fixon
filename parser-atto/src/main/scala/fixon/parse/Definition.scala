package fixon.parse

import atto.*
import atto.Atto.*
import cats.instances.char.*
import cats.syntax.all.*
import fixon.ast.*
import higherkindness.droste.data.Fix

object Definition {
  // Adapted https://github.com/tpolecat/atto/blob/master/modules/docs/src/main/scala/json.scala

  // Bracketed, comma-separated sequence, internal whitespace allowed
  def seq[A](open: Char, p: Parser[A], close: Char): Parser[List[A]] =
    char(open).t ~> sepByT(p, char(',')) <~ char(close)

  // Colon-separated pair, internal whitespace allowed
  lazy val pair: Parser[(String, Json.J)] =
    pairByT(stringLiteral, char(':'), jexpr)

  // Json Expression
  lazy val jexpr: Parser[Json.J] = delay {
    stringLiteral        -| Json.string         |
    seq('{', pair,  '}') -| Json.document       |
    seq('[', jexpr, ']') -| Json.array          |
    bigDecimal           -| Json.number         |
    string("null")       >| Json.`null`         |
    string("true")       >| Json.boolean(true)  |
    string("false")      >| Json.boolean(false)
  }

  // Syntax for turning a parser into one that consumes trailing whitespace
  implicit class TokenOps[A](self: Parser[A]) {
    def t: Parser[A] =
      self <~ takeWhile(c => c.isSpaceChar || c === '\n')
  }

  // Delimited list
  def sepByT[A](a: Parser[A], b: Parser[?]): Parser[List[A]] =
    sepBy(a.t, b.t)

  // Delimited pair, internal whitespace allowed
  def pairByT[A,B](a: Parser[A], delim: Parser[?], b: Parser[B]): Parser[(A,B)] =
    pairBy(a.t, delim.t, b)
}
