package conf

class ConfParser {

  def parse(text: String): Conf = {
    val lines = text.split("\n").map(formatLine)
    val path = lines.filter(pair => pair(0) == "PATH")(0)(1)
    val interval = lines
                    .map(array => Pair[String, String](array(0), array(1)))
                    .map(pair => translateIntervals(pair._1, pair._2)).sum
    return new Conf(interval, path)

  }

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
