package json2csv

import java.util.Calendar

class JsonToCsvImpl extends JsonToCsv{

  val sep = " ; "
  val fields: String = List("geometry_type", "geometry_coordinates", "name", "tipo", "typeLabel", "serviceType", "serviceUri", "distance").reduce((a, b) => a + sep + b)

  /**
    * Return a csv like the example here: https://github.com/italia/daf-replicate-ingestion/issues/2
    * @param json a json parsed from scala.util.parsing.json.JSON
    * @return
    */
  override def trasform(json: Map[String, _]): String = {
    val busStopsList = json("BusStops").asInstanceOf[Map[String, _]]("features").asInstanceOf[List[Map[String, _]]]
    val sensorsSitesList = json("SensorSites").asInstanceOf[Map[String, _]]("features").asInstanceOf[List[Map[String, _]]]
    val servicesList = json("Services").asInstanceOf[Map[String, _]]("features").asInstanceOf[List[Map[String, _]]]
    val timestamp = "\n" + Calendar.getInstance.getTime.toString
    val queryResultsList = busStopsList ++ sensorsSitesList ++ servicesList
    val csvResults = queryResultsList.map(flatQueryLine)
    return (timestamp +: csvResults).reduce((a,b) => a + "\n" + b)
  }

  def flatQueryLine(line: Map[String, _]) : String = {
    val geometry = line("geometry").asInstanceOf[Map[String, _]]
    val type_g = geometry("type").asInstanceOf[String]
    val coordinates_g = geometry("coordinates").toString
    val properties = line("properties").asInstanceOf[Map[String, String]]
    val name = properties("name")
    val tipo = properties("tipo")
    val typeLabel = properties("typeLabel")
    val serviceType = properties("serviceType")
    val serviceUri = properties("serviceUri")
    val distance = properties("distance")
    return List(type_g, coordinates_g, name, tipo, typeLabel, serviceType, serviceUri, distance).reduce((a,b) => a + sep + b)
  }

}
