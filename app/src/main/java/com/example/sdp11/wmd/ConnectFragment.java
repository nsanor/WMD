package com.example.sdp11.wmd;


import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectFragment extends Fragment{
    private final static String TAG = ConnectFragment.class.getSimpleName();

    private View view;
    private TextView connectionStatus;
    private TextView devicesHeader;
    private LeDeviceListAdapter listAdapter;
    private Button button_search;

    private boolean deviceFound = false;

    private BluetoothAdapter bluetoothAdapter;

    private boolean mConnected = false;

    private static final long SCAN_PERIOD = 1000;

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
        bluetoothAdapter = bluetoothManager.getAdapter();

        //Use logged address to connect to disc automatically
        //BluetoothDevice device =  bluetoothAdapter.getRemoteDevice("");

        connectionStatus = (TextView)view.findViewById(R.id.ConnectionStatus);
        devicesHeader = (TextView)view.findViewById(R.id.DevicesFound);

        button_search = (Button)view.findViewById(R.id.Paired);
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(button_search.getText().equals(getString(R.string.button_search_devices))){
                    if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) scanLeDevice(true);
                    else Toast.makeText(getActivity(), "Please turn Bluetooth on", Toast.LENGTH_SHORT).show();
                }
                else {
                    if(MainActivity.mBluetoothLEService.mConnected){
                        setConnectionStatus(true);
                    }
                    else if(!MainActivity.mBluetoothLEService.mConnected){
                        setConnectionStatus(false);
                    }
                }
            }
        });

        ListView deviceListView = (ListView)view.findViewById(R.id.devices);

        listAdapter = new LeDeviceListAdapter();
        deviceListView.setAdapter(listAdapter);

        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> aView, View v, int pos, long id) {
                try {
                    BluetoothDevice device = listAdapter.getDevice(pos);
                    device.connectGatt(getActivity(), false, MainActivity.mBluetoothLEService.getGattCallback());
                    mConnected = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        MainActivity.getConnectTabReference();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(MainActivity.mBluetoothLEService.mConnected){
            setConnectionStatus(true);
        }
        else if(!MainActivity.mBluetoothLEService.mConnected){
            setConnectionStatus(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        listAdapter.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    public void setConnectionStatus(boolean connected) {
        if(connected) {
            connectionStatus.setText("Device Connected!");
            devicesHeader.setVisibility(View.INVISIBLE);
            button_search.setText(getString(R.string.refresh_devices));
            listAdapter.clear();
            listAdapter.notifyDataSetChanged();
            mConnected = true;
        }
        else {
            connectionStatus.setText("Device Disconnected");
            devicesHeader.setVisibility(View.VISIBLE);
            button_search.setText(getString(R.string.button_search_devices));
            listAdapter.clear();
            listAdapter.notifyDataSetChanged();
            mConnected = false;
        }
    }


    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            Handler mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bluetoothAdapter.stopLeScan(mScanCallback);
                    if(!deviceFound) {
                        listAdapter.clear();
                        listAdapter.notifyDataSetChanged();
                    }
                }
            }, SCAN_PERIOD);
            bluetoothAdapter.startLeScan(mScanCallback);
        } else {
            bluetoothAdapter.stopLeScan(mScanCallback);
        }

    }

    private BluetoothAdapter.LeScanCallback mScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    deviceFound = true;
                    devicesHeader.setVisibility(view.getVisibility());
                    listAdapter.addDevice(device);
                    listAdapter.notifyDataSetChanged();
                }
            });
        }
    };


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
                viewHolder.deviceName.setText(getString(R.string.unknown_device));
            viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }
    }
}
