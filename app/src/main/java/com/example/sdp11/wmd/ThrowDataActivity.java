package com.example.sdp11.wmd;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Student on 3/5/2015.
 */
public class ThrowDataActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_throw_data);

        TableLayout table = (TableLayout)findViewById(R.id.throw_table);
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
//                    return throwData.getThrowIntegrity();
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
