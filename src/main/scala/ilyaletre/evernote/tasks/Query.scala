package ilyaletre.evernote.tasks 

// Search grammer
// https://dev.evernote.com/doc/articles/searching_notes.php
sealed trait Predicate
case class Notebook(name: String) extends Predicate
case class Title(name: String) extends Predicate
//case class Created(name: String) extends Predicate
case class Not(p: Predicate) extends Predicate

object Predicate {
  def asPair(p: Predicate): (String, String) = {
    p match {
      case Notebook(name) => ("notebook", name)
      case Title(name) => ("intitle", s""""$name"""")
      case Not(Not(p)) => Predicate.asPair(p)
      case Not(p) => {
        val (k, v) = Predicate.asPair(p)
        ("-" + k, v)
      }
    }
  }
}

object Query {
  def fromPredicates(predicates: Seq[Predicate]): String = {
    predicates.map { p =>
      val (k, v) = Predicate.asPair(p)
      s"$k:$v"
    }.mkString(" ")
  }
}