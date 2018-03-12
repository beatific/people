package org.beatific.people.people

import org.beatific.people.utils.ImplicitUtils._
import org.beatific.people.utils.concurrency.ThreadEach
import org.beatific.people.event.Event

abstract class Observer[T <: Event] extends People[T] {

  val time = size.threadpool
  
  Publisher + this
  
  def observe(finish : => Unit)

  def receive(letter: T): Unit = {
    observe(HeadHunter finish this)
  }
  
}