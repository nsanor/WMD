package com.example.sdp11.wmd;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;


public class DataFragment extends Fragment {

    View view;
    ThrowsDataSource dataSource;
    ArrayAdapter adapter;

    public DataFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_data, container, false);

        dataSource = new ThrowsDataSource(getActivity());
        dataSource.open();
        dataSource.deleteAllThrows();
        dataSource.createThrow(1, 2, 3, 1, 1, 1, 1, 1);
//        dataSource.createThrow(4, 5, 6, 1, 1, 1, 1, 1);
//        dataSource.createThrow(7, 8, 9, 1, 1, 1, 1, 1);

        ListView lis = (ListView)view.findViewById(R.id.list);

        List<RawThrowData> values = dataSource.getAllThrows();

        adapter = new ArrayAdapter<RawThrowData>(getActivity(),
                android.R.layout.simple_list_item_1, values);
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
