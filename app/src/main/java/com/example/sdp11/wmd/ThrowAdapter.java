package com.example.sdp11.wmd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nsanor on 2/10/2015.
 */
public class ThrowAdapter extends ArrayAdapter<ThrowData> {
    private ArrayList<ThrowData> items;
    private Context context;

    public ThrowAdapter(Context context, int textViewResourceId, ArrayList<ThrowData> items) {
        super(context, textViewResourceId, items);
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.row, null);
        }
        ThrowData ctd = items.get(position);
        if (ctd != null) {
            TextView id = (TextView) v.findViewById(R.id.throw_id);
            TextView dist = (TextView) v.findViewById(R.id.total_distance);
            TextView tintegrity = (TextView) v.findViewById(R.id.throw_integrity);
            if (id != null) {
                id.setText(String.valueOf(ctd.getThrowId()));
            }
            if(dist != null){
                dist.setText(String.valueOf(ctd.getTotalDistance()));
            }
            if(tintegrity != null){
                dist.setText(String.valueOf(ctd.getThrowIntegrity()));
            }
        }
        return v;
    }

}
