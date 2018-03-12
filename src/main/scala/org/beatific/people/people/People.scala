package org.beatific.people.people

import org.beatific.people.utils.concurrency.ThreadEach
import org.beatific.people.utils.ImplicitUtils._
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

object People {

  var basePackage :String = null
  
  val time = Runtime.getRuntime.availableProcessors.threadpool
  
  def apply[T](id: String): Option[People[T]] = {

    id.asClass(basePackage) match {
      case Some(clazz) => Some(clazz.newInstance().asInstanceOf[People[T]])
      case None        => None
    }
  }
}

trait People[T] {

  val id: String = this.getClass.getName.substring(this.getClass.getName.lastIndexOf(".") + 1)
  val size: Int = Runtime.getRuntime.availableProcessors
  
  def receive(letter: T)
  
  val receiver: ThreadEach[People[_]] = new ThreadEach[People[_]]()
  
  protected def to[V]: People[V] = receiver() match {
    case Some(people) => people.asInstanceOf[Worker[V]]
    case None         => throw new RuntimeException("Please specify who you want to send to!")
  }
  
  def >>[V](address: String): People[T] = {
    val other: People[V] = HeadHunter ? address
    receiver(other)
    this
  }
  
  def apply[V](letter: V): People[T] = {
    this.to.receive(letter)
    this
  }
}