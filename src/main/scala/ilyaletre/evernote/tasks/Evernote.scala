package ilyaletre.evernote.tasks 

import scala.util.{Try}
import collection.JavaConverters._
import com.evernote.edam.`type`.{Note}
import com.evernote.clients.{NoteStoreClient}
import com.evernote.edam.notestore.{NoteFilter, NotesMetadataResultSpec}
import ilyaletre.evernote.tasks.CreateNote.CreateReq
import com.evernote.auth.{EvernoteAuth, EvernoteService}
import com.evernote.clients.{ClientFactory}

case class Evernote(noteStoreClient: NoteStoreClient)

object Evernote {
  def apply(token: String): Evernote = {
    val evernoteAuth = new EvernoteAuth(EvernoteService.SANDBOX, token)
    val factory = new ClientFactory(evernoteAuth)
    val noteStore = factory.createNoteStoreClient()
    Evernote(noteStore)
  }
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
      val filter = new NoteFilter()
      filter.setWords(Query.fromPredicates(Seq(Notebook("template"), Title(title))))
      val spec = new NotesMetadataResultSpec()
      Try {
        val metadata = self.noteStoreClient.findNotesMetadata(filter, 0, 1, spec)
        if (metadata.getNotesSize() < 1) {
          val message = s"Could not find any note having notebook: template, title: $title"
          throw new IllegalArgumentException(message)
        }
        else {
          val guid = metadata.getNotes().get(0).getGuid()
          val note = self.noteStoreClient.getNote(guid, true, false, false, false)
          NoteTemplate(note)
        }
      }
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
      * @param req
      * @return
      */
    def createNote(self: Evernote, req: CreateReq): Try[Unit] = {
      Try {
        val books = self.noteStoreClient.listNotebooks().asScala.filter { notebook => notebook.getName == req.notebook }
        if (books.length < 1) {
          val message = s"Could not find any notebook: ${req.notebook}"
          throw new IllegalArgumentException(message)
        }
        val nbguid = books.head.getGuid()
        val note = new Note()
        note.setTitle(req.title)
        note.setContent(req.content)
        note.setActive(true)
        note.setNotebookGuid(nbguid)
        note.setTagNames(req.tags.toList.asJava)
        self.noteStoreClient.createNote(note)
      }
    }
  }
}