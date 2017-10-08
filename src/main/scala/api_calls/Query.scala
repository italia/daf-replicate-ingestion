package api_calls

trait Query {

  val format: String = "json"
  val baseUrl : String = "http://servicemap.disit.org/WebAppGrafo/api/v1/?"
  val serviceUri: String

}
