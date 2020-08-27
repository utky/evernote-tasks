package ilyaletre.evernote.tasks

import scala.util.Try
import java.time.{ZonedDateTime, Instant}
import java.time.temporal.ChronoUnit
import ilyaletre.evernote.tasks.GetTemplate._
import cats._
import cats.implicits._
import com.typesafe.scalalogging.Logger

object WeeklyReview {
  val logger = Logger("WeeklyReview")
  def dateFormat = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
  def daysOfWeek = 7
  def targetNotebook = "log"
  def apply[F[_]
    : GetTemplate
    : CreateNote
    : FindNotes
    : GetNote
    : Monad]
    (date: ZonedDateTime): F[Unit] = {
    val oneWeekAgo = date.minus(daysOfWeek, ChronoUnit.DAYS)
    val title = dateFormat.format(date)
    val predicates = Seq(
      Notebook(targetNotebook),
      Updated(DaysBefore(daysOfWeek))
    )
    logger.info(s"oneWeekAgo: ${oneWeekAgo}")
    logger.info(s"title: ${title}")
    for {
      lastReview <- GetNote[F].getNoteByTitle(targetNotebook, dateFormat.format(oneWeekAgo))
      plan = Review.decodePlan(lastReview.getContent()).getOrElse(Seq()).map { p => Map("summary" -> p)}

      _ = logger.info(s"read plan: ${plan}")

      notesMetaThisWeek <- FindNotes[F].findNotes(predicates)
      notesThisWeek <- notesMetaThisWeek.traverse {notemeta => GetNote[F].getNoteById(notemeta.getGuid())}
      logs = notesThisWeek.sortBy { _.getTitle() }.map { note =>
        Map(
          "title" -> note.getTitle(),
          "topics" -> Review.decodeSummary(note.getContent()).getOrElse(Seq()).map { summary => Map("summary" -> summary) }
          )
      }

      _ = logger.info(s"read logs: ${logs}")

      context = Map(
        "todo" -> plan,
        "logs" -> logs,
      )

      // FIXME インデントが全然うまくいってない
      template = FileTemplate("templates/weekly-review.xml.mustache")

      content = template.render(context)

      _ = logger.info(s"generated new review: ${content}")
      result <- CreateNote[F].createNote(CreateNote.CreateReq(
        title,
        content,
        targetNotebook,
        Seq("review")
      ))
    }
    yield Try { () }
  }
}