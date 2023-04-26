import java.util.ArrayList;
import java.util.List;

public class Activity {

    private String id;
    private List<Waypoint> waypoints;

    public Activity(String id, List<Waypoint> waypoints) {
        this.id = id;
        this.waypoints = waypoints;
    }

    public String getID() {
        return id;
    }
    public List<Waypoint> getWaypoints() {
        return waypoints;
    }
    public int size() {
        return waypoints.size();
    }

    public String toString() {
        String r = "";
        r += "User ID: " + id + "\n";
        for (int i = 0; i < waypoints.size(); i++) {
            r+= "Waypoint " + i + ": " + waypoints.get(i)+"\n";
        }

        return r;
    }
}
