package com.example.ds_2023;

import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;



public class MyThread extends Thread{

    private final String ACTIVITY_TAG = "Thread";

    private class Message {
         
    }
    private String gpxData;
    public MyThread(String gpxData) {
        this.gpxData = gpxData;
    }
    @Override
    public void run() {

        Socket s = null;
        try {
            Log.d(ACTIVITY_TAG, gpxData);

            s = new Socket("192.168.56.1",8000);

            DataOutputStream output = new DataOutputStream(s.getOutputStream());
            output.flush();

            DataInputStream input = new DataInputStream(s.getInputStream());
            output.write(serialize(gpxData));
            s.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] serialize(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(o);
        }
        return baos.toByteArray();
    }
}


