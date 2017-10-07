package json2csv

trait JsonToCsv {
  def trasform(json: Map[String, _]) : String
}
