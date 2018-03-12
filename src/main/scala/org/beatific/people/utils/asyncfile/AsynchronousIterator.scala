package org.beatific.people.utils.asyncfile

import java.nio.channels.AsynchronousFileChannel

class AsynchronousIterator(size: Long, capacity: Int, from: Long, to: Long) {

  val pos = from
  val cap = capacity
  val max = if (size > to) to else size
  val repetitions = ((size - from.toDouble + 1) / capacity).ceil.toInt
  def units: List[AsynchronousUnit] = (0 until repetitions toList).map(i => new AsynchronousUnit(this, i))
  val complete: CountAlarm[String, String] = new CountAlarm(repetitions)
  var cache :List[String] = List()  
}



