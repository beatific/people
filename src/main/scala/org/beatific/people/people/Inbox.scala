package org.beatific.people.people

import scala.collection.mutable.Queue
import org.beatific.people.utils.ResizableBlockingQueue
import java.util.concurrent.BlockingQueue

class Inbox[T](size: Int) {

  val inbox: ResizableBlockingQueue[T] = new ResizableBlockingQueue(size)
  
  def remainingCapacity = inbox.remainingCapacity

  def take: T = inbox.take()
  
  def put(letter :T) = inbox.put(letter)
  
  def >> : Option[T] = {

    inbox.takeIfNotEmpty match {
      case nullObject if nullObject == null => None
      case letter => Some(letter)
    }
    
  }

  def +(letter: T): Option[T] = {

    inbox.putIfNotFull(letter) match {
      case true => Some(letter)
      case false => None
    }
  }

  def ++  = inbox.resize(inbox.capacity() +  inbox.size())

  def -- = inbox.resize(inbox.capacity() -  inbox.size())
}