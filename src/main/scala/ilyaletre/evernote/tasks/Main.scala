package ilyaletre.evernote.tasks 

import collection.JavaConverters._
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import com.evernote.edam.`type`.{Notebook}
import ilyaletre.evernote.tasks.Evernote._
import ilyaletre.evernote.tasks.CreateNote._
import java.time.ZoneId
import cats.implicits._
import com.typesafe.scalalogging.Logger
import scala.util.Failure
import scala.util.Success

object Main extends App {
  val logger = Logger("Main")
  val timeZone = ZoneId.of("Asia/Tokyo")
  val token = System.getenv("EVERNOTE_AUTH_TOKEN")
  logger.info(s"args: ${args}")
  args(0) match {
    case "weekly-review" => {

      val today = if (args.length > 1) {
        val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val dt = java.time.LocalDate.parse(args(1), fmt)
        ZonedDateTime.of(dt, java.time.LocalTime.of(0, 0, 0), timeZone)
      }
      else {
        ZonedDateTime.now(timeZone)
      }

      logger.info(s"target date: ${today}")

      val action = WeeklyReview(today)
      logger.info(s"start to interact with evernote API")
      Evernote(action)(token) match {
        case Failure(exception) => logger.error("weekly-review failed", exception)
        case Success(_) => logger.info("weekly-review succeeded")
      }
    }
  }
}
