import java.io.*;
import java.net.Socket;

public class User {



    public static void main(String[] args) throws IOException{


        Socket userSocket = new Socket("localhost",8081);
        System.out.println("[USER] Connection established.");

        //PrintWriter out = new PrintWriter(userSocket.getOutputStream(),true);
        FileInputStream fis = new FileInputStream("src/gpxs/route1.gpx");
        OutputStream os = userSocket.getOutputStream();

        byte[] buffer = new byte[1024];
        int bytesRead;

        while((bytesRead = fis.read(buffer)) != -1) {
            os.write(buffer,0, bytesRead);
        }

        os.flush();

        fis.close();
        userSocket.close();

       //System.out.println("[USER] Transfer complete.");
    }
}

