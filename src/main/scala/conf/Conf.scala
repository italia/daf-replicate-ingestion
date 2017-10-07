package conf

import java.io.File
import java.nio.file.Path

class Conf(val interval: Int, private val _path: String) {
  val path : String= new File(_path).getAbsolutePath
}
