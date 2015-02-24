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

    private View view;
    private ThrowsDataSource dataSource;
    private ArrayAdapter adapter;

    public DataFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataSource = MainActivity.dataSource;

        //Test
        dataSource.deleteAllThrows();
        for (int i = 0; i < 25; i++) dataSource.createThrow(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_data, container, false);

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
