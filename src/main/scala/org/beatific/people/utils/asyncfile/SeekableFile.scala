package org.beatific.people.utils.asyncfile

import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.channels.CompletionHandler
import java.nio.file.Paths
import java.nio.file.StandardOpenOption.READ
import java.nio.file.StandardOpenOption.WRITE
import java.nio.charset.Charset
import org.beatific.people.utils.RandomAccessFile
import org.beatific.people.utils.ImplicitUtils._
import java.nio.file.Path
import java.nio.file.Files
import java.util.concurrent.ExecutorService

class SeekableFile(name: String)(implicit pool :ExecutorService = Runtime.getRuntime.availableProcessors.threadpool.pool) {

  val path: Path = Paths.get(name)
  System.err.println("thread pool size[" + pool.size + "]")
  
  def size = Files.size(path)

  def read(f: String => Unit, encoding: String = Charset.defaultCharset().name(), start: Long = 0L, end: Long = size) :Long ={

    val channel: AsynchronousFileChannel = AsynchronousFileChannel.open(Paths.get(name), READ)
    innerRead(channel, f, encoding, start, end)((s, u, f) => u.processAfter(s, strs => {
      strs.length match {
        case length if length == 0 => None
        case _ => Some(strs.reduce(_ + _))
      }
    }, s => { close(channel); f(s) }))
  }
  
  def readLine(f: String => Unit, encoding: String = Charset.defaultCharset().name(), start: Long = 0L, end: Long = size) :Long ={

    val channel: AsynchronousFileChannel = AsynchronousFileChannel.open(Paths.get(name), READ)
    innerRead(channel, f, encoding, start, end)((s, u, f) => u.processBy(s, strs => Some(strs.flatMap(str => {
      
      str match {
        case text if text contains "\n" => {
          (u.cache() + text).split("\n") toList match {
            case lines :+ line => u.cache(line); lines
          }
        }
        case text => u.cache(text); Nil
      }
      
    })), strs => strs.foreach(f(_)), close(channel) ))
  }

  def readEach(f: String => Unit, encoding: String = Charset.defaultCharset().name(), start: Long = 0L, end: Long = Files.size(path)) :Long = {
    val channel: AsynchronousFileChannel = AsynchronousFileChannel.open(Paths.get(name), READ)
    innerRead(channel, f, encoding, start, end)((s, u, f) => { f(s); u.afterComplete(() => close(channel)) })
  }

  private def innerRead(channel: AsynchronousFileChannel, f: String => Unit, encoding: String, start: Long, end: Long)(readAfter: (String, AsynchronousUnit, String => Unit) => Unit) :Long = {

    val iter: AsynchronousIterator = new AsynchronousIterator(channel.size, ByteBufferPool.CAPACITY, start, end)

    for ( i <- 0 until iter.repetitions) {
      val unit = iter.unit(i)
      val buffer: ByteBuffer = ByteBufferPool.get
      channel.read(buffer, unit.begin, unit,
        new CompletionHandler[Integer, AsynchronousUnit]() {
          def completed(res: Integer, unit: AsynchronousUnit): Unit = {

            buffer.hasRemaining() match {
              case true  => readAfter(buffer.mkString(encoding, 0, unit.end - unit.begin toInt), unit, f)
              case false => readAfter(buffer.mkString(encoding), unit, f)
            }

            ByteBufferPool.release(buffer)
          }

          def failed(t: Throwable, unit: AsynchronousUnit): Unit = {
            ByteBufferPool.release(buffer)
          }
        })
    }
    end
  }

  private def close(channel: AsynchronousFileChannel) {
    if (channel != null && channel.isOpen()) channel.close();
  }
}