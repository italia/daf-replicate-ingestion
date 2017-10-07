package api_calls

import api_calls.Point.GPS
import rest.RestClient

class ServicesQuery(val client: RestClient) extends Query {
  def build(point: GPS, maxDists: Int = -1, maxResults: Int = -1): String = {
    var query = baseUrl + s"format=$format&selection=${point.location}"
    if (maxDists != -1){
      query += s"&maxDists=$maxDists"
    }
    if(maxResults != -1){
      query += s"&maxResults=$maxResults"
    }
    return query
  }

  override val serviceUri = "http://www.disit.org/km4city/resource/METRO487"
}
