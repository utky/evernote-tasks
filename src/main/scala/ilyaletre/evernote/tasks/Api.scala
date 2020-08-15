package ilyaletre.evernote.tasks

import scala.util.{Try}
import com.evernote.edam.notestore.NoteMetadata
import com.evernote.edam.`type`.Note

/**
  * Find template from notebook 'template' with specific title
  */
trait GetTemplate[F[_]] {
  def getTemplate(title: String): F[Template]
}

object GetTemplate {
  def apply[F[_]](implicit instance: GetTemplate[F]): GetTemplate[F] = instance
}

/**
  * Create note with the title and content under the notebook with tags
  */
trait CreateNote[F[_]] {
  def createNote(req: CreateNote.CreateReq): F[Unit]
}

object CreateNote {
  case class CreateReq(title: String, content: String, notebook: String, tags: Seq[String])

  def apply[F[_]](implicit instance: CreateNote[F]): CreateNote[F] = instance
}

/**
  * Fetch NoteMetadata list
  */
trait FindNotes[F[_]] {
  def findNotes(predicates: Seq[Predicate]): F[List[NoteMetadata]]
}

object FindNotes {
  def apply[F[_]](implicit instance: FindNotes[F]): FindNotes[F] = instance
}

trait GetNote[F[_]] {
  def getNoteByTitle(notebook: String, title: String): F[Note]
}

object GetNote {
  def apply[F[_]](implicit instance: GetNote[F]): GetNote[F] = instance
}