package com.example.sdp11.wmd;


import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;


public class DataFragment extends Fragment {

    View view;
    ThrowsDataSource dataSource;

    public DataFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_data, container, false);

        dataSource = new ThrowsDataSource(getActivity());
        dataSource.open();
        dataSource.createThrow(1, 1, 1, 1, 1, 1, 1, 1);
//        dataSource.createThrow(1, 1, 1, 1, 1, 1);
//        dataSource.createThrow(1, 1, 1, 1, 1, 1);

        ListView lis = (ListView)v.findViewById(R.id.DataPoints);

        List<Throw> values = dataSource.getAllThrows();

        ArrayAdapter<Throw> adapter = new ArrayAdapter<Throw>(getActivity(),
                R.layout.fragment_data, values);
        lis.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        dataSource.open();
        super.onResume();
    }

    @Override
    public void onPause() {
        dataSource.close();
        super.onPause();
    }

}
