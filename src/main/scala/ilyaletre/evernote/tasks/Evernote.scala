package ilyaletre.evernote.tasks 

import scala.util.{Try}
import com.evernote.edam.`type`.{Note}
import com.evernote.clients.{NoteStoreClient}

case class Evernote(noteStoreClient: NoteStoreClient)

object Evernote {
  implicit val getTemplate = new GetTemplate[Evernote] {
    /**
      * 1. [[findNotesMetadata https://dev.evernote.com/doc/reference/javadoc/com/evernote/edam/notestore/NoteStore.Client.html#findNotesMetadata(java.lang.String,%20com.evernote.edam.notestore.NoteFilter,%20int,%20int,%20com.evernote.edam.notestore.NotesMetadataResultSpec)]] w/ filter notebook, intitle
      * 2. [[NoteList https://dev.evernote.com/doc/reference/javadoc/com/evernote/edam/notestore/NoteList.html]] length should be 1
      * 3. [[getNote https://dev.evernote.com/doc/reference/javadoc/com/evernote/edam/notestore/NoteStore.Client.html#getNote(java.lang.String,%20java.lang.String,%20boolean,%20boolean,%20boolean,%20boolean)]] by guid of first of 2. 
      * @param self
      * @param title
      * @return
      */
    def getTemplate(self: Evernote, title: String): Try[Template] = {
      ???
    }
  }
  implicit val createNote = new CreateNote[Evernote] {
    /**
      * 
      *
      * 1. [[listNotebooks https://dev.evernote.com/doc/reference/javadoc/com/evernote/edam/notestore/NoteStore.Client.html#listNotebooks(java.lang.String)]] and get matched one
      * 2. instantiate Note
      * 3. Set attributes
      *     A) title
      *     B) content XHTML
      *     C) active
      *     D) notebookGuid
      *     C) tagNames
      * 4. [[createNote https://dev.evernote.com/doc/reference/javadoc/com/evernote/edam/notestore/NoteStore.Client.html#createNote(java.lang.String,%20com.evernote.edam.type.Note)]]
      * 
      * @param self
      * @param title
      * @param content
      * @param notebook
      * @param tags
      * @return
      */
    def createNote(self: Evernote, title: String, content: String, notebook: String, tags: Seq[String]): Try[Unit] = {
      ???
    }
  }
}