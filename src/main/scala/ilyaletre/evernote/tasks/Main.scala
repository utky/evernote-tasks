package ilyaletre.evernote.tasks 

import collection.JavaConverters._
import java.time.ZonedDateTime
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
  val token = System.getenv("EVERNOTE_AUTH_TOKEN")
  logger.info(s"args: ${args}")
  args(0) match {
    case "weekly-review" => {
      val today = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"))
      logger.info(s"today: ${today}")
      val action = WeeklyReview(today)
      logger.info(s"start to interact with evernote API")
      Evernote(action)(token) match {
        case Failure(exception) => logger.error("weekly-review failed", exception)
        case Success(_) => logger.info("weekly-review succeeded")
      }
    }
  }
}
