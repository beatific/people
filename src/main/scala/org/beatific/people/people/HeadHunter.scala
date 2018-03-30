package org.beatific.people.people

import org.beatific.people.utils.ImplicitUtils._
import org.beatific.people.world.PresentWorldChecker
import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.concurrent.locks.ReentrantLock
import org.beatific.people.world.OtherWorldBroker
import org.beatific.people.event.Event
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

object HeadHunter {

  var addressBook = AddressBook
  var publisher = Publisher

  def +[T](people: Worker[T]) = {
    addressBook + people
  }

  def <<[T](people: Worker[T], letter: T) {

//    val other: People[T] = this ? people.id
    WaitingWorker(people).receive(letter)
  }

  def ?[T](name: String): People[T] = {
    val worker: Worker[T] = addressBook find name

    worker.remainingPeople() match {
      case remaining if remaining > 0 => worker
      case _ => {
        val human: Option[People[T]] = OtherWorldBroker(name)

        human match {
          case Some(ailen) => ailen
          case None => worker
        }
      }
    }
  }

}