package ilyaletre.evernote.tasks

import org.scalatest.funspec.AnyFunSpec
import com.evernote.edam.`type`.{Note}

class ReviewSpec extends AnyFunSpec {
  describe("Review") {
    it("should decode content") {
      val decoded = HeaderedListParser.parseHeaderedList("## ToDo", """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE en-note SYSTEM "http://xml.evernote.com/pub/enml2.dtd">
<en-note>
<div>## ToDo</div>
<div><br/></div>
<div>* todo item 1</div>
<div>* todo item 2</div>
<div><br clear="none"/></div>
<div>## Summary</div>
<div><br/></div>
<div>* 2020-08-19</div>
<div>    * 0819 summary</div>
<div>* 2020-08-20</div>
<div>    + 0820 summary</div>
<div><br clear="none"/></div>
<div>## Plan</div>
<div><br/></div>
<div>* next item 1</div>
<div>* next item 2</div>
<div><br/></div>
<div><br/></div>
</en-note>""")
      val expected = Seq("todo item 1", "todo item 2")
      assert(decoded == expected)
    }
  }
}