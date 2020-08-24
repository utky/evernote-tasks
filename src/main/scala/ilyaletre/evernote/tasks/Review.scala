package ilyaletre.evernote.tasks

import scala.xml._

case class Review(todo: Seq[String], summary: Seq[DaySummary], plan: Seq[String])
case class DaySummary(date: String, topics: Seq[String])

sealed trait DecodeError
case object DecodeFailed extends DecodeError

object ToDo {
  sealed trait Parser
  case object Init extends Parser
  case object Header extends Parser
  case class Todo(items: Seq[String]) extends Parser
  case class Done(items: Seq[String]) extends Parser
  val itemRegexp = "^\\s*[\\*\\+\\-]\\s+(.+)$".r
  def decode(parser: Parser, node: Node): Parser = {
    println(s"${node.label}: ${node.text}")
    parser match {
      case Init if node.text.strip() == "## ToDo" => Header
      case Header if itemRegexp.matches(node.text) => Todo(Seq(itemRegexp.findFirstMatchIn(node.text).get.group(1)))
      case Todo(items) if itemRegexp.matches(node.text) => Todo(items :+ itemRegexp.findFirstMatchIn(node.text).get.group(1))
      case Todo(items) if node.text.strip().startsWith("##") => Done(items)
      case other => other
    }
  }
  def result(parse: Parser): Seq[String] = {
    println(parse)
    parse match {
      case Todo(items) => items
      case Done(items) => items
      case _ => Seq()
    }
  }
}

object Review {
  def decodeReview(content: String): Either[DecodeError, Review] = {
    val divs = XML.loadString(content).child
    val todo = ToDo.result(divs.foldLeft[ToDo.Parser](ToDo.Init)(ToDo.decode))
    Right(Review(todo, Seq(), Seq()))
  }
  def decodeTodo(current: Elem): Either[DecodeError, Seq[String]] = ???
  def decodeSummary(content: String): Either[DecodeError, Seq[DaySummary]] = ???
  def decodeDaySummary(content: String): Either[DecodeError, DaySummary] = ???
  def decodeDate(content: String): Either[DecodeError, String] = ???
  def decodeTopics(content: String): Either[DecodeError, Seq[String]] = ???
  def decodePlan(content: String): Either[DecodeError, Seq[String]] = ???
  def encodeReview(review: Review): String = ???
}