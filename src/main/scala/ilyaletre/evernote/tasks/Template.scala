package ilyaletre.evernote.tasks

import com.evernote.edam.`type`.{Note}

trait Template {
}

case class NoteTemplate(note: Note) extends Template {
  override def toString(): String = {
    note.getContent()
  }
}