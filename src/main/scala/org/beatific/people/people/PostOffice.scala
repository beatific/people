package org.beatific.people.people

object PostOffice {

  var mailbox: Map[String, Inbox[_]] = Map()

  def inbox[T](worker: Worker[T]): Inbox[T] = synchronized {
    mailbox.get(worker.id) match {
      case Some(inbox) =>
        inbox ++; inbox.asInstanceOf[Inbox[T]]
      case None => {
        val inbox: Inbox[T] = new Inbox(worker.size * 2)
        mailbox += (worker.id -> inbox)
        inbox
      }
    }
  }
}