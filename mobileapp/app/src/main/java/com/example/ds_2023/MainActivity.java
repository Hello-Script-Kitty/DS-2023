package com.example.ds_2023;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_XML = 1;

    private final String ACTIVITY_TAG = "Main Activity";

    Button buttonPick;
    Button buttonSend;
    TextView label;
    Uri selectedGpx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonPick = (Button) findViewById(R.id.button_pick);
        label = (TextView) findViewById(R.id.label);

        buttonPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                intent.setType("*/*");

                //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

                startActivityForResult(intent,PICK_XML);


            }
        });

        buttonSend = (Button) findViewById(R.id.button_send);

        buttonSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                label.setText(selectedGpx.getPath());
                File gpx = new File(selectedGpx.getPath());

                MyThread thread = new MyThread(gpx);
                thread.start();


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_XML) {
            selectedGpx = data.getData();


        }
    }



}