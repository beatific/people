package org.beatific.people.utils.asyncfile

import java.nio.channels.AsynchronousFileChannel

class AsynchronousIterator(size: Long, capacity: Int, from: Long, to: Long) {

  val pos:Long = from
  val cap:Int = capacity
  val max:Long = if (size > to) to else size
  val repetitions = ((size - from.toDouble + 1) / capacity).ceil.toInt
  val complete: CountAlarm[String, String] = new CountAlarm(repetitions)
  var cache :List[String] = List()
  
  def unit(i: Int) : AsynchronousUnit =  new AsynchronousUnit(this, i)
}
