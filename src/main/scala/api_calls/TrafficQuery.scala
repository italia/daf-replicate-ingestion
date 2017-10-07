package api_calls

import api_calls.Point.GPS
import rest.RestClient

class TrafficQuery(val client: RestClient) extends Query {
  def build(point: GPS, maxDists: Int = -1, maxResults: Int = -1): String = {
    val query = baseUrl + s"format=$format&selection=${point.location}"
    if (maxDists)
  }

  override val serviceUri = "http://www.disit.org/km4city/resource/METRO487"
}
