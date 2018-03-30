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
import java.nio.file.attribute.FileAttribute
import scala.collection.mutable.ArrayBuffer
import java.util.EnumSet
import java.nio.file.LinkOption

class AsyncFile(name: String)(implicit pool: ExecutorService = Runtime.getRuntime.availableProcessors.threadpool.pool) {

  val path: Path = Paths.get(name)

  Files.exists(path) match {
    case true  =>
    case false => Files.createFile(path)
  }

  var s: Long = System.currentTimeMillis()

  def size = Files.size(path)

  def write(text: String, encoding: String = Charset.defaultCharset().name())  {
    val channel: AsynchronousFileChannel = AsynchronousFileChannel.open(Paths.get(name), EnumSet.of(WRITE), pool)
    channel.write(ByteBuffer.wrap(text.getBytes(encoding)), size , null,
        new CompletionHandler[Integer, Object]() {
          def completed(res: Integer, obj: Object): Unit = {
            
           if (channel != null && channel.isOpen()) channel.close()
          }

          def failed(t: Throwable, obj: Object): Unit = {
            if (channel != null && channel.isOpen()) channel.close()
          }
          
        })
  }

  def read(f: String => Unit, encoding: String = Charset.defaultCharset().name(), start: Long = 0L, end: Long = size): Long = {

    val channel: AsynchronousFileChannel = AsynchronousFileChannel.open(Paths.get(name), EnumSet.of(READ), pool)
    innerRead(channel, f, encoding, start, end)((s, u, f) => u.processAfter(s, strs => {
      strs.length match {
        case length if length == 0 => None
        case _                     => Some(strs.reduce(_ + _))
      }
    }, s => { close(channel); f(s) }))
  }

  def readLines(f: List[String] => Unit, encoding: String = Charset.defaultCharset().name(), start: Long = 0L, end: Long = size): Long = {

    s = System.currentTimeMillis()
    val channel: AsynchronousFileChannel = AsynchronousFileChannel.open(Paths.get(name), EnumSet.of(READ), pool)
    innerRead(channel, f, encoding, start, end)((s, u, f) => u.processBy(s, strs => Some(strs.flatMap(str => {

      str match {
        case text if text contains "\n" => {
          (u.cache() + text).split("\n").map(_ + "\n") toList match {
            case lines :+ line => u.cache(line); lines
          }
        }
        case text => u.cache(text); Nil
      }

    })), strs => f(strs), close(channel)))
  }

  def readLine(f: String => Unit, encoding: String = Charset.defaultCharset().name(), start: Long = 0L, end: Long = size): Long = {

    s = System.currentTimeMillis()
    val channel: AsynchronousFileChannel = AsynchronousFileChannel.open(Paths.get(name), EnumSet.of(READ), pool)
    innerRead(channel, f, encoding, start, end)((s, u, f) => u.processBy(s, strs => Some(strs.flatMap(str => {

      str match {
        case text if text contains "\n" => {
          (u.cache() + text).split("\n").map(_ + "\n") toList match {
            case lines :+ line => u.cache(line); lines
          }
        }
        case text => u.cache(text); Nil
      }

    })), strs => strs.foreach(f(_)), close(channel)))
  }

  def readEach(f: String => Unit, encoding: String = Charset.defaultCharset().name(), start: Long = 0L, end: Long = Files.size(path)): Long = {
    val channel: AsynchronousFileChannel = AsynchronousFileChannel.open(Paths.get(name), EnumSet.of(READ), pool)
    innerRead(channel, f, encoding, start, end)((s, u, f) => { f(s); u.afterComplete(() => close(channel)) })
  }

  private def innerRead[T](channel: AsynchronousFileChannel, f: T => Unit, encoding: String, start: Long, end: Long)(readAfter: (String, AsynchronousUnit, T => Unit) => Unit): Long = {

    s = System.currentTimeMillis()
    val iter: AsynchronousIterator = new AsynchronousIterator(channel.size, ByteBufferPool.CAPACITY, start, end)

    for (i <- 0 until iter.repetitions) {
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
            close(channel)
            ByteBufferPool.release(buffer)
          }
        })
    }
    end
  }

  private def close(channel: AsynchronousFileChannel) {
    if (channel != null && channel.isOpen()) channel.close()
    println("Elapsed Time[" + (System.currentTimeMillis() - s) + "ms]")
  }
}