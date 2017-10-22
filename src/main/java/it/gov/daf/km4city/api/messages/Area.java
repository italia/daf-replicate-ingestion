package it.gov.daf.km4city.api.messages;

/**
 * immutable object, starting message for ApiLocation actor
 */
public class Area {
    private double lat1;
    private double long1;
    private double lat2;
    private double long2;

    public Area(double lat1, double long1, double lat2, double long2) {
        this.lat1 = lat1;
        this.long1 = long1;
        this.lat2 = lat2;
        this.long2 = long2;
    }

    public double getLat1() {
        return lat1;
    }

    public double getLong1() {
        return long1;
    }

    public double getLat2() {
        return lat2;
    }

    public double getLong2() {
        return long2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Area area = (Area) o;

        if (Double.compare(area.lat1, lat1) != 0) return false;
        if (Double.compare(area.long1, long1) != 0) return false;
        if (Double.compare(area.lat2, lat2) != 0) return false;
        return Double.compare(area.long2, long2) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(lat1);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(long1);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lat2);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(long2);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Area{" +
                "lat1=" + lat1 +
                ", long1=" + long1 +
                ", lat2=" + lat2 +
                ", long2=" + long2 +
                '}';
    }
}
