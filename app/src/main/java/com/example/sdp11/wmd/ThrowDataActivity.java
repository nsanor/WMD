package com.example.sdp11.wmd;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.sql.Timestamp;

/**
 * Created by Student on 3/5/2015.
 */
public class ThrowDataActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_throw_data);

        Bundle data = getIntent().getExtras();
        ThrowData t = (ThrowData) data.getParcelable("Throw");

        TextView rowThrowID = (TextView)findViewById(R.id.throw_id_data);
        TextView rowGameID = (TextView)findViewById(R.id.game_id_data);
        TextView rowInitialDirection = (TextView)findViewById(R.id.initial_direction_data);
        TextView rowFinalDirection = (TextView)findViewById(R.id.final_direction_data);
        TextView rowThrowQuality = (TextView)findViewById(R.id.throw_integrity_data);
        TextView rowTotalDistance = (TextView)findViewById(R.id.total_distance_data);
        TextView rowTotalTime = (TextView)findViewById(R.id.total_time_data);
        TextView rowSyncTime = (TextView)findViewById(R.id.sync_time_data);

        rowThrowID.setText(String.valueOf(t.getThrowId()));
        rowGameID.setText(String.valueOf(t.getGameId()));
        rowInitialDirection.setText(String.valueOf(t.getInitialDirection()));
        rowFinalDirection.setText(String.valueOf(t.getFinalDirection()));
        rowThrowQuality.setText(String.valueOf(t.getThrowQuality()));
        rowTotalDistance.setText(String.valueOf(t.getTotalDistance()));
        rowTotalTime.setText(String.valueOf(new Timestamp(t.getTotalTime()*1000)));
        rowSyncTime.setText(String.valueOf(new Timestamp(t.getSyncTime()*1000)));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

//    public class GridAdapter extends BaseAdapter {
//        private Context context;
//        private ThrowData throwData;
//
//        public GridAdapter(Context c, ThrowData t) {
//            context = c;
//            throwData = t;
//        }
//
//        @Override
//        public int getCount() {
//            return 9;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            switch(position){
//                case 0:
//                    return throwData.getThrowId();
//                case 1:
//                    return throwData.getHoleId();
//                case 2:
//                    return throwData.getGameId();
//                case 3:
//                    return throwData.getInitialDirection();
//                case 4:
//                    return throwData.getFinalDirection();
//                case 5:
//                    return throwData.getThrowQuality();
//                case 6:
//                    return throwData.getTotalDistance();
//                case 7:
//                    return throwData.getTotalTime();
//                case 8:
//                    return throwData.getSyncTime();
//                default:
//                    return null;
//            }
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return 0;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            TextView textView;
//            if(convertView == null) {
//                textView = new TextView(context);
//                textView.setText(String.valueOf(throwData.getThrowId()));
//            }
//        }
//    }
}
