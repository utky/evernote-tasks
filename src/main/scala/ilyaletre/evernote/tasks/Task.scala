package ilyaletre.evernote.tasks

import ilyaletre.evernote.tasks.GetTemplate._

object WeeklyReview {
  def apply(evernote: Evernote, templateTitle: String): Unit = {
    val template = evernote.getTemplate(templateTitle)
    val content = template.render(Map())
  }
}