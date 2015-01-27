package com.example.sdp11.wmd;


import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class DataFragment extends Fragment {

    View view;

    public DataFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_data, container, false);

        ThrowsDataSource dataSource = new ThrowsDataSource(getActivity());
        dataSource.createThrow(1, 1, 1, 1, 1, 1);
        dataSource.createThrow(1, 1, 1, 1, 1, 1);
        dataSource.createThrow(1, 1, 1, 1, 1, 1);

        List<Throw> values = dataSource.getAllComments();

        ArrayAdapter<Throw> adapter = new ArrayAdapter<Throw>(this,
                R.layout.fragment_data, values);
        setListAdapter(adapter);
        return view;
    }

    public void addData(View view, TableLayout tl){
        TextView textView = new TextView(getActivity());
        //textView.setText(Double.toString(location.getLatitude()));

        TableRow row = new TableRow(getActivity());
        row.addView(textView);
        tl.addView(row);
    }


}
