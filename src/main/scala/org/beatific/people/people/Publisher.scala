package org.beatific.people.people

import org.beatific.people.event.Event
import org.beatific.people.utils.ImplicitUtils._

object Publisher {

  var observers: Map[Class[_], Observer[_]] = Map()

  def +[T <: Event](observer: Observer[T]) {
    observers += (observer.genericType.asInstanceOf[Class[T]] -> observer.asInstanceOf[Observer[T]])
  }

  def >>[T](event: T) {
    observers.get(event.getClass()) match {
      case Some(observer) => observer.asInstanceOf[Observer[T]].receive(event)
      case None           =>

    }
  }
}