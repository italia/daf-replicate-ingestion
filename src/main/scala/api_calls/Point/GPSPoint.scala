package api_calls.Point

class GPSPoint(lat: Int, long: Int) extends StartingPoint{
  override val location = s"$lat;$long"
}
