package org.beatific.people.utils

import java.io.File

object FileUtils {

  def filelist(dirs: String*) :List[String] = {
    dirs flatMap(dir => filelist(dir)) toList
  }
  
  def filelist(dir: String):List[String] = {
    filelist(new File(dir))
  }
  
  def filelist(dir: File):List[String] = {
    dir.listFiles().flatMap {
      f =>
        f match {
          case directory if directory.isDirectory => filelist(directory)
          case file if file.isFile                => List(file.getAbsolutePath)
          case _                                  => Nil
        }
    } toList
  }
}