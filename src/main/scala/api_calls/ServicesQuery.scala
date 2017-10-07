package api_calls

import api_calls.Point.GPSPoint
/**
  * This class builds the query that will be sent to Km4city, in particular all the services data around an area.
  * For more information read the pdf page 17 at this link: http://www.disit.org/Sii-Mobility-SmartCityAPI-v1-6.pdf
  */
class ServicesQuery extends Query {

  /**
    * @param point Latitude and longitude
    * @param maxDists [Optional] Max area around point where the query search all the services data
    * @param maxResults [Optional] Max results to display (default 100)
    */
  def build(point: GPSPoint, maxDists: Int = -1, maxResults: Int = -1): String = {
    var query = baseUrl + s"format=$format&selection=${point.location}"
    if (maxDists != -1){
      query += s"&maxDists=$maxDists"
    }
    if(maxResults != -1){
      query += s"&maxResults=$maxResults"
    }
    return query
  }

  override val serviceUri = "http://servicemap.km4city.org/WebAppGrafo/"
}
