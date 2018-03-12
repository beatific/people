package org.beatific.people.people

import scala.util.Random

object AddressBook {

  private var addressBook: Map[String, List[People[_]]] = Map()
  private var busyMap: Map[People[_], Int] = Map()

  def +[T](people: People[T]) = synchronized {

    busyMap += (people -> 0)
    addressBook.get(people.id) match {
      case Some(saram) => {
        addressBook += (people.id -> (saram :+ people))
      }

      case None => {
        addressBook += (people.id -> List(people))
      }
    }
  }

  def -(name: String): Option[People[_]] = synchronized {
    addressBook.get(name) match {
      case Some(people) => {
        people.length == 1 match {
          case true => None
          case false => {
            val last = people.last
            addressBook += name -> people.filterNot(_ == last)
            busyMap -= last
            Some(last)
          }
        }
      }
      case None => None
    }
  }

  def release[T](people: People[T]) = synchronized {
    busyMap.get(people) match {
      case Some(use) => busyMap += (people -> (use - 1))
      case None      =>
    }
  }

  def find[T](name: String): Option[Worker[T]] = synchronized {

    addressBook.get(name) match {
      case Some(saram) => {
        val min = saram.reduce((a, b) => if (busyMap.getOrElse(a, Int.MaxValue) > busyMap.getOrElse(b, Int.MaxValue)) b else a)

        (min.size - busyMap.getOrElse(min, 0)) match {
          case afford if afford > 0 => {
            busyMap += min -> (min.size - afford + 1)
            Some(min.asInstanceOf[Worker[T]])
          }
          case busy => None
        }
      }
      case None => None
    }
  }
  
  def findForce[T](name: String):People[T] = synchronized {
    addressBook.get(name) match {
      case Some(saram) => saram(Random.nextInt(saram.size-1)).asInstanceOf[Worker[T]]
      case None => {
        val people : Option[People[T]] = People(name)
        people match {
          case Some(pp) => pp
          case None => throw new RuntimeException("Can not find People named [" + name + "]") 
        }
      }
    }
  }

}