package org.beatific.people.utils

import java.io.File

object FileUtils {

  def file(child: String, parent: File) = {
    new File(parent.getAbsolutePath + File.separator + child)
  }

  def filelist(dir: String): Array[String] = {
    filelist(new File(dir))
  }

  def filelist(dir: File): Array[String] = {

    val files = dir.list
    files ++ files.filter(file(_, dir).isDirectory).flatMap(directory => filelist(file(directory, dir)))
  }

  def filelist(dirs: String*): Array[String] = {
    dirs flatMap (dir => filelist(dir)) toArray
  }

}