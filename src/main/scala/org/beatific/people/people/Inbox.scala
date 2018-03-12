package org.beatific.people.people

import scala.collection.mutable.Queue
import org.beatific.people.utils.ResizableBlockingQueue
import java.util.concurrent.BlockingQueue

class Inbox[T](size: Int) {

  val inbox: ResizableBlockingQueue[T] = new ResizableBlockingQueue(size)

  def take: T = synchronized ( inbox.take() )
  
  def put(letter :T) = synchronized ( inbox.put(letter) )
  
  def >> : Option[T] = synchronized {

    inbox.isEmpty match {
      case false => {
        Some(inbox.take())
      }
      case true => None
    }
  }

  def +(letter: T): Option[Int] = synchronized {

    inbox.remainingCapacity match {
      case capacity if capacity > 0 =>
        inbox.put(letter); Some(inbox.remainingCapacity)
      case _                        => None
    }
  }

  def ++  = inbox.resize(inbox.capacity() +  inbox.size())

  def -- = inbox.resize(inbox.capacity() -  inbox.size())
}