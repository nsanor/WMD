package com.example.sdp11.wmd;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Locale;


public class MainActivity extends Activity implements ActionBar.TabListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private final static String TAG = MainActivity.class.getSimpleName();

    private static SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private GoogleApiClient mGoogleApiClient;
    public static Location mCurrentLocation;
    private LocationRequest mLocationRequest;

    public static ThrowsDataSource dataSource;

    public static BluetoothLEService mBluetoothLEService;

    private static ConnectFragment connectFragment;
    private final static int REQUEST_ENABLE_BT = 1;

    private String testStrings[] = {"$,,,,0.00,0.00,",
            "060180,,,N$4104",
            "226,W$4104.5594",
            ",N,08130.6241,W",
            "$4104.5537,N,08",
            "130.6254,W$4104",
            ".5495,N,08130.6",
            "239,W$4104.5468",
            ",N,08130.6196,W",
            "130.6168,W$4104",
            ".5474,N,08130.6",
            "141,W$4104.5480",
            ",N,08130.6117,W",
            "$4104.5488,N,08",
            "130.6098,W$4104",
            ".5492,N,08130.6",
            ",N,08130.6086,W",
            "$4104.5494,N,08",
            "130.6083,W$4104",
            ".5491,N,08130.6",
            "080,W$4104.5489",
            ",N,08130.6075,W",
            "130.6073,W$4104",
            ".5481,N,08130.6",
            "075,W$4104.5478",
            ",N,08130.6074,W",
            "$4104.5478,N,08",
            "130.6074,W$4104",
            ".5478,N,08130.6",
            ",N,08130.6074,W",
            "$4104.5478,N,08",
            "130.6074,W$4104",
            ".5478,N,08130.6",
            "074,W$4104.5478",
            ",N,08130.6074,W",
            "130.6074,W$4104",
            ".5478,N,08130.6",
            "074,W$4104.5478",
            ",N,08130.6074,W",
            "$4104.5478,N,08",
            "130.6074,W$4104",
            ".5478,N,08130.6",
            ",N,08130.6074,W",
            "$FF"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buildGoogleApiClient();
        createLocationRequest();

        dataSource = new ThrowsDataSource(this);
        dataSource.open();
        dataSource.loadTotalsData();

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

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
//        if (mBluetoothLEService != null) {
//            //final boolean result = mBluetoothLEService.connect(mDeviceAddress);
//            //Log.i(TAG, "Connect request result=" + result);
//        }
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLEService = null;
        unregisterReceiver(mGattUpdateReceiver);
        dataSource.close();
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
            //mBluetoothLEService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLEService.disconnect();
            mBluetoothLEService = null;
        }
    };

    public static void getConnectTabReference() {
        connectFragment = (ConnectFragment)mSectionsPagerAdapter.getRegisteredFragment(0);
    }

//    private String getCurrentTimestamp() {
//        long time = System.currentTimeMillis();
//        Timestamp tsTemp = new Timestamp(time);
//        return tsTemp.toString();
//    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLEService.ACTION_GATT_CONNECTED.equals(action)) {
                //updateConnectionState(R.string.connected);
//                writeToLog("Bluetooth Connected.");
                connectFragment.setConnectionStatus(true);
                invalidateOptionsMenu();
            } else if (BluetoothLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
                //updateConnectionState(R.string.disconnected);
//                writeToLog("Bluetooth Disconnected.");
                connectFragment.setConnectionStatus(false);
                invalidateOptionsMenu();
            } //else if (BluetoothLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
//                // Show all the supported services and characteristics button_toggle the
//                // user interface.
////                writeToLog("Services Discovered.");
//                //displayGattServices(getSupportedGattServices());
//            } else if (BluetoothLEService.ACTION_DATA_AVAILABLE.equals(action)) {
//                //displayData(intent.getStringExtra(EXTRA_DATA));
//                //Log.e(TAG, intent.getStringExtra(mBluetoothLEService.EXTRA_DATA));
////                writeToLog("Characteristic Changed.");
////                writeToLog("Transferred Data: " + intent.getStringExtra(mBluetoothLEService.EXTRA_DATA));
//                //if it's been longer than 10 seconds, it's a new throw
////                if((System.currentTimeMillis() - lastSyncTime) > 10000) TotalsData.updateThrowCount();
////                lastSyncTime = System.currentTimeMillis();
////                parseTransferredData(intent.getStringExtra(mBluetoothLEService.EXTRA_DATA));
//            }
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

//    protected void stopLocationUpdates() {
//        LocationServices.FusedLocationApi.removeLocationUpdates(
//                mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
//    }

//    private class MyLocationListener implements LocationListener
//    {
//
//        public void onLocationChanged(final Location loc)
//        {
//            Log.e(TAG, "Location changed");
//        }
//
//        public void onProviderDisabled(String provider)
//        {
//            Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
//        }
//
//
//        public void onProviderEnabled(String provider)
//        {
//            Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
//        }
//
//
//        public void onStatusChanged(String provider, int status, Bundle extras)
//        {
//
//        }
//
//    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        //Log.e(TAG, "Location = " + location.getLatitude() + ", " + location.getLongitude());
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);//new Location("");
//        mCurrentLocation.setLatitude(41.075657);
//        mCurrentLocation.setLongitude(-81.509961);//
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
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, MainActivity.this);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks toggle the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.new_game:
                Toast.makeText(getApplicationContext(), "New Game",
                        Toast.LENGTH_SHORT).show();
                //Add yes/no dialog here
                TotalsData.updateGameId();
                mBluetoothLEService.clearTransferredPoints();
                mBluetoothLEService.clearHole();
                mBluetoothLEService.clearUserPoints();
                return true;
            case R.id.clear_all:
                new AlertDialog.Builder(this)
                        .setTitle("Delete data")
                        .setMessage("Are you sure you want to delete all throw data?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mBluetoothLEService.clearHole();
                                mBluetoothLEService.clearTransferredPoints();
                                mBluetoothLEService.clearUserPoints();
                                TotalsData.resetData();
                                dataSource.deleteAllThrows();
                                dataSource.deleteTotals();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
                return true;
            case R.id.about:
                Intent intent = new Intent(MainActivity.this, AboutUsActivity.class);
                MainActivity.this.startActivity(intent);
                return true;
            case R.id.view_log:
                Intent logIntent = new Intent(MainActivity.this, LogActivity.class);
                MainActivity.this.startActivity(logIntent);
                return true;
            case R.id.add_demo:
                for(String s: testStrings) {
                    mBluetoothLEService.bufferStrings(s);
                }
                return true;
            case R.id.map_legend:
                Intent LegendIntent = new Intent(MainActivity.this, LegendActivity.class);
                MainActivity.this.startActivity(LegendIntent);
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
}
