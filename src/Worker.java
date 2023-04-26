import java.io.IOException;
import java.net.Socket;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Worker {

    private class Workload{

        private String id;
        private double totDistance;
        private double avgSpeed;
        private double totElevation;
        private double totTime;

        public Workload(String id) {
            this.id = id;
            totDistance = 0;
            avgSpeed = 0;
            totElevation = 0;
            totTime = 0;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public double getTotDistance() {
            return totDistance;
        }

        public void setTotDistance(double totDistance) {
            this.totDistance = totDistance;
        }

        public double getAvgSpeed() {
            return avgSpeed;
        }

        public void setAvgSpeed(double avgSpeed) {
            this.avgSpeed = avgSpeed;
        }

        public double getTotElevation() {
            return totElevation;
        }

        public void setTotElevation(double totElevation) {
            this.totElevation = totElevation;
        }

        public double getTotTime() {
            return totTime;
        }

        public void setTotTime(double totTime) {
            this.totTime = totTime;
        }

        public String toString() {
            return "Activity ID: " + id + "    Total Distance: " + totDistance + "    Total Elevation: " + totElevation + "     Total Time: " + totTime + "     Average Speed: " + avgSpeed;
        }
    }

    public static void main(String[] args) throws  IOException{

        Socket workerSocket = new Socket("localhost",8080);
        System.out.println("[CLIENT] Connection established.");
    }

    public Workload process(Activity chunk) {

        Workload workload = new Workload(chunk.getID());
        List<Waypoint> waypoints = chunk.getWaypoints();

        for (int i = 0; i < chunk.size() - 1; i++) {
            double distance = calculateDistance(waypoints.get(i), waypoints.get(i+1));
            double time = calculateTime(waypoints.get(i), waypoints.get(i+1));
            double elevation = calculateElevation(waypoints.get(i),waypoints.get(i+1));
            workload.setTotDistance(workload.getTotDistance() + distance);
            workload.setTotTime(workload.getTotTime()+time);
            workload.setTotElevation(workload.getTotElevation()+elevation);
        }
        workload.setAvgSpeed(workload.getTotDistance() / workload.getTotTime());
        System.out.println(workload);
        return workload;
    }

    private double calculateDistance(Waypoint w1, Waypoint w2) {
        int radius = 6371; //radius of earth in km

        double dLat = Math.toRadians(w2.getLatitude() - w1.getLatitude());
        double dLon = Math.toRadians(w2.getLongitude() - w1.getLongitude());

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(w1.getLatitude())) * Math.cos(Math.toRadians(w2.getLatitude())) * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = radius*c;
        return distance * 1000;
    }

    private double calculateTime(Waypoint w1, Waypoint w2) {
        return ChronoUnit.SECONDS.between(w1.getTime(),w2.getTime());
    }

    private double calculateElevation(Waypoint w1, Waypoint w2) {
        if (w2.getElevation() > w1.getElevation()) {
            return w2.getElevation() - w1.getElevation();
        } else {
            return 0;
        }
    }
}
