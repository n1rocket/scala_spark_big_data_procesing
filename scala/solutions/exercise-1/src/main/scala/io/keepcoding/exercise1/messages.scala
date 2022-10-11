package io.keepcoding.exercise1

import java.net.URI
import java.util.UUID

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

sealed trait Message {
  val id: UUID
}

case class TextMessage(id: UUID, content: String) extends Message with Printable with Coloreable{
  override val background_color: String = "Red"
  override def print(): Unit = println(s"Content: $content")
}

object TextMessage {
  def apply(content: String) = TextMessage(UUID.randomUUID(), content)
}

case class AttachmentMessage(id: UUID, attachmentURI: URI) extends Message with Printable {
  override def print(): Unit = println(s"Content: $attachmentURI")
}

object AttachmentMessage {
  def apply(attachmentURI: URI) = AttachmentMessage(UUID.randomUUID(), attachmentURI)
}

case class SoundMessage(id: UUID, soundURI: URI) extends Message with Runnable {
  override def start(): Unit = println(s"Starting sound: $soundURI")
  override def stop(): Unit = println(s"Stopping sound: $soundURI")
}

object SoundMessage {
  def apply(soundURI: URI) = SoundMessage(UUID.randomUUID(), soundURI)
}

object Main {
  def main(args: Array[String]): Unit = {
    val text = TextMessage(UUID.randomUUID(), "This is the content")
    val text2 = TextMessage("This is the content")
    val attach = AttachmentMessage(UUID.randomUUID(), new URI("http://myUri"))
    val attach2 = AttachmentMessage(new URI("http://myUri"))
    val sound = SoundMessage(UUID.randomUUID(), new URI("http://myUri/sound"))
    val sound2 = SoundMessage(new URI("http://myUri/sound"))

    sound.start()
    text.print()
    attach.print()

  }
}

