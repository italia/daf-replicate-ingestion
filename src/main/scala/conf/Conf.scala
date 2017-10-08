package conf

import java.io.File
import java.nio.file.Path

class Conf(val interval: Int, private val _path: String, val latitude: Int, val longitude: Int) {
  val path : String= new File(_path).getAbsolutePath
  var maxDists : Int = -1
  var maxResults : Int = -1
}
