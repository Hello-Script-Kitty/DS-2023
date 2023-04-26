import java.time.LocalDateTime;

public class Waypoint {

    private double latitude;
    private double longitude;
    private float elevation;
    private LocalDateTime time;

    public Waypoint(double latitude, double longitude, float elevation, LocalDateTime time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;
        this.time = time;
    }
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getElevation() {
        return elevation;
    }

    public void setElevation(float elevation) {
        this.elevation = elevation;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String toString() {
        return "Lat: " + latitude + " Lon: " + longitude + " Ele: " +elevation+" Time: " +time;
    }
}
