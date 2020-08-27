package ilyaletre.evernote.tasks 

import java.time.{Instant, ZoneId}
import java.time.format.{DateTimeFormatter}

sealed trait TimeSpan
case class DaysBefore(before: Int) extends TimeSpan {
  override def toString(): String = s"day-$before"
}
case class At(at: Instant) extends TimeSpan {
  override def toString(): String = TimeSpan.timeFormat.format(at)
}

object TimeSpan {
  val timeFormat = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX").withZone(ZoneId.of("UTC"))
}

// Search grammer
// https://dev.evernote.com/doc/articles/searching_notes.php
sealed trait Predicate
case class Notebook(name: String) extends Predicate
case class Title(name: String) extends Predicate
case class Created(time: TimeSpan) extends Predicate
case class Updated(time: TimeSpan) extends Predicate
case class Tag(tag: String) extends Predicate
case class Not(p: Predicate) extends Predicate

object Predicate {
  def asPair(p: Predicate): (String, String) = {
    p match {
      case Notebook(name) => ("notebook", name)
      case Title(name) => ("intitle", s""""$name"""")
      case Created(t) => ("created", t.toString())
      case Updated(t) => ("updated", t.toString())
      case Tag(tag) => ("tag", tag)
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