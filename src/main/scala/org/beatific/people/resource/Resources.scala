package org.beatific.people.resource

import scala.collection.immutable.Queue

object Resources {
  
  var resources :Map[Resource, Time] = Map()
  var alarm :Queue[Resource] = Queue()
  
  def register(resource :Resource) {
    resources += resource -> resource.getTime
  }
}