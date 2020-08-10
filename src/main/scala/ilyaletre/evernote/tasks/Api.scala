package ilyaletre.evernote.tasks

import scala.util.{Try}

trait GetTemplate[T] {
  def getTemplate(self: T, title: String): Try[Template]
}

trait CreateNote[T] {
  def createNote(self: T, content: String): Try[Unit]
}

object Api {
  def getTemplate[T](implicit instance: GetTemplate[T], self: T, title: String): Try[Template]
    = instance.getTemplate(self, title)

  def createNote[T](implicit instance: CreateNote[T], self: T, content: String): Try[Unit]
    = instance.createNote(self, content)
}