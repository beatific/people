package org.beatific.people.people

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

import org.beatific.people.utils.ImplicitUtils._
import org.beatific.people.utils.concurrency.Unavailable
import org.beatific.people.utils.concurrency.Available
import org.beatific.people.utils.concurrency.ThreadEach

abstract class Worker[T] extends People[T] {

  val time = People.time
  val inbox: Inbox[T] = PostOffice.inbox(this)
  
  HeadHunter + this

  def remainingPeople() = {
    time.pool.active
  }
  
  private def readMore() {
    inbox >> match {
      case Some(taken) => receive(taken)
      case None =>
    }
  }

  protected def work(working: T)

  private def read(letter: T) {

    work(letter)
    receiver.clear
    HeadHunter finish this
  }

  def receive(letter: T) {

    time(runnable = read(letter), onFinal = readMore) match {
      case Unavailable => {
        inbox + letter match {
          case None => HeadHunter << (this, letter)
          case Some(_) =>
        }
      }
      case Available =>
    }
  }

}