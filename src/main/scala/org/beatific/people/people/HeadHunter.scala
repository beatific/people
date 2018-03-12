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
  val returnCount: Map[String, Int] = Map()

  def +[T](people: People[T]) = {
    addressBook + people
  }

  def <<[T](people: People[T], letter: T) {

    returnCount ++ people.id
    val other: People[T] = this ? people.id
    other.receive(letter)
  }

  def ?[T](name: String): People[T] = {
    val address: Option[Worker[T]] = addressBook find name 

    address match {
      case Some(people) => people
      case None => {
        PresentWorldChecker() match {
          case true => synchronized {

            val worker: Option[Worker[T]] = addressBook find name

            worker match {
              case Some(people) => people
              case None =>
                val human: Option[People[T]] = People(name)

                human match {
                  case Some(people) => people
                  case None         => throw new RuntimeException("There is no people named '" + name + "'!")
                }
            }
          }
          case false =>
            val human: Option[People[T]] = OtherWorldBroker(name)

            human match {
              case Some(ailen) => ailen
              case None => {
                addressBook findForce name
              }
            }
        }
      }
    }
  }

  def finish[T](people: People[T]) {

    try {
      addressBook.release(people)
    } catch {
      case e: IllegalMonitorStateException =>
    }
  }

}