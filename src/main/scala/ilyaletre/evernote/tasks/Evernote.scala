package ilyaletre.evernote.tasks 

import scala.util.{Try}
import collection.JavaConverters._
import com.evernote.edam.`type`.{Note}
import com.evernote.clients.{NoteStoreClient}
import com.evernote.edam.notestore.{NoteFilter, NotesMetadataResultSpec}
import ilyaletre.evernote.tasks.CreateNote.CreateReq
import com.evernote.auth.{EvernoteAuth, EvernoteService}
import com.evernote.clients.{ClientFactory}
import cats.data.ReaderT
import cats.data.Kleisli._
import com.evernote.edam.notestore.NoteMetadata

//case class Evernote(noteStoreClient: NoteStoreClient)
object Evernote {

  type Evernote[A] = ReaderT[Try, NoteStoreClient, A]

  def apply[A](f: Evernote[A])(token: String): Try[A] = {
    val evernoteAuth = new EvernoteAuth(EvernoteService.SANDBOX, token)
    val factory = new ClientFactory(evernoteAuth)
    val client = factory.createNoteStoreClient()
    f.run(client)
  }

  def findOneNote(
    client: NoteStoreClient,
    filter: NoteFilter,
    resultSpec: NotesMetadataResultSpec): Note = {
    val metadata = client.findNotesMetadata(filter, 0, 1, resultSpec)
    if (metadata.getNotesSize() < 1) {
      val message = s"Could not find any note by words: ${filter.getWords()}"
      throw new IllegalArgumentException(message)
    }
    else {
      val guid = metadata.getNotes().get(0).getGuid()
      client.getNote(guid, true, false, false, false)
    }
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
    def getTemplate(title: String): Evernote[Template] = ReaderT { client =>
      val filter = new NoteFilter()
      filter.setWords(Query.fromPredicates(Seq(Notebook("template"), Title(title))))
      val spec = new NotesMetadataResultSpec()
      Try {
        val note = findOneNote(client, filter, spec)
        NoteTemplate(note)
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
    def createNote(req: CreateReq): Evernote[Unit] = ReaderT { client =>
      Try {
        val books = client.listNotebooks().asScala.filter { notebook => notebook.getName == req.notebook }
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
        client.createNote(note)
      }
    }
  }
  implicit val findNotes = new FindNotes[Evernote] {
    /**
      * 
      *
      * @param predicates
      * @return
      */
    def findNotes(predicates: Seq[Predicate]): Evernote[List[NoteMetadata]] = ReaderT { client => 
      Try {
        val words = Query.fromPredicates(predicates)
        val filter = new NoteFilter()
        filter.setWords(words)
        val spec = new NotesMetadataResultSpec()
        val metadata = client.findNotesMetadata(filter, 0, 255, spec)
        metadata.getNotes().asScala.toList
      }
    }
  }
  implicit val getNote = new GetNote[Evernote] {
    /**
      * 
      *
      * @param title
      * @return
      */
    def getNoteByTitle(notebook: String, title: String): Evernote[Note] = ReaderT { client =>
      val filter = new NoteFilter()
      val words = Query.fromPredicates(Seq(
        Notebook(notebook),
        Title(title)
      ))
      filter.setWords(words)
      val spec = new NotesMetadataResultSpec()
      Try {
        findOneNote(client, filter, spec)
      }
    }
  }
}