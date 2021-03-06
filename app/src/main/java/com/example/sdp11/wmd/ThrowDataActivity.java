package com.example.sdp11.wmd;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class ThrowDataActivity extends Activity {
    private final static String TAG = ThrowDataActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_throw_data);

        Bundle data = getIntent().getExtras();
        ThrowData t = data.getParcelable("Throw");

        TextView rowThrowID = (TextView)findViewById(R.id.throw_id_data);
        TextView rowGameID = (TextView)findViewById(R.id.game_id_data);
        TextView rowTotalDistance = (TextView)findViewById(R.id.total_distance_data);
        TextView rowTotalAngle = (TextView)findViewById(R.id.total_angle_data);
        TextView rowSyncTime = (TextView)findViewById(R.id.sync_time_data);

        rowThrowID.setText(String.valueOf(t.getThrowId()));
        rowGameID.setText(String.valueOf(t.getGameId()));
        rowTotalDistance.setText(String.format("%.02f", t.getTotalDistance()));
        rowTotalAngle.setText((t.getTotalAngle() == 1000) ? "N/A" : String.format("%.02f", t.getTotalAngle()));
        rowSyncTime.setText(convertToGPSTime(t.getSyncTime()));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public String convertToGPSTime(double time) {
        //Subtract 4 hours to convert to Eastern time
        //if(gps) time -= 40000;
        String temp = String.valueOf(time);
        if(time < 100000) temp = "0" + temp;
        Log.e(TAG, temp);
        return temp.substring(0,2) + ":" + temp.substring(2,4) + ":" + temp.substring(4);
    }
}
