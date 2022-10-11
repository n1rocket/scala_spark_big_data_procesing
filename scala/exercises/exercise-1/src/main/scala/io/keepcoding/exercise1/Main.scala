package io.keepcoding.exercise1

import java.net.URI
import java.util.UUID


object Main {
  abstract class Message(val id: UUID)

  trait Printable {
    def print(): Unit
  }

  trait Runnable {
    def start(): Unit

    def stop(): Unit
  }

  trait Coloreable {
    val background_color: String
  }

  class Text(id: UUID, val text: String) extends Message(id) with Printable with Coloreable {
    override val background_color: String = "Azul"

    //def apply(x: Int) = x + y

    override def print(): Unit = println(s"Text: $id $text")
  }

  object Text{
    def unapply(message: Message): Option[UUID] = Some(message.id)
    def apply(text:String) = new Text(UUID.randomUUID(), text)
  }

  class Attachment(id: UUID, val uri: URI) extends Message(id) with Printable {
    override def print(): Unit = println(s"Attachment: $id $uri")
  }

  object Attachment {
    def unapply(message: Message): Option[UUID] = Some(message.id)

    def apply(uri: URI) = new Attachment(UUID.randomUUID(), uri)
  }

  class Sound(id: UUID, val uri: URI) extends Message(id) with Runnable {
    override def start(): Unit = println(s"Sound: Starting")

    override def stop(): Unit = println(s"Sound: Stoping")
  }

  object Sound {
    def unapply(message: Message): Option[UUID] = Some(message.id)
    def apply(uri: URI) = new Sound(UUID.randomUUID(), uri)
  }

  def printType(msg:Message): Unit = msg match {
    case Attachment(uri) => println(s"Es un Attachment con uri $uri")
    case Sound(uri) => println(s"Es un Sound con uri $uri")
    case Text(id) => println(s"Es un Text con id $id")
  }

  def main(args: Array[String]): Unit = {
    val text = new Text(UUID.randomUUID(), "Texto 1")
    val attach = new Attachment(UUID.randomUUID(), URI.create("https://google.com"))
    val sound = new Sound(UUID.randomUUID(), URI.create("https://google.com"))

    println(s"${text.background_color}")
    text.print()
    attach.print()
    sound.start()
    sound.stop()

    //Extras
    val text2 = Text("Segundo sin UUID")
    val attach2 = Attachment(URI.create("https://google.com"))
    val sound2 = Sound(URI.create("https://google.com"))

    println(s"${text2.background_color}")
    text2.print()
    attach2.print()
    sound2.start()
    sound2.stop()

    printType(attach)
  }
}
