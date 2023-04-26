
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    //private final int PORT = 8080;
    //private ServerSocket serverSocket;

    private ArrayList<WorkerHandler> workers;
    private ArrayList<UserHandler> users;
    private final int numOfWorkers = 3;

    public static void main(String[] args) throws IOException {

        ServerSocket workerServerSocket = new ServerSocket(8080);
        ServerSocket userServerSocket = new ServerSocket(8081);
        System.out.println("[SERVER] Waiting for connection.");

        //workerServerSocket.accept();
        //System.out.println("[SERVER] Worker connected.");

        Socket userSocket = userServerSocket.accept();
        System.out.println("[SERVER] User connected.");

        InputStream is = userSocket.getInputStream();
        FileOutputStream fos = new FileOutputStream("src/gpxs/test.gpx");

        byte[] buffer = new byte[1024];
        int bytesRead;

        while((bytesRead = is.read(buffer)) != -1) {
            fos.write(buffer,0,bytesRead);
        }

        System.out.println("[SERVER] Transfer complete.");

        XMLParser parser = new XMLParser();
        Activity ac = parser.parse(new File("src/gpxs/test.gpx"));






        Server s = new Server();
        s.map(ac);

        Worker w = new Worker();
        Activity[] chunks = s.map(ac);
        w.process(chunks[0]);

        //System.out.println(ac);
        //System.out.println(55/4);
        fos.close();
        is.close();
        userSocket.close();
        userServerSocket.close();


    }

    public Activity[] map(Activity activity) {
        //split
        Activity[] chunks = new Activity[numOfWorkers];
        int counter = ((activity.size() + numOfWorkers - 1) / numOfWorkers)+1;

        for (int i = 0; i<numOfWorkers; i++) {


            if (i==0) {
                chunks[0] = new Activity(activity.getID(), activity.getWaypoints().subList(0,counter));
            } else if (i<numOfWorkers-1) {
                chunks[i] = new Activity(activity.getID(), activity.getWaypoints().subList((i*counter)-i,(i+1)*counter - i));
            } else {
                chunks[numOfWorkers-1] = new Activity(activity.getID(), activity.getWaypoints().subList((i*counter)-i, activity.size()));
            }
        }

        for(int i = 0; i < chunks.length; i++) {
            System.out.println(chunks[i]);
        }
        return chunks;
        //map
    }
}
//17  0-6(7)/6-12(7)/12-17(6)
//38 0-12(14)/12-24(13)/24-36(13)

