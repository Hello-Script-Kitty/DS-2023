package com.example.ds_2023;

import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

public class MyThread extends Thread{

    private class Message {
         
    }
    File selectedGpx;
    public MyThread(File gpx) {
        this.selectedGpx = selectedGpx;
    }
    @Override
    public void run() {
        Socket s = null;
        try {
            s = new Socket("192.168.56.1",8000);
            s.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}


