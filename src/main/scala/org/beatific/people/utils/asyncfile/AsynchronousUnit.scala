package org.beatific.people.utils.asyncfile

import java.nio.channels.AsynchronousFileChannel

class AsynchronousUnit(iter: AsynchronousIterator, key: Int) {

  val begin: Long = iter.cap.toLong * key.toLong + iter.pos
  val end: Long = if (iter.cap.toLong * (key.toLong + 1L) - 1L + iter.pos > iter.max) iter.max - 1L else iter.cap.toLong * (key.toLong + 1L) - 1L + iter.pos

  def cache(temp: String) {
    iter.cache :+= temp
  }

  def cache(): String = {
    iter.cache.length match {
      case length if length == 0 => ""
      case _                     => {
        val cache :String = iter.cache.reduce(_ + _)
        iter.cache = Nil
        cache
      }
    }
  }

  def afterComplete(f: () => Unit) {
    iter.complete.count(f)
  }

  def processBy(result: String, converter: List[String] => Option[List[String]], handler: List[String] => Unit, finish: => Unit = {}) {

    iter.complete.countBySorted(key, result, converter, finish) match {
      case Some(data) => iter.synchronized ( handler(data) )
      case None       =>
    }
  }

  def processAfter(result: String, converter: List[String] => Option[String], handler: String => Unit) {

    iter.complete.countBySortedAfterAll(key, result, converter) match {
      case Some(data) => handler(data)
      case None       =>
    }
  }
}