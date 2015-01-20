package com.example.sdp11.wmd;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


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
        TableLayout tl = (TableLayout) view.findViewById(R.id.DataTable);
        addData(view, tl);
        return view;
    }

    public void addData(View view, TableLayout tl){
        TextView textView = new TextView(getActivity());
        textView.setText("I'm in the table");

        TableRow row = new TableRow(getActivity());
        row.addView(textView);
        tl.addView(row);
    }


}
