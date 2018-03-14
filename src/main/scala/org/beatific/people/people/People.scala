package org.beatific.people.people

import org.beatific.people.utils.concurrency.ThreadEach
import org.beatific.people.utils.ImplicitUtils._
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

object People {

  var basePackage: String = null

//  val time = Runtime.getRuntime.availableProcessors.threadpool
  val time = 1.threadpool

  def apply[T](id: String): Option[People[T]] = {

    id.asClassOf(basePackage) match {
      case Some(clazz) => Some(clazz.newInstance().asInstanceOf[People[T]])
      case None        => None
    }
  }
}

trait People[T] {

//  val size: Int = Runtime.getRuntime.availableProcessors
  val size: Int = 1

  def receive(letter: T)

  val receiver: ThreadEach[People[_]] = new ThreadEach[People[_]]()

  protected def to[V]: People[V] = receiver() match {
    case Some(people) => people.asInstanceOf[People[V]]
    case None         => throw new RuntimeException("Please specify who you want to send to!")
  }

  def >>[V](address: String): People[T] = {
    val other: People[V] = HeadHunter ? address
    receiver(other)
    this
  }
  
  def send[V](people :People[V], letter: V) = {
    people.receive(letter)
  }

  def apply[V](letter: V): People[T] = {
    this.to.receive(letter)
    this
  }
}