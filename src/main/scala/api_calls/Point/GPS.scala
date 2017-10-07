package api_calls.Point

class GPS(lat: Int, long: Int) extends StartingPoint{
  override val location = s"<$lat>;<$long>"
}
