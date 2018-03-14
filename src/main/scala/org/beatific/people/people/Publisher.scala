package org.beatific.people.people

import org.beatific.people.event.Event
import org.beatific.people.utils.ImplicitUtils.SingleObjectUtils
import org.beatific.people.utils.reflection.ClassSupports

object Publisher {

  var observers: Map[Class[_], Observer[_]] = Map()
  
//  ClassSupports.findClassBySuperClass("org.beatific", classOf[Observer[_ <: Event]]) foreach {
//    observer => {
//      val ob = observer.newInstance()
//      observers += (ob.genericType.asInstanceOf[Class[_ <: Event]] -> ob.asInstanceOf[Observer[_ <: Event]])
//    }
//  }

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