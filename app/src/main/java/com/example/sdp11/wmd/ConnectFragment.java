package com.example.sdp11.wmd;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectFragment extends Fragment{

    private Button on, off, search, paired;
    View view;

    private BluetoothAdapter BA;
    private ListView lv;
    private ArrayAdapter listAdapter;
    private List values;

    private boolean mScanning;
    private Handler mHandler;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    public ConnectFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_connect, container, false);
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        BA = bluetoothManager.getAdapter();

        on = (Button)view.findViewById(R.id.Toggle);
        on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle(view);
            }
        });

        paired = (Button)view.findViewById(R.id.Paired);
        paired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(BA.getState() == BluetoothAdapter.STATE_ON) scanLeDevice(true);
            }
        });

        lv = (ListView)view.findViewById(R.id.devices);

        //BA.startLeScan(mScanCallback);

        listAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, 0);
        lv.setAdapter(listAdapter);

        return view;
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    BA.stopLeScan(mScanCallback);
                    Log.e("", "Stop Scanning");
                }
            }, SCAN_PERIOD);

            Log.e("", "Now Scanning");
            mScanning = true;
            BA.startLeScan(mScanCallback);
        } else {
            mScanning = false;
            BA.stopLeScan(mScanCallback);
        }

    }

    private BluetoothAdapter.LeScanCallback mScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listAdapter.add(device.getName());
                    listAdapter.notifyDataSetChanged();
                    Log.e("", device.getName());
                }
            });
        }
    };

//    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
//        @Override
//        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
//            //Connection established
//            if (status == BluetoothGatt.GATT_SUCCESS
//                    && newState == BluetoothProfile.STATE_CONNECTED) {
//
//                //Discover services
//                gatt.discoverServices();
//
//            } else if (status == BluetoothGatt.GATT_SUCCESS
//                    && newState == BluetoothProfile.STATE_DISCONNECTED) {
//
//                //Handle a disconnect event
//
//            }
//        }
//
//        @Override
//        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
//            if (status == BluetoothGatt.GATT_SUCCESS) {
//
//                Log.i("ConnectFragment", "Connected to: " + gatt.getDevice());
//            }
//        }
//    };




    public void toggle(View view){
        if(!BA.isEnabled()){
            //Intent TurnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //startActivityForResult(TurnOn, 0);
            BA.enable();
            while (BA.getState() != BluetoothAdapter.STATE_ON);
            Toast.makeText(getActivity(), "Bluetooth is now on!", Toast.LENGTH_SHORT).show();
        }
        else {
            //Intent TurnOn = new Intent(BluetoothAdapter.ACTION_);
            //startActivityForResult(TurnOn, 0);
            BA.disable();
            while (BA.getState() != BluetoothAdapter.STATE_OFF);
            Toast.makeText(getActivity(), "Bluetooth is now off!", Toast.LENGTH_SHORT).show();
        }
    }
}
