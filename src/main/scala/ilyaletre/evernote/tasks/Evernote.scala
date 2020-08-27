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
import com.typesafe.scalalogging.Logger

object Evernote {

  lazy val logger = Logger("Evernote")

  type Evernote[A] = ReaderT[Try, NoteStoreClient, A]

  def apply[A](f: Evernote[A])(token: String): Try[A] = {
    val evernoteAuth = new EvernoteAuth(EvernoteService.PRODUCTION, token)
    val factory = new ClientFactory(evernoteAuth)
    val client = factory.createNoteStoreClient()
    logger.info(s"evernote NoteStoreClient ${client}")
    f.run(client)
  }

  def findOneNote(
    client: NoteStoreClient,
    filter: NoteFilter,
    resultSpec: NotesMetadataResultSpec): Note = {
    logger.debug(s"start findOneNote findNotesMetadata")
    val metadata = client.findNotesMetadata(filter, 0, 1, resultSpec)
    logger.debug(s"end findOneNote findNotesMetadata")
    logger.info(s"NoteMetadataList noteSize: ${metadata.getNotesSize()}")
    logger.info(s"NoteMetadataList searchWords: ${metadata.getSearchedWords()}")
    logger.info(s"NoteMetadataList totalNotes: ${metadata.getTotalNotes()}")
    if (metadata.getNotesSize() < 1) {
      val message = s"Could not find any note by words: ${filter.getWords()}"
      throw new IllegalArgumentException(message)
    }
    else {
      val guid = metadata.getNotes().get(0).getGuid()
      logger.info(s"getting a note by guid: ${guid}")
      logger.debug(s"start findOneNote getNote: ${guid}")
      val note = client.getNote(guid, true, false, false, false)
      logger.debug(s"end findOneNote getNote: ${guid}")
      note
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
      logger.info(s"getting template with title: ${title}")
      val filter = new NoteFilter()
      filter.setWords(Query.fromPredicates(Seq(Notebook("template"), Title(title))))
      val spec = new NotesMetadataResultSpec()
      Try {
        val note = findOneNote(client, filter, spec)
        logger.info(s"Got template title: ${note.getTitle()}")
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
        logger.info(s"Notebook guid to create new note: ${nbguid}")
        logger.info(s"New note: ${req}")
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
      * Search multiple notes by specified critria.
      *
      * @param predicates
      * @return
      */
    def findNotes(predicates: Seq[Predicate]): Evernote[List[NoteMetadata]] = ReaderT { client => 
      Try {
        val words = Query.fromPredicates(predicates)
        logger.info(s"fiding notes by words: ${words}")
        val filter = new NoteFilter()
        filter.setWords(words)
        val spec = new NotesMetadataResultSpec()
      logger.debug(s"start findNotes findNotesMetadata:")
        val metadata = client.findNotesMetadata(filter, 0, 255, spec)
      logger.debug(s"end findNotes findNotesMetadata:")
        logger.info(s"found metadata ${metadata.getNotesSize()}")
        metadata.getNotes().asScala.toList
      }
    }
  }
  implicit val getNote = new GetNote[Evernote] {
    /**
      * Fetch single note by title and belonging notebook name.
      *
      * @param notebook
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
        logger.info(s"stating to getNoteByTitle notebook: ${notebook}, title: ${title}")
        findOneNote(client, filter, spec)
      }
    }
    def getNoteById(id: String): Evernote[Note] = ReaderT { client =>
      Try {
        logger.debug(s"start getNoteByTitle getNote: ${id}")
        val note = client.getNote(id, true, false, false, false)
        logger.debug(s"end getNoteByTitle getNote: ${id}")
        note
      }
    }
  }
}