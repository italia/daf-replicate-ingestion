package rest

import scalaj.http.{Http, HttpResponse}

/**
  * Gets the data from the url as a String
  */
class RestClientImpl extends RestClient{

  /**
    * Executes a http request to the specified url
    *
    * @param url the url to the resource
    * @return the response as a string
    */
  def pullData(url: String): String = {
    val response: HttpResponse[String] = Http(url).asString
    return response.body
  }

}
