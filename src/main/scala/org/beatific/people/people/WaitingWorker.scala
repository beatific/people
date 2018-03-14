package org.beatific.people.people

import org.beatific.people.utils.concurrency.Unavailable
import org.beatific.people.utils.concurrency.Available

object WaitingWorker {
  
  def apply[T](worker :Worker[T]) :People[T] = new WaitingWorker(worker)
}

class WaitingWorker[T](worker: Worker[T]) extends People[T] {
  
  override val size = worker.size
  val id = worker.id
  val inbox = worker.inbox
  val time = worker.time
  
  private def readMore() {
    inbox >> match {
      case Some(taken) => receive(taken)
      case None        =>
    }
  }
  
  private def read(letter : T) {
    worker.read(letter)
  }
  
  def receive(letter: T) {

    time(runnable = read(letter), onFinal = readMore) match {
      case Unavailable => inbox put letter
      case Available =>
    }
  }
}