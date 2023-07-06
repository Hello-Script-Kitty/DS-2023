package org.opa.ds23.ds_2023;

import android.os.Handler;
import android.util.Log;

import org.opa.ds23.gpxr.common.messaging.ActivityMsg;
import org.opa.ds23.gpxr.common.messaging.Message;
import org.opa.ds23.gpxr.common.messaging.Type;
import org.opa.ds23.gpxr.common.net.Connection;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MyThread extends Thread{

    private final String ACTIVITY_TAG = "Thread";
    private String gpxData;
    private Handler handler;
    static final ExecutorService es = Executors.newCachedThreadPool();
    public MyThread(String gpxData, Handler handler) {
        this.gpxData = gpxData;
        this.handler = handler;
    }

    @Override
    public void run() {

        try (Connection connection = new Connection("192.168.56.1", 8000, null, null)) {
            //logger.debug("Sending " + f.getName());
            Log.d(ACTIVITY_TAG, gpxData);
            es.execute(connection);
            ActivityMsg act = new ActivityMsg();
            act.gpxContent = gpxData;
            Message msg = new Message(Type.Activity, act.serialize());
            connection.send(msg.serialize());
            //logger.debug("Done sending file " + f.getName());
            Log.d(ACTIVITY_TAG, "Done sending file");

        } catch (IOException | InterruptedException e) {
            //logger.error("An error occured while sending " + f.getName());
            Log.d(ACTIVITY_TAG, "An error occured while sending.");
            //logger.error(Exceptions.getStackTrace(e));

        }
    }
}


