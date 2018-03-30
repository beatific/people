package org.beatific.people.people

import org.beatific.people.utils.concurrency.Unavailable
import org.beatific.people.utils.concurrency.Available

object WaitingWorker {
  
  def apply[T](worker :Worker[T]) :People[T] = new WaitingWorker(worker)
}

class WaitingWorker[T](worker: Worker[T]) extends People[T] {
  
  def receive(letter: T) {
    
    worker.time.force(runnable = worker.read(letter))
  }
}