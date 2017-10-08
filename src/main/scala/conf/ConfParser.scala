package conf

import java.lang.IndexOutOfBoundsException

class ConfParser {

  def parse(text: String): Conf = {
    val lines = text.split("\n").filterNot(p => p.startsWith("#")).map(formatLine)
    val path = getValue(lines, "PATH")
    val interval = lines
                    .map(pair => translateIntervals(pair(0), pair(1))).sum
    val latitude = getValue(lines, "LATITUDE").toInt
    val longitude = getValue(lines, "LONGITUDE").toInt
    val conf = new Conf(interval, path, latitude, longitude)
    try {
      val maxDists = getValue(lines, "MAXDISTS").toInt
      conf.maxDists = maxDists
    } catch{
      case e: ArrayIndexOutOfBoundsException =>
    }
    try {
      val maxResults = getValue(lines, "MAXRESULTS").toInt
      conf.maxResults = maxResults
    } catch{
      case e: ArrayIndexOutOfBoundsException =>
    }

    return conf
  }

  def getValue(lines: Array[Array[String]], name: String):  String =
    lines.filter(pair => pair(0) == name)(0)(1)

  def translateIntervals(typeInverval: String, value: String) : Int =
    typeInverval match{
      case "INTERVAL_MICROSECONDS" => value.toInt
      case "INTERVAL_MILLISECONDS" => value.toInt * 1000
      case "INTERVAL_SECONDS" => value.toInt * 1000 * 1000
      case "INTERVAL_MINUTES" => value.toInt * 1000 * 1000 * 60
      case "INTERVAL_HOURS" => value.toInt * 1000 * 1000 * 60 * 60
      case "INTERVAL_DAYS" => value.toInt * 1000 * 1000 * 60 * 60 * 24
      case _ => 0
    }

  private def formatLine(line: String) : Array[String] =
    line.trim.split("=")

}
