package org.beatific.people.people

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import scala.collection.mutable.Queue

//class Inbox[T](size: Int) {
//
//  val inbox: Queue[T] = Queue()
//  var remainingCapacity = size
//  var max = size
//
//  def take: Option[T] = synchronized {
//
//    inbox.isEmpty match {
//      case false => {
//        remainingCapacity += 1
//        Some(inbox.dequeue)
//      }
//      case true => None
//    }
//  }
//
//  def +(letter: T): Option[Int] = synchronized {
//
//    remainingCapacity match {
//      case capacity if capacity > 0 =>
//        inbox += letter; remainingCapacity -= 1; Some(remainingCapacity)
//      case _                        => None
//    }
//  }
//
//  def ++ : Int = synchronized {
//    remainingCapacity += size
//    max += size
//    remainingCapacity
//  }
//
//  def -- : Option[Int] = synchronized {
//    remainingCapacity - size match {
//      case capacity if capacity < size => None
//      case capacity                    => remainingCapacity -= size; max -= size; Some(remainingCapacity)
//    }
//  }
//}