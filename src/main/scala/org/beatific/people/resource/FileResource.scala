package org.beatific.people.resource

import java.io.File
import org.beatific.people.people.Publisher
import org.beatific.people.event.Event


class FileResource(filename: String, event: Event, time :Time) extends Resource(time) {

  val file: File = new File(filename)
  var calltime : Long = -1
  
  def check() {
    
    calltime < file.lastModified() match {
      case true => Publisher >> event
      case false => 
    }
    
  }
}