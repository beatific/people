package org.beatific.people.utils.concurrency

class ThreadEach[T] {
  
  val local :java.lang.ThreadLocal[T] = new java.lang.ThreadLocal()
  
  def apply():Option[T] = {
    if(local.get == null) None
    else Some(local.get)
  }
  
  def apply(value: T) {
    local.set(value)
  }
  
  def clear() {
    local.remove()
  }
  
}