package org.beatific.people.people

import scala.util.Random

object AddressBook {

  private var addressBook: Map[String, Worker[_]] = Map()

  def +[T](people: Worker[T]) = synchronized {

    addressBook.get(people.id) match {
      case Some(saram) =>
      case None        => addressBook += (people.id -> people)
    }
  }

  def find[T](name: String): Worker[T] = synchronized {
    addressBook.get(name) match {
      case Some(saram) => saram.asInstanceOf[Worker[T]]
      case None => {
        val people: Option[People[T]] = People(name)
        people match {
          case Some(pp) => pp.asInstanceOf[Worker[T]]
          case None     => throw new RuntimeException("There is no people named '" + name + "'!")
        }
      }
    }
  }

}