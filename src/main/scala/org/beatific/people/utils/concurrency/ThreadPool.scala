package org.beatific.people.utils.concurrency

import java.util.concurrent.Executors

import scala.util.Try
import scala.util.control.NonFatal

import org.beatific.people.utils.ImplicitUtils._

abstract class Availability
case object Available extends Availability
case object Unavailable extends Availability

class ThreadPool(poolsize: Int) {

  val pool = Executors.newFixedThreadPool(poolsize)

  def apply[T](runnable: => T, onSuccess: T => Unit = { r: T => () }, onFail: Throwable => Unit = t => (), onFinal: => Unit = ()): Availability = synchronized {

    pool.active < poolsize match {
      case true =>
        pool.execute(new Runnable {
          def run() {
            try onSuccess(runnable) catch {
              case NonFatal(e) => onFail(e)
            } finally onFinal
          }
        })
        Available
      case false => Unavailable
    }
  }
}