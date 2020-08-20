package ilyaletre.evernote.tasks

import com.evernote.edam.`type`.{Note}

import org.fusesource.scalate._

trait Template {
  def render(parameters: Map[String, Any]): String
}

case class NoteTemplate(note: Note) extends Template {
  def render(parameters: Map[String, Any]): String = {
    val engine = new TemplateEngine()
    val templateSource = TemplateSource.fromText("note.mustache", note.getContent())
    engine.layout(templateSource, parameters)
  }
  override def toString(): String = {
    note.getContent()
  }
}

case class FileTemplate(path: String) extends Template {
  def render(parameters: Map[String, Any]): String = {
    val engine = new TemplateEngine()
    val templateSource = TemplateSource.fromFile(path)
    engine.layout(templateSource, parameters)
  }
}