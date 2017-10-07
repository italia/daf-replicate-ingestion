package scala

import api_calls.Point.GPSPoint
import api_calls.ServicesQuery
import org.scalatest.{FlatSpec, Matchers}

class TestServicesQuery extends FlatSpec with Matchers{

  "A Service query" should "produce a well formatted query when passed input params" in{
    val servicesQuery = new ServicesQuery()
    val query = servicesQuery.build(new GPSPoint(0,0), 2, 1)
    query shouldEqual "http://servicemap.disit.org/WebAppGrafo/api/v1/?format=json&selection=0;0&maxDists=2&maxResults=1"
  }

  it should "produce a well formatted query when not passed input params" in {
    val servicesQuery = new ServicesQuery()
    val query = servicesQuery.build(new GPSPoint(0,0))
    query shouldEqual "http://servicemap.disit.org/WebAppGrafo/api/v1/?format=json&selection=0;0"
  }

}
