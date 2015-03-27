package com.example.sdp11.wmd;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import android.os.Handler;

/**
 * Created by nsanor on 2/20/2015.
 */
public class BluetoothLEService extends Service {
    private final static String TAG = BluetoothLEService.class.getSimpleName();

    // UUIDs for UAT service and associated characteristics.
    public static UUID UART_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    public static UUID TX_UUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    public static UUID RX_UUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
    // UUID for the BTLE client characteristic which is necessary for notifications.
    public static UUID CLIENT_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;

    private BluetoothGattCharacteristic tx;
    private BluetoothGattCharacteristic rx;

    private String mBluetoothDeviceAddress;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.sdp11.wmd.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.sdp11.wmd.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.sdp11.wmd.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.sdp11.wmd.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.sdp11.wmd.EXTRA_DATA";

    private static final String Separator = System.getProperty("line.separator");
    private String transferredFilename = "transferred_points.txt";
    private String logFilename = "my_log.txt";
    private long lastSyncTime = 0;

    private ArrayList<GPSDataPoint> gpsData;

    private boolean isTransferring = false;
    private ArrayList<String> inputStrings;
    private String inputString = "";

    private boolean isGPS;

    public BluetoothGattCallback getGattCallback() {return mGattCallback;}
    public String getmBluetoothDeviceAddress() {
        return mBluetoothDeviceAddress;
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            //Connection established
            if (status == BluetoothGatt.GATT_SUCCESS
                    && newState == BluetoothProfile.STATE_CONNECTED) {
                mBluetoothGatt  = gatt;
                broadcastUpdate(ACTION_GATT_CONNECTED);
                writeToLog("Bluetooth Connected.");
                //Discover services
                gatt.discoverServices();

            } else if (status == BluetoothGatt.GATT_SUCCESS
                    && newState == BluetoothProfile.STATE_DISCONNECTED) {
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
                writeToLog("Bluetooth Disconnected.");
                //Handle a disconnect event
            }

            else {
                Log.i(TAG, "Connection state changed.  New state: " + newState);
            }
        }

        @Override
        // New services discovered
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                writeToLog("Services Discovered.");
            } else {
                Log.i(TAG, "Error, onServicesDiscovered received status: " + status);
            }

            // Save reference to each characteristic.
            tx = gatt.getService(UART_UUID).getCharacteristic(TX_UUID);
            rx = gatt.getService(UART_UUID).getCharacteristic(RX_UUID);
            // Setup notifications on RX characteristic changes (i.e. data received).
            // First call setCharacteristicNotification to enable notification.
            if (!gatt.setCharacteristicNotification(rx, true)) {
                Log.i(TAG, "Couldn't set notifications for RX characteristic!");
            }
            // Next update the RX characteristic's client descriptor to enable notifications.
            if (rx.getDescriptor(CLIENT_UUID) != null) {
                BluetoothGattDescriptor desc = rx.getDescriptor(CLIENT_UUID);
                desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                if (!gatt.writeDescriptor(desc)) {
                    Log.i(TAG, "Couldn't write RX client descriptor value!");
                }
            }
            else {
                Log.i(TAG, "Couldn't get RX client descriptor!");
            }
        }


        @Override
        // Result of a characteristic read operation
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
//                if(!isTransferring) {
//                    Log.e(TAG, "in handler");
//                    isTransferring = true;
//                    Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            processData();
//                            isTransferring = false;
//                        }
//                    }, 10000);
//                }

                String data = characteristic.getStringValue(0);
                Log.e(TAG, "onCharacteristicRead: " + data);
//                writeToLog("Characteristic Changed.");
//                writeToLog("Transferred Data: " + data);
//                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
//                if((System.currentTimeMillis() - lastSyncTime) > 10000) TotalsData.updateThrowCount();
//                lastSyncTime = System.currentTimeMillis();
//                parseTransferredData(data);
            }
        }

        // Called when a remote characteristic changes (like the RX characteristic).
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

//            if(!isTransferring) {
//                isTransferring = true;
//
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        processData();
//                        Log.e(TAG, "in handler");
//                        isTransferring = false;
//                    }
//                }, 10000);
//            }

            String data = characteristic.getStringValue(0);
            Log.e(TAG, "onCharacteristicChanged: " + data);
            writeToLog("Transferred Data: " + data);
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            if((System.currentTimeMillis() - lastSyncTime) > 10000) TotalsData.updateThrowCount();
            lastSyncTime = System.currentTimeMillis();
            parseTransferredData(data);
        }

        private void broadcastUpdate(final String action) {
            final Intent i = new Intent(action);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            BluetoothLEService.this.sendBroadcast(i);
        }

        private void broadcastUpdate(final String action,
                                     final BluetoothGattCharacteristic characteristic) {
            final Intent intent = new Intent(action);

            //writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" +
                        stringBuilder.toString());
            }
            sendBroadcast(intent);
        }



    };

    private void processData() {
        double initialDirection = calculateInitialDirection();
        double finalDirection = calculateFinalDirection();

    }

    private double calculateInitialDirection(){
        return -1;
    }

    private double calculateFinalDirection(){
        return -1;
    }

    //Cycle through all transferred GPS and IMU data
    public void parseTransferredData(String input) {

    //Test
//        String data[] = {"$GPRMC,180338.600,A,4104.5010,N,08130.6533,W,2.67,356.61,190215,,,A*7D\n",
//                "$GPRMC,180338.800,A,4104.5012,N,08130.6533,W,2.55,358.37,190215,,,A*7D\n",
//                "$GPRMC,180339.000,A,4104.5013,N,08130.6533,W,2.80,356.43,190215,,,A*70\n",
//                "$GPRMC,180339.200,A,4104.5014,N,08130.6533,W,2.39,353.28,190215,,,A*7F\n",
//                "$GPRMC,180339.400,A,4104.5016,N,08130.6533,W,2.67,352.87,190215,,,A*74\n",
//                "$GPRMC,180339.600,A,4104.5017,N,08130.6532,W,2.82,358.80,190215,,,A*70\n"};
    if(input.startsWith("$GPRMC") || isGPS) {
        isGPS = true;
        //gpsData.add(parseGPS(s));
        combineStrings(input);
    }
    else {
        Log.i(TAG, "Implement IMU parser here");
    }
    //if(gps != null) GPSCoordinates.add(gps); //Create throw when we get sample data from IMU
    //dataSource.createThrow();
    }

    private void combineStrings(String input) {
        //Log.e(TAG, "Input String at beginning: " + inputString);

        if(input.contains("\n")) {
            //Log.e(TAG, "newline present");
            String data[] = input.split("\n");
            //for(String i: data) Log.e(TAG, i);
            //Start of a new line
            if(data.length > 1) {
                //Log.e(TAG, "New line, needs split");
                inputString += data[0].trim();
                parseGPS(inputString);
                inputString = data[1].trim();
            }

            //else Log.e(TAG, "Error splitting");
        }
        else {
            inputString += input.trim();
            //Log.e(TAG, "No newline present");
            //If there are two characters after the star (full string)
            if(inputString.contains("*") && (inputString.length() - inputString.indexOf("*") - 1) >= 2){//if(inputString.length() - inputString.replace(",", "").length() >= 12) {
                //Log.e(TAG, "Full string");
                parseGPS(inputString);
                inputString = "";
            }

        }
        //Log.e(TAG, "Input String at end: " + inputString);
    }

    private void parseGPS(String i) {
        double latDeg, latMin, latitude, lonDeg, lonMin, longitude;
        double time;
        String input[] = i.split(",");

        //Figure out time!!
        if (input.length >= 7) {
            time = Double.parseDouble(input[1]);
            latDeg =Double.parseDouble(input[3].substring(0, 2));
            latMin =Double.parseDouble(input[3].substring(2));
            latitude = latDeg + (latMin / 60);
            if (input[4].equals(String.valueOf("S"))) latitude = -1 * latitude;
            lonDeg =Double.parseDouble(input[5].substring(0, 3));
            lonMin =Double.parseDouble(input[5].substring(3));
            longitude = (lonDeg + (lonMin / 60)) * -1;
            if (input[6].equals(String.valueOf("E"))) longitude = -1 * longitude;
            //return new inputDataPoint(latitude, longitude, 1);
            writeTransferredPoints(latitude + ", " + longitude + Separator);
            GPSDataPoint gpsdataPoint = new GPSDataPoint(latitude, longitude, (long)time);
            gpsData.add(gpsdataPoint);
        }
    }

    public void writeTransferredPoints(String text) {

        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(transferredFilename, Context.MODE_APPEND);
            outputStream.write(text.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearTransferredPoints() {
        FileOutputStream outputStream;
        String text = "";

        try {
            outputStream = openFileOutput(transferredFilename, Context.MODE_PRIVATE);
            outputStream.write(text.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCurrentTimestamp() {
        long time = System.currentTimeMillis();
        Timestamp tsTemp = new Timestamp(time);
        return tsTemp.toString();
    }

    private void writeToLog(String text) {
        FileOutputStream outputStream;
        text = "[" + getCurrentTimestamp() + "] : " + text + Separator;

        try {
            outputStream = openFileOutput(logFilename, Context.MODE_APPEND);
            outputStream.write(text.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearLog() {
        FileOutputStream outputStream;
        String text = "";

        try {
            outputStream = openFileOutput(logFilename, Context.MODE_PRIVATE);
            outputStream.write(text.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class LocalBinder extends Binder {
        BluetoothLEService getService() {
            return BluetoothLEService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
//                new IntentFilter("my-event"));
        inputStrings = new ArrayList<String>();
        gpsData = new ArrayList<GPSDataPoint>();
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
//        mBluetoothManager == MainActivity.Blue
//        if (mBluetoothManager == null) {
//            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//            if (mBluetoothManager == null) {
//                Log.i(TAG, "Unable to initialize BluetoothManager.");
//                return false;
//            }
//        }

        //mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.i(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null) {
            Log.i(TAG, "BluetoothAdapter not initialized in Connect");
            return false;
        }
        if (address == null) {
            Log.i(TAG, "unspecified address.");
            return false;
        }




        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.i(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            return mBluetoothGatt.connect();
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.i(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, true, mGattCallback);
        Log.i(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        MainActivity.setDeviceAddress(address);
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    //gsf

    public void transmit(String message) {
        if (tx == null || message == null || message.isEmpty()) {
            // Do nothing if there is no device or message to send.
            Log.i(TAG, "something broke");
            return;
        }
        // Update TX characteristic value.  Note the setValue overload that takes a byte array must be used.
        tx.setValue(message.getBytes(Charset.forName("UTF-8")));
        if (mBluetoothGatt.writeCharacteristic(tx)) {
            Log.i(TAG, "Sent: " + message);
        }
        else {
            Log.i(TAG, "Couldn't write TX characteristic!");
        }
    }


    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.i(TAG, "BluetoothAdapter not initialized in Disconnect");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }
}
