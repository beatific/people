package org.beatific.people.resource

abstract class Resource(time :Time) {
  
  Resources.register(this)
  def getTime = time
  def alramTime :Int = 0
}