package com.example.sdp11.wmd;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.os.IBinder;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.internal.da;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends Activity implements ActionBar.TabListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private final static String TAG = MainActivity.class.getSimpleName();

    private static SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private GoogleApiClient mGoogleApiClient;
    private static Location mCurrentLocation;
    private String mLastUpdateTime;
    private Boolean mRequestingLocationUpdates = true;
    private LocationRequest mLocationRequest;

    public static ThrowsDataSource dataSource;
    public static SQLiteDatabase throwsDB;

    public static BluetoothLEService mBluetoothLEService;
    private BluetoothGatt mBluetoothGatt;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics;

    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private static String mDeviceAddress = "DB:AB:7F:DD:10:0F";

    private boolean mConnected = false;

    private ArrayList<GPSDataPoint> GPSCoordinates;

    private static ConnectFragment connectFragment;
    private static DataFragment dataFragment;
    private static MapFragment mapFragment;

    private static final String Separator = System.getProperty("line.separator");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if( savedInstanceState != null ){
//            getActionBar().selectTab(savedInstanceState.getInt(tabState));
//        }

        buildGoogleApiClient();
        createLocationRequest();

        dataSource = new ThrowsDataSource(this);
        dataSource.open();
        TotalsData.loadTotalsData(dataSource.getMaxThrowId());

        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        mBluetoothLEService = new BluetoothLEService();
        Intent gattServiceIntent = new Intent(this, BluetoothLEService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    //Cycle through all transferred GPS and IMU data
    private void parseTransferredData() {
        String sampleGPS[] = {"$GPRMC,180338.600,A,4104.5010,N,08130.6533,W,2.67,356.61,190215,,,A*7D\n",
                "$GPRMC,180338.800,A,4104.5012,N,08130.6533,W,2.55,358.37,190215,,,A*7D\n",
                "$GPRMC,180339.000,A,4104.5013,N,08130.6533,W,2.80,356.43,190215,,,A*70\n",
                "$GPRMC,180339.200,A,4104.5014,N,08130.6533,W,2.39,353.28,190215,,,A*7F\n",
                "$GPRMC,180339.400,A,4104.5016,N,08130.6533,W,2.67,352.87,190215,,,A*74\n",
                "$GPRMC,180339.600,A,4104.5017,N,08130.6532,W,2.82,358.80,190215,,,A*70"};

        for (String s: sampleGPS) {
            GPSDataPoint gps = parseGPS(s);
            //if(gps != null) GPSCoordinates.add(gps); //Create throw when we get sample data from IMU
            //dataSource.createThrow();
        }
    }

    private GPSDataPoint parseGPS(String GPSData) {
        String gps[] = GPSData.split(",");
        double latDeg, latMin, latitude, lonDeg, lonMin, longitude, time;
        if ((gps[0].equals("$GPRMC")) && (gps[7] != null)) {
            time = Double.parseDouble(gps[1]);
            latDeg =Double.parseDouble(gps[3].substring(0, 2));
            latMin =Double.parseDouble(gps[3].substring(2, 8));
            latitude = latDeg + (latMin / 60);
            if (gps[4].equals(String.valueOf('S'))) latitude = -1 * latitude;
            lonDeg =Double.parseDouble(gps[5].substring(0, 3));
            lonMin =Double.parseDouble(gps[5].substring(3, 9));
            longitude = lonDeg + (lonMin / 60);
            if (gps[6].equals(String.valueOf('W'))) longitude = -1 * longitude;
            return new GPSDataPoint(latitude, longitude, 1);
        }

        return null;
    }

    private void writeToLog(String text) {
        String filename = "my_log.txt";
        FileOutputStream outputStream;
        text = "[" + getCurrentTimestamp() + "] : " + text + Separator;

        try {
            outputStream = openFileOutput(filename, Context.MODE_APPEND);
            outputStream.write(text.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setDeviceAddress(String address) {
        mDeviceAddress = address;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLEService != null) {
            final boolean result = mBluetoothLEService.connect(mDeviceAddress);
            Log.i(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public static void getConnectTabReference() {
        connectFragment = (ConnectFragment)mSectionsPagerAdapter.getRegisteredFragment(0);
    }

    public static void getDataTabReference() {
        dataFragment = (DataFragment)mSectionsPagerAdapter.getRegisteredFragment(1);
    }

    public static void getMapTabReference() {
        mapFragment = (MapFragment)mSectionsPagerAdapter.getRegisteredFragment(2);
    }




    private String getCurrentTimestamp() {
        long time = System.currentTimeMillis();
        Timestamp tsTemp = new Timestamp(time);
        return tsTemp.toString();
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (mBluetoothLEService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                //updateConnectionState(R.string.connected);
                mConnectionState = STATE_CONNECTED;
                writeToLog("Bluetooth Connected.");
                connectFragment.setConnectionStatus(mBluetoothLEService.getmBluetoothDeviceAddress());
                invalidateOptionsMenu();
            } else if (mBluetoothLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                //updateConnectionState(R.string.disconnected);
                mConnectionState = STATE_DISCONNECTED;
                writeToLog("Bluetooth Disconnected.");
                connectFragment.setConnectionStatus(null);
                invalidateOptionsMenu();
                //clearUI();
            } else if (mBluetoothLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics button_toggle the
                // user interface.
                writeToLog("Services Discovered.");
                displayGattServices(getSupportedGattServices());
            } else if (mBluetoothLEService.ACTION_DATA_AVAILABLE.equals(action)) {
                //displayData(intent.getStringExtra(EXTRA_DATA));
                Log.e(TAG, intent.getStringExtra(mBluetoothLEService.EXTRA_DATA));
                writeToLog("Characteristic Changed.");
                writeToLog("Transferred Data: " + intent.getStringExtra(mBluetoothLEService.EXTRA_DATA));
                parseTransferredData();
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLEService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLEService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLEService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLEService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = "unknown"; //getResources().
        //getString(R.string.unknown_service);
        String unknownCharaString = "unknown"; //getResources().
        //getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData =
                new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics =
                new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData =
                    new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.
                            lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();
            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic :
                    gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData =
                        new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid,
                                unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLEService = ((BluetoothLEService.LocalBinder) service).getService();
            if (!mBluetoothLEService.initialize()) {
                Log.i(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLEService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLEService = null;
        }
    };

//    protected void stopLocationUpdates() {
//        LocationServices.FusedLocationApi.removeLocationUpdates(
//                mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
//    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //savedInstanceState.putInt("tabState", getSelectedTab());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLEService = null;
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            Fragment fragment = null;
            if(position==0) fragment = new ConnectFragment();
            if(position==1) fragment = new DataFragment();
            if(position==2) fragment = new MapFragment();
            return fragment;
        }

        @Override
        public int getCount() {
            // Setup and Map tabs
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks toggle the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), "Settings",
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.about:
//                Toast.makeText(getApplicationContext(), "About Us",
//                        Toast.LENGTH_SHORT).show();
                parseTransferredData();
                return true;
            case R.id.view_log:
                Intent intent = new Intent(MainActivity.this, LogActivity.class);
                MainActivity.this.startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    protected synchronized void buildGoogleApiClient() {
       mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }

    public static Location getmCurrentLocation() {
        return mCurrentLocation;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
    }
}
