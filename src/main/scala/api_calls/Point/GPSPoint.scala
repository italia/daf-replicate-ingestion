package api_calls.Point

class GPSPoint(lat: Double, long: Double) extends StartingPoint{
  override val location = s"$lat;$long"
}
