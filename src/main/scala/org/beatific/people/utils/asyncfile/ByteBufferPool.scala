package org.beatific.people.utils.asyncfile

import java.nio.ByteBuffer
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

object ByteBufferPool {
  
  val CAPACITY = 1024
  private val pool: ByteBufferPool = new ByteBufferPool(Runtime.getRuntime.availableProcessors(), CAPACITY)
  
  def get = pool.get
  def release(buffer: ByteBuffer) = pool.release(buffer)
  
  def apply[T](r:ByteBuffer => T) :T = {
    val buffer = get
    
    try r(buffer) finally {
      release(buffer)
    }
  }
}

private class ByteBufferPool(size: Int, capacity: Int) {

  val pool: BlockingQueue[ByteBuffer] = new LinkedBlockingQueue(size)

  (1 until size toList).map(i => pool.add(ByteBuffer.allocateDirect(capacity)))

  def get = {
    pool.take()
  }

  def release(buffer: ByteBuffer) {
    buffer.clear()
    pool.add(buffer)
  }

}