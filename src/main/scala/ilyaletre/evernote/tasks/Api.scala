package ilyaletre.evernote.tasks

import scala.util.{Try}

/**
  * Find template from notebook 'template' with specific title
  */
trait GetTemplate[T] {
  def getTemplate(self: T, title: String): Try[Template]
}

/**
  * Create note with the title and content under the notebook with tags
  */
trait CreateNote[T] {
  def createNote(self: T, title: String, content: String, notebook: String, tags: Seq[String]): Try[Unit]
}

object Api {
  def getTemplate[T](implicit instance: GetTemplate[T], self: T, title: String): Try[Template]
    = instance.getTemplate(self, title)

  def createNote[T](implicit instance: CreateNote[T], self: T, title: String, content: String, notebook: String, tags: Seq[String]): Try[Unit]
    = instance.createNote(self, title, content, notebook, tags)
}