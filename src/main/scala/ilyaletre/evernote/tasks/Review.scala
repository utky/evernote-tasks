package ilyaletre.evernote.tasks

import scala.xml._

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
    val f = javax.xml.parsers.SAXParserFactory.newInstance()
    f.setValidating(false);
    f.setFeature("http://xml.org/sax/features/namespaces", false);
    f.setFeature("http://xml.org/sax/features/validation", false);
    f.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
    f.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

    val p = f.newSAXParser()
    val divs = XML.withSAXParser(p).loadString(content).child
    divs.foldLeft[State](Init)(parseNode(header)) match {
      case Items(items) => items
      case Done(items) => items
      case _ => Seq()
    }
  }

  def parseNode(header: String)(parser: State, node: Node): State = {
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
  def decodeSummary(content: String): Either[DecodeError, Seq[String]] =  Right(HeaderedListParser.parseHeaderedList("## summary", content))
  def decodePlan(content: String): Either[DecodeError, Seq[String]] = Right(HeaderedListParser.parseHeaderedList("## plan", content))
}