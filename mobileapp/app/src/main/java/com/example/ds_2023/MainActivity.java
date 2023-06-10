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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_XML = 1;

    private final String ACTIVITY_TAG = "Main Activity";

    Button buttonPick;
    Button buttonSend;
    TextView label;
    Uri selectedGpx;

    String gpxData;

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

                try {
                    gpxData = readTextFromUri(selectedGpx);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                Log.d(ACTIVITY_TAG, gpxData);
                MyThread thread = new MyThread(gpxData);

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

    private String readTextFromUri(Uri uri) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }
}