package api_calls

trait Query {

  val format: String = "json"
  val baseUrl : String = "http://servicemap.km4city.org/WebAppGrafo/"
  val serviceUri: String

}
