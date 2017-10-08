package rest

trait RestClient {
  def pullData(url: String): String
}
