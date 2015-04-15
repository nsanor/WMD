package com.example.sdp11.wmd;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

public class BluetoothLEService extends Service {
    private final static String TAG = BluetoothLEService.class.getSimpleName();

    // UUIDs for UAT service and associated characteristics.
    public static UUID UART_UUID = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    //public static UUID TX_UUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    public static UUID RX_UUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
    // UUID for the BTLE client characteristic which is necessary for notifications.
    public static UUID CLIENT_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;

    private String mBluetoothDeviceAddress;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.sdp11.wmd.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.sdp11.wmd.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.sdp11.wmd.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.sdp11.wmd.ACTION_DATA_AVAILABLE";

    private static final String Separator = System.getProperty("line.separator");
    private String transferredFilename = "transferred_points.txt";

    private ArrayList<GPSDataPoint> gpsData;

    private String allInput = "";
    private LatLng hole;

    public boolean mConnected = false;

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
                mBluetoothDeviceAddress = gatt.getDevice().getAddress();
                mConnected = true;
                //Discover services
                gatt.discoverServices();

            } else if (status == BluetoothGatt.GATT_SUCCESS
                    && newState == BluetoothProfile.STATE_DISCONNECTED) {
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
                writeToLog("Bluetooth Disconnected.");
                Log.e(TAG, "disconnected");
                mConnected = false;
                //Handle a disconnect event
            }

            else {
                Log.e(TAG, "Connection state changed.  New state: " + newState);
                mConnected = false;
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
            BluetoothGattCharacteristic rx = gatt.getService(UART_UUID).getCharacteristic(RX_UUID);
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

                String data = characteristic.getStringValue(0);
                Log.e(TAG, "onCharacteristicRead: " + data);
            }
        }

        // Called when a remote characteristic changes (like the RX characteristic).
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

            String data = characteristic.getStringValue(0);
            Log.e(TAG, "onCharacteristicChanged: " + data);
            writeToLog("Transferred Data: " + data);
            bufferStrings(data);
        }

        private void broadcastUpdate(final String action) {
            final Intent i = new Intent(action);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            BluetoothLEService.this.sendBroadcast(i);
        }

//        private void broadcastUpdate(final String action,
//                                     final BluetoothGattCharacteristic characteristic) {
//            final Intent intent = new Intent(action);
//
//            //writes the data formatted in HEX.
//            final byte[] data = characteristic.getValue();
//            if (data != null && data.length > 0) {
//                final StringBuilder stringBuilder = new StringBuilder(data.length);
//                for(byte byteChar : data)
//                    stringBuilder.append(String.format("%02X ", byteChar));
//                intent.putExtra(EXTRA_DATA, new String(data) + "\n" +
//                        stringBuilder.toString());
//            }
//            sendBroadcast(intent);
//        }
    };



    public void bufferStrings(String input) {
        allInput += input;
        parseTransferredData();
    }

    //Cycle through all transferred GPS and IMU data
    public void parseTransferredData() {
        if(allInput.contains("FF")) {
            String strings[] = allInput.split("\\$");
            for(String s: strings) {
                Log.e(TAG, s);
                if(s.length() >= 1) parseGPS(s);
                else Log.e(TAG, "Not a valid string");
            }
            processData();
            allInput = "";
        }
        //else Log.e(TAG, "No Termination character");
    }

    private void parseGPS(String i) {
        double latDeg, latMin, latitude, lonDeg, lonMin, longitude;
        String input[] = i.split("\\,");

        if ((input.length == 4)) {
            latDeg = Double.parseDouble(input[0].substring(0, 2));
            latMin = Double.parseDouble(input[0].substring(2));
            latitude = latDeg + (latMin / 60);
            if (input[1].equals(String.valueOf("S"))) latitude = -1 * latitude;
            lonDeg = Double.parseDouble(input[2].substring(0, 3));
            lonMin = Double.parseDouble(input[2].substring(3));
            longitude = (lonDeg + (lonMin / 60)) * -1;
            if (input[3].equals(String.valueOf("E"))) longitude = -1 * longitude;

            GPSDataPoint gpsdataPoint = new GPSDataPoint(latitude, longitude);

            if(gpsdataPoint.getLoc().distanceTo(MainActivity.mCurrentLocation) < 50) {
                Log.e(TAG, "Writing to log: " + latitude + ", " + longitude + Separator);
                writeTransferredPoints(latitude + ", " + longitude + Separator);
                gpsData.add(gpsdataPoint);
            }
            else if(gpsData.size() >= 1) {
                if(gpsdataPoint.getLoc().distanceTo(gpsData.get(gpsData.size() - 1).getLoc()) < 5) {
                    Log.e(TAG, "Writing to log: " + latitude + ", " + longitude + Separator);
                    writeTransferredPoints(latitude + ", " + longitude + Separator);
                    gpsData.add(gpsdataPoint);
                }
                Log.e(TAG, "Point " + latitude + ", " + longitude + " is not reliable");
            }
            else Log.e(TAG, "Point " + latitude + ", " + longitude + " is not reliable");

        }
        else Log.e(TAG, "Not a valid GPS string");
    }

    private void processData() {

        if(gpsData.size() < 1) return;
        //total distance = distance from last saved gps point to first
        double totalDistance = gpsData.get(0).getLoc().distanceTo(gpsData.get(gpsData.size() - 1).getLoc());

        //total angle = angle from last to first - angle to hole
        double totalAngle = calculateAngle();

        MainActivity.dataSource.createThrow(totalDistance, totalAngle);

        recalcTotals(totalDistance, totalAngle);
    }

    private double calculateAngle() {

        if(hole == null) return 1000;
        GPSDataPoint startingPoint = gpsData.get(0);
        GPSDataPoint endingPoint = gpsData.get(gpsData.size()-1);
        double lat1 = Math.toRadians(startingPoint.getLatitude());
        double long1 = Math.toRadians(startingPoint.getLongitude());
        double lat2 = Math.toRadians(endingPoint.getLatitude());
        double long2 = Math.toRadians(endingPoint.getLongitude());

        double deltaLong = long2 - long1;
        double y = Math.sin(deltaLong) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(deltaLong);
        double bearing1 =  Math.toDegrees(Math.atan2(y, x));

        lat2 = Math.toRadians(hole.latitude);
        long2 = Math.toRadians(hole.longitude);

        deltaLong = long2 - long1;
        y = Math.sin(deltaLong) * Math.cos(lat2);
        x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(deltaLong);
        double bearing2 =  Math.toDegrees(Math.atan2(y, x));

        return Math.abs(bearing2 - bearing1);
    }

    private void recalcTotals(double totalDistance, double totalAngle) {
        Log.e(TAG, "Passed params: totaldistance = " + totalDistance + " totalAngle = " + totalAngle);
        double averageDistance = TotalsData.getAverageDistance();
        double averageAngle = TotalsData.getAverageAngle();
        int throwCount = TotalsData.getThrowCount();
        Log.e(TAG, "Initial Conditions: throwcount = " + throwCount + " averagedistance = " + averageDistance + " averageangle = " + averageAngle);
        TotalsData.updateThrowCount();
        throwCount = TotalsData.getThrowCount();

        if(totalAngle != 1000.0){
            averageAngle = ((averageAngle*(throwCount-1)) + totalAngle) / throwCount;
            TotalsData.setAverageAngle(averageAngle);
        }

        averageDistance = ((averageDistance*(throwCount-1))+totalDistance)/ throwCount;
        TotalsData.setAverageDistance(averageDistance);
        Log.e(TAG, "Final Conditions: throwcount = " + throwCount + " averagedistance = " + averageDistance + " averageangle = " + averageAngle);

        MainActivity.dataSource.writeTotalsData();
    }

    public void setHoleLocation(Marker holeLocation) {
        hole = holeLocation.getPosition();
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

    public void clearHole() {
        FileOutputStream outputStream;
        String text = "";
        String holeFilename = "hole_location.txt";

        try {
            outputStream = openFileOutput(holeFilename, Context.MODE_PRIVATE);
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
        String logFilename = "my_log.txt";

        try {
            outputStream = openFileOutput(logFilename, Context.MODE_APPEND);
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
        gpsData = new ArrayList<GPSDataPoint>();


        //hole = new LatLng();
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

//    public boolean connect(final String address) {
//        if (mBluetoothAdapter == null) {
//            Log.i(TAG, "BluetoothAdapter not initialized in Connect");
//            return false;
//        }
//        if (address == null) {
//            Log.i(TAG, "unspecified address.");
//            return false;
//        }
//
//
//
//
//        // Previously connected device.  Try to reconnect.
//        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
//                && mBluetoothGatt != null) {
//            Log.i(TAG, "Trying to use an existing mBluetoothGatt for connection.");
//            return mBluetoothGatt.connect();
//        }
//
//        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
//        if (device == null) {
//            Log.i(TAG, "Device not found.  Unable to connect.");
//            return false;
//        }
//        // We want to directly connect to the device, so we are setting the autoConnect
//        // parameter to false.
//        mBluetoothGatt = device.connectGatt(this, true, mGattCallback);
//        Log.i(TAG, "Trying to create a new connection.");
//        mBluetoothDeviceAddress = address;
//        MainActivity.setDeviceAddress(address);
//        return true;
//    }

//    public void transmit(String message) {
//        if (tx == null || message == null || message.isEmpty()) {
//            // Do nothing if there is no device or message to send.
//            Log.i(TAG, "something broke");
//            return;
//        }
//        // Update TX characteristic value.  Note the setValue overload that takes a byte array must be used.
//        tx.setValue(message.getBytes(Charset.forName("UTF-8")));
//        if (mBluetoothGatt.writeCharacteristic(tx)) {
//            Log.i(TAG, "Sent: " + message);
//        }
//        else {
//            Log.i(TAG, "Couldn't write TX characteristic!");
//        }
//    }

    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.i(TAG, "BluetoothAdapter not initialized in Disconnect");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }
}