package org.beatific.people.people

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

import org.beatific.people.utils.ImplicitUtils._
import org.beatific.people.utils.concurrency.Unavailable
import org.beatific.people.utils.concurrency.Available
import org.beatific.people.utils.concurrency.ThreadEach

abstract class Worker[T] extends People[T] {

  val id: String = this.getClass.getName.substring(this.getClass.getName.lastIndexOf(".") + 1)
  val time = size.threadpool
  val inbox: Inbox[T] = PostOffice.inbox(this)

  def remainingPeople() = {
    time.pool.active + inbox.remainingCapacity
  }

  HeadHunter + this

  private def readMore() {
    inbox >> match {
      case Some(taken) => read(taken)
      case None        => 
    }
  }

  def read(letter: T) {
    try work(letter)
    finally readMore()
  }

  protected def work(working: T)

  def receive(letter: T) {

    time(runnable = read(letter)) match {
      case Unavailable => {
        inbox + letter match {
          case None    => HeadHunter << (this, letter)
          case Some(_) =>
        }
      }
      case Available =>
    }
  }

}