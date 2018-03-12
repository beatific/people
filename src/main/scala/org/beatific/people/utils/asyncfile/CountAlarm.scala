package org.beatific.people.utils.asyncfile

import scala.collection.immutable.ListMap
import org.beatific.people.utils.ImplicitUtils._
import scala.reflect._
import java.util.concurrent.LinkedBlockingQueue

class CountAlarm[T, V](last: Int)(implicit tag: ClassTag[T]) {

  var current = 0
  var buffer: Array[T] = new Array(last)
  var cursor = 0

  def count(f: () => Unit) {
    synchronized {
      current += 1
      current match {
        case n if n == last => f()
        case c              => None
      }
    }
  }

  private def push(queue :List[T]): List[T] = {
    
    cursor match {
      case c if c >= last => queue
      case c              => {
        cursor += 1
        buffer(c) match {
          case value if value == null => queue
          case value => push(queue :+ buffer(c)) 
        }
      }
    }
  }

  private def push(): List[T] = push(Nil)

  def countBySorted(index: Int, data: T, converter: List[T] => Option[List[V]], finish: => Unit): Option[List[V]] = {

    synchronized {
      buffer.update(index, data)
      current += 1
      current match {
        case n if n == last => finish
        case c              =>
      }
      converter(push())
    }
  }

  def countBySortedAfterAll(index: Int, data: T, converter: List[T] => Option[V]): Option[V] = {

    synchronized {
      buffer.update(index, data)
      current += 1
      current match {
        case n if n == last => converter(buffer.toList)
        case c              => None
      }
    }

  }
}