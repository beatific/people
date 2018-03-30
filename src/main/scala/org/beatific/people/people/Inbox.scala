package org.beatific.people.people

import scala.collection.mutable.Queue
import org.beatific.people.utils.ResizableBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

class Inbox[T](size: Int) {

  val inbox: ResizableBlockingQueue[T] = new ResizableBlockingQueue(size)
  def remainingCapacity = inbox.remainingCapacity

  def take: T = {
    System.err.println(Thread.currentThread() + ":take start")
    val t = inbox.take()
    System.err.println(Thread.currentThread() + ":take end")
    t
    
//    inbox.takeIfNotEmpty match {
//      case nullObject if nullObject == null => throw new RuntimeException("take nullObject")
//      case letter => letter
//    }
  }
  
  def put(letter :T) = {
    System.err.println(Thread.currentThread() + ": put start")
    inbox.put(letter)
    System.err.println(Thread.currentThread() + ":put end")
//    inbox.putIfNotFull(letter)
  }
  
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