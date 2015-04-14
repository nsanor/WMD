package com.example.sdp11.wmd;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class LogActivity extends Activity implements AdapterView.OnItemSelectedListener {
    private final static String TAG = LogActivity.class.getSimpleName();
    private TextView logText;
    private String filename = "my_log.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        Log.e(TAG, "in onCreate");

        logText = (TextView)findViewById(R.id.log_text);
        logText.setMovementMethod(new ScrollingMovementMethod());
        logText.setText("");

        Spinner logChoice = (Spinner)findViewById(R.id.log_choice);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.log_item, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        logChoice.setAdapter(adapter);
        logChoice.setOnItemSelectedListener(this);

        refreshText();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.clear_log) {
            clearLog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void clearLog() {
        FileOutputStream outputStream;
        String text = "";

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(text.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        logText.setText("");
    }


    private void refreshText() {
        logText.setText("");
        try {
            InputStream inputStream = openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                    stringBuilder.append("\n\n");
                }

                inputStream.close();
                logText.setText(stringBuilder.toString() + "\n");
            }
        }
        catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "Can not read file: " + e.toString());
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(adapterView.getItemAtPosition(i).equals("Bluetooth Log")) filename = "my_log.txt";
        else filename = "transferred_points.txt";
        refreshText();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
