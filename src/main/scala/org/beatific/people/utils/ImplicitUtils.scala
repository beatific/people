package org.beatific.people.utils

import java.util.concurrent.Executors
import java.nio.ByteBuffer
import scalax.io.Resource
import scalax.file.Path
import net.liftweb.json.Serialization
import net.liftweb.json._
import org.beatific.people.utils.asyncfile.SeekableFile
import scala.io.Source
import java.util.concurrent.ExecutorService
import java.util.concurrent.ThreadPoolExecutor
import scala.util.Try
import org.beatific.people.utils.concurrency.ThreadPool
import org.beatific.people.people.People
import org.beatific.people.utils.reflection.ClassSupports
import org.beatific.people.resource.Time

object ImplicitUtils {

  implicit val formats = DefaultFormats

  implicit class SingleIntUtils(i: Int) {
    def threadpool = new ThreadPool(i)
    def apply[T <: Time](time:Class[T]):T = Time.newInstance(time, i)
  }

  implicit class SingleExecuteServiceUtils(service: ExecutorService) {

    def size = if (service.isInstanceOf[ThreadPoolExecutor]) service.asInstanceOf[ThreadPoolExecutor].getMaximumPoolSize else -1
    def active = if (service.isInstanceOf[ThreadPoolExecutor]) service.asInstanceOf[ThreadPoolExecutor].getActiveCount else -1
  }
  
  implicit class SingleClassUtils[T](clazz :Class[T]) {
    def genericType = ClassSupports.genericType(clazz, 0)
    def genericType(index :Int) = ClassSupports.genericType(clazz, index)
  }
  
  implicit class SingleByteBufferUtils(buffer: ByteBuffer) {
    def mkString = ByteBufferSupports.btos(buffer)
    def mkString(encoding: String) = ByteBufferSupports.btos(buffer, encoding)
    def mkString(encoding: String, start: Int, end: Int) = ByteBufferSupports.btos(buffer, encoding, start, end)
  }
  
  implicit class SingleMapUtilsWhenIntegerValue[T](var map: Map[T, Int]) {
    def ++ (key :T) = {
      
      map.get(key) match {
        case Some(count) => map += (key -> (count + 1))
        case None => map += (key -> 0)
      }
    }
  }
  

  implicit class SingleObjectUtils(obj: Object) {

    def genericType = ClassSupports.genericType(obj.getClass, 0)
    def genericType(index :Int) = ClassSupports.genericType(obj.getClass, index)
    
    def writeObject(name: String) {
      val path = Path(name)
      path.deleteIfExists(force = true)
      path.createFile(failIfExists = false)

      Resource.fromFile(name).write(Serialization.write(obj))
    }
  }

  implicit class SingleStringUtils(val str: String) extends AnyVal {

    def af = new SeekableFile(str)
    def f = new RandomAccessFile(str)
    def asClassOf(basePackage:String) = ClassSupports.findClassName(basePackage, str)

    def loadObject[T]()(implicit manifest: Manifest[T]): T = {
      parse(Source.fromFile(str)("UTF-8").mkString).values.asInstanceOf[T]
    }

    def refine(): String = {
      """[가-힣ㄱ-ㅎㅏ-ㅣa-zA-Z0-9_%\[\]\"]""".r.findAllIn(str).mkString
    }

    def indexesBetweenStrings(target: String, from: String, to: String): List[Int] = {

      var indexes: List[Int] = Nil

      var fromIndex = str.indexOf(from)
      var toIndex = str.indexOf(to)
      var targetIndex = str.indexOf(target)

      while (fromIndex > 0 && toIndex > 0 && targetIndex > 0) {
        fromIndex = str.indexOf(from, toIndex)
        toIndex = str.indexOf(to, fromIndex)

        if (fromIndex < targetIndex && targetIndex < toIndex) {
          indexes :+= targetIndex
          targetIndex = str.indexOf(target, targetIndex)
        }
      }

      indexes
    }

    def indexesNotBetweenStrings(target: String, from: String, to: String): List[Int] = {
      var indexes: List[Int] = Nil
      var targetIndex = str.indexOf(target)

      val between = str.indexesBetweenStrings(target, from, to)

      while (targetIndex > 0) {
        if (!between.contains(targetIndex)) indexes :+= targetIndex
        targetIndex = str.indexOf(target, targetIndex)
      }

      indexes
    }

  }

  implicit class MultiStringUtils(val str: List[String]) {

    def removeAfterString(from: String): List[String] = {
      str.map {
        case line if line.contains("from") => {
          var fromline = line.indexOf(from)
          line.substring(0, fromline)
        }
        case line => line
      }
    }

    def removeBetweenStrings(from: String, to: String): List[String] = {
      var templines: List[(Int, Int)] = Nil
      var removelines: List[Int] = Nil
      var commentlines: List[Int] = Nil

      str.zipWithIndex.foreach {
        case (line, i) => {
          line match {
            case s if s.contains(from) => {
              if (s.contains(to)) commentlines :+= i
              else templines :+= (i, -1)
            }
            case s if s.contains(to) => {
              var (fromline, _): (Int, Int) = templines(templines.size - 1)

              templines = templines.dropRight(1)
              removelines :::= (fromline + 1 to i - 1 toList)
              commentlines :+= fromline
              commentlines :+= i
            }
            case _ => {}
          }
        }
      }

      str.zipWithIndex.filterNot {
        case (line, i) => removelines.contains(i)
      } map {
        case (line, i) if commentlines.contains(i) => {

          var fromline = line.indexOf(from)
          var toline = line.indexOf(to)

          fromline = if (fromline == -1) 0 else fromline
          toline = if (toline == -1) line.size else toline + 2

          line.substring(0, fromline) + line.substring(toline)
        }
        case (line, i) => line
      }
    }

  }
}