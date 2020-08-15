package ilyaletre.evernote.tasks

import scala.util.Try
import java.time.{ZonedDateTime, Instant}
import java.time.temporal.ChronoUnit
import ilyaletre.evernote.tasks.GetTemplate._
import cats._
import cats.implicits._

object WeeklyReview {
  def dateFormat = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
  def daysOfWeek = 7
  def targetNotebook = "log"
  def apply[F[_]
    : GetTemplate
    : CreateNote
    : FindNotes
    : GetNote
    : Monad]
    (date: ZonedDateTime, templateTitle: String): F[Try[Unit]] = {
    val oneWeekAgo = date.minus(daysOfWeek, ChronoUnit.DAYS)
    val title = dateFormat.format(date)
    val context = Map(
      "logs" -> List[String](),
      "todo" -> List[String]()
    )
    val predicates = Seq(
      Notebook(targetNotebook),
      Created(DaysBefore(daysOfWeek))
    )
    for {
      postsThisWeek <- FindNotes[F].findNotes(predicates)
      previousReview <- GetNote[F].getNoteByTitle(targetNotebook, dateFormat.format(oneWeekAgo))
      template <- GetTemplate[F].getTemplate(templateTitle)
      content = template.render(context)
      _ <- CreateNote[F].createNote(CreateNote.CreateReq(
        title,
        content,
        targetNotebook,
        Seq("review")
      ))
    }
    yield Try { () }
  }
}