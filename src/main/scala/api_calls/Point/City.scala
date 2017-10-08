package api_calls.Point

class City(private val city: String) extends StartingPoint{
  override val location: String = city.replace(" ", "%20")
}
