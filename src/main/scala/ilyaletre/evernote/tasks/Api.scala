package ilyaletre.evernote.tasks

import scala.util.{Try}

/**
  * Find template from notebook 'template' with specific title
  */
trait GetTemplate[T] {
  def getTemplate(self: T, title: String): Try[Template]
}

object GetTemplate {
  def apply[T](implicit instance: GetTemplate[T]): GetTemplate[T] = instance

  implicit class GetTemplateOps[T: GetTemplate](a: T) {
    def getTemplate(title: String): Try[Template] = GetTemplate[T].getTemplate(a, title)
  }

  // def getTemplate[T](implicit instance: GetTemplate[T], self: T, title: String): Try[Template]
  //   = instance.getTemplate(self, title)
}


/**
  * Create note with the title and content under the notebook with tags
  */
trait CreateNote[T] {
  def createNote(self: T, req: CreateNote.CreateReq): Try[Unit]
}

object CreateNote {
  case class CreateReq(title: String, content: String, notebook: String, tags: Seq[String])

  def apply[T](implicit instance: CreateNote[T]): CreateNote[T] = instance

  implicit class CreateNoteOps[T: CreateNote](a: T) {
    def createNote(req: CreateReq): Try[Unit]
      = CreateNote[T].createNote(a, req)
  }

  // def createNote[T](implicit instance: CreateNote[T], self: T, title: String, content: String, notebook: String, tags: Seq[String]): Try[Unit]
  //   = instance.createNote(self, title, content, notebook, tags)
}