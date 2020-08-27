package ilyaletre.evernote.tasks

import scala.xml._

case class Review(todo: Seq[String], summary: Seq[Summary], plan: Seq[String])
case class Summary(date: String, topics: Seq[String])

sealed trait DecodeError
case object DecodeFailed extends DecodeError

sealed trait State 
case object Init extends State
case object Header extends State
case class Items(items: Seq[String]) extends State
case class Done(items: Seq[String]) extends State

object HeaderedListParser {

  val itemRegexp = "^\\s*[\\*\\+\\-]\\s+(.+)$".r

  def parseHeaderedList(header: String, content: String): Seq[String] = {
    val divs = XML.loadString(content).child
    divs.foldLeft[State](Init)(parseNode(header)) match {
      case Items(items) => items
      case Done(items) => items
      case _ => Seq()
    }
  }

  def parseNode(header: String)(parser: State, node: Node): State = {
    println(s"${node.label}: ${node.text}")
    parser match {
      case Init if node.text.strip() == header => Header
      case Header if HeaderedListParser.itemRegexp.matches(node.text) => Items(Seq(HeaderedListParser.itemRegexp.findFirstMatchIn(node.text).get.group(1)))
      case Items(items) if HeaderedListParser.itemRegexp.matches(node.text) => Items(items :+ HeaderedListParser.itemRegexp.findFirstMatchIn(node.text).get.group(1))
      case Items(items) if node.text.strip().startsWith("##") => Done(items)
      case other => other
    }
  }
}

object Review {
  def decodeReview(content: String): Either[DecodeError, Review] = {
    val plan = HeaderedListParser.parseHeaderedList("## Plan", content)
    Right(Review(Seq(), Seq(), plan))
  }
  def decodeSummary(content: String): Either[DecodeError, Seq[String]] =  Right(HeaderedListParser.parseHeaderedList("## summary", content))
  def decodePlan(content: String): Either[DecodeError, Seq[String]] = Right(HeaderedListParser.parseHeaderedList("## plan", content))

  def encodeReview(review: Review): String = ???
}