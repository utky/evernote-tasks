package ilyaletre.evernote.tasks 

import collection.JavaConverters._
import com.evernote.auth.{EvernoteAuth, EvernoteService}
import com.evernote.clients.{ClientFactory}
import com.evernote.edam.`type`.{Notebook}

object Main extends App {
  val token = System.getenv("EVERNOTE_AUTH_TOKEN")
  val evernoteAuth = new EvernoteAuth(EvernoteService.SANDBOX, token)
  val factory = new ClientFactory(evernoteAuth)
  val noteStore = factory.createNoteStoreClient()
  noteStore.listNotebooks().asScala.foreach { println((_:Notebook)) }
}
