package org.beatific.people.resource

case class Miliseconds(time : Int) extends Time(time)
case class Seconds(time : Int) extends Time(time)
case class Minutes(time : Int) extends Time(time)
case class Hours(time : Int) extends Time(time)
case class Days(time : Int) extends Time(time)
case object Infinite extends Time(0)

object Time {
  def newInstance[T <: Time](time: Class[T], i: Int):T = {
    time.getConstructor(classOf[Int]).newInstance(i.asInstanceOf[Object])
  }
}

abstract class Time(value :Int) {
  
  def apply() :Int = {
    this match {
      case Miliseconds(time) => time
      case Seconds(time) => time * 1000
      case Minutes(time) => time * 1000 * 60
      case Hours(time) => time * 1000 * 60 * 60
      case Days(time) => time * 1000 * 60 * 60 * 24
      case Infinite => -1
    }
  }
  
  def +(time :Time) :Time= {
    time match {
      case Infinite => Infinite
      case _ => this match {
        case Infinite => Infinite
        case _ => Miliseconds(this() + time()) 
      }
    }
  }
}