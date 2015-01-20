package com.example.sdp11.wmd;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class SetupFragment extends Fragment{

    private Button on, off, search, paired;
    View view;

    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    private ListView lv;


    public SetupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_setup, container, false);

        on = (Button)view.findViewById(R.id.TurnOn);
        on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                on(view);
            }
        });

        off = (Button)view.findViewById(R.id.TurnOff);
        off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                off(view);
            }
        });

        search = (Button)view.findViewById(R.id.Search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                visible(view);
            }
        });

        paired = (Button)view.findViewById(R.id.Paired);
        paired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list(view);
            }
        });

        lv = (ListView)view.findViewById(R.id.devices);
        BA = BluetoothAdapter.getDefaultAdapter();

        return view;
    }

    public void on(View view){
        if(!BA.isEnabled()){
            Intent TurnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(TurnOn, 0);
            Toast.makeText(getActivity(), "Bluetooth On!", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getActivity(), "Bluetooth is already on!", Toast.LENGTH_SHORT).show();
        }
    }

    public void off(View view){
        BA.disable();
        Toast.makeText(getActivity(), "Bluetooth Off!", Toast.LENGTH_SHORT).show();
    }

    public void list(View view){
        pairedDevices = BA.getBondedDevices();
        ArrayList list = new ArrayList();

        for(BluetoothDevice bt : pairedDevices)
            list.add(bt.getName());

        Toast.makeText(getActivity(),"Showing Paired Devices",
                Toast.LENGTH_SHORT).show();
        final ArrayAdapter adapter = new ArrayAdapter
                (getActivity(),android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);
    }

    public void visible(View view){
        Intent getVisible = new Intent(BluetoothAdapter.
                ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible, 0);

    }
}
