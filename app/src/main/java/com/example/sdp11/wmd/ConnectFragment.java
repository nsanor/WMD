package com.example.sdp11.wmd;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectFragment extends Fragment{
    private final static String TAG = ConnectFragment.class.getSimpleName();

    private Button button_toggle, button_search;
    View view;

    private boolean mConnected = false;

    private BluetoothLEService mBluetoothLEService;

    private String mDeviceName;
    private String mDeviceAddress;

    private BluetoothAdapter BA;
    private ListView lv;
    //private ArrayAdapter listAdapter;
    private LeDeviceListAdapter listAdapter;
    private List values;

    private BluetoothGatt mBluetoothGatt;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics;

    private boolean mScanning;
    private Handler mHandler;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 1000;

//    private int mConnectionState = STATE_DISCONNECTED;
//
//    private static final int STATE_DISCONNECTED = 0;
//    private static final int STATE_CONNECTING = 1;
//    private static final int STATE_CONNECTED = 2;
//
//    private final String LIST_NAME = "NAME";
//    private final String LIST_UUID = "UUID";


    public ConnectFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_connect, container, false);
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        BA = bluetoothManager.getAdapter();

        button_toggle = (Button)view.findViewById(R.id.Toggle);
        button_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle(view);
            }
        });

        button_search = (Button)view.findViewById(R.id.Paired);
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BA.getState() == BluetoothAdapter.STATE_ON) scanLeDevice(true);
            }
        });

        lv = (ListView)view.findViewById(R.id.devices);

        listAdapter = new LeDeviceListAdapter();//ArrayList<String>(getActivity(),android.R.layout.simple_list_item_1, 0);
        lv.setAdapter(listAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> aView, View v, int position, long id) {
                //Log.e(String.valueOf(position), "Connect here.");
                BluetoothDevice device = listAdapter.getDevice(position);
                mBluetoothGatt = device.connectGatt(getActivity(), false, mBluetoothLEService.getGattCallback());
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
//        if (mBluetoothLEService != null) {
//            final boolean result = mBluetoothLEService.connect(mDeviceAddress);
//            Log.d(TAG, "Connect request result=" + result);
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //getActivity().unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //getActivity().unbindService(mServiceConnection);
        //mBluetoothLEService = null;
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
                    //Log.e("", "Stop Scanning");
                }
            }, SCAN_PERIOD);

            //Log.e("", "Now Scanning");
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
                    //listAdapter.add(device.getName() + ": " + device.getAddress());
                    listAdapter.addDevice(device);
                    listAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    public void toggle(View view){
        if(!BA.isEnabled()){
            //Intent TurnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //startActivityForResult(TurnOn, 0);
            BA.enable();
            while (BA.getState() != BluetoothAdapter.STATE_ON);
            Toast.makeText(getActivity(), "Bluetooth is now button_toggle!", Toast.LENGTH_SHORT).show();
        }
        else {
            //Intent TurnOn = new Intent(BluetoothAdapter.ACTION_);
            //startActivityForResult(TurnOn, 0);
            BA.disable();
            while (BA.getState() != BluetoothAdapter.STATE_OFF);
            Toast.makeText(getActivity(), "Bluetooth is now off!", Toast.LENGTH_SHORT).show();
        }
    }

    static class ViewHolder{
        TextView deviceAddress;
        TextView deviceName;
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = getActivity().getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }
    }
}
