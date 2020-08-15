package ilyaletre.evernote.tasks 

import collection.JavaConverters._
import java.time.ZonedDateTime
import com.evernote.edam.`type`.{Notebook}
import ilyaletre.evernote.tasks.Evernote._
import ilyaletre.evernote.tasks.CreateNote._
import java.time.ZoneId
import cats.implicits._

object Main extends App {
  val token = System.getenv("EVERNOTE_AUTH_TOKEN")
  args(0) match {
    case "weekly-review" => {
      val today = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"))
      val action = WeeklyReview(today, args(1))
      Evernote(action)(token)
      ()
    }
  }
}
