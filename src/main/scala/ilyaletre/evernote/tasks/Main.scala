package ilyaletre.evernote.tasks 

import collection.JavaConverters._

import com.evernote.edam.`type`.{Notebook}
import ilyaletre.evernote.tasks.Evernote._
import ilyaletre.evernote.tasks.GetTemplate._
import ilyaletre.evernote.tasks.CreateNote._

object Main extends App {
  val token = System.getenv("EVERNOTE_AUTH_TOKEN")
  args(0) match {
    case "weekly-review" => {
      val evernote = Evernote(token)
      evernote.getTemplate(args(0))
    }
  }


}
