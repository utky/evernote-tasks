package ilyaletre.evernote.tasks 

import scala.util.{Try}
import com.evernote.edam.`type`.{Note}
import com.evernote.clients.{NoteStoreClient}

case class Evernote(noteStoreClient: NoteStoreClient)

object Evernote {
  implicit val getTemplate = new GetTemplate[Evernote] {
    def getTemplate(self: Evernote, title: String): Try[Template] = {
      ???
    }
  }
}