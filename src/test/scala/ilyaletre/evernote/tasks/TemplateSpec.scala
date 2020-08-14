package ilyaletre.evernote.tasks

import org.scalatest.funspec.AnyFunSpec
import com.evernote.edam.`type`.{Note}

class TemplateSpec extends AnyFunSpec {
  describe("NoteTemplate") {
    it("render plain text") {
      val note = new Note()
      note.setContent("{{ name }}")
      val template = NoteTemplate(note)
      assert(template.render(Map("name" -> "TemplateSpec")) == "TemplateSpec")
    }
    it("render list") {
      val note = new Note()
      note.setContent("""{{#list}}
      * {{name}}
      {{/list}}""")
      val template = NoteTemplate(note)
      assert(template.render(
        Map("list" -> List(
          Map("name" -> "number1"),
          Map("name" -> "number2"),
          Map("name" -> "number3"),
        ))
      ) == """* number1
      * number2
      * number3
      """)
    }
  }
}