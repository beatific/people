package org.beatific.people.people

import org.beatific.people.event.Event
import org.beatific.people.utils.ImplicitUtils.SingleObjectUtils
import org.beatific.people.utils.reflection.ClassSupports

object Publisher {

  var observers: Map[Class[_], List[Observer[_]]] = Map()
  
  def +[T <: Event](observer: Observer[T]) {
    val t = observer.genericType.asInstanceOf[Class[T]]
    observers +=  (observer.genericType.asInstanceOf[Class[T]] -> (observers.getOrElse(t, Nil) :+ observer.asInstanceOf[Observer[T]]))
  }

  def >>[T](event: T) {
    observers.get(event.getClass()) match {
      case Some(observers) => observers.foreach(_.asInstanceOf[Observer[T]].receive(event))
      case None           =>

    }
  }
}