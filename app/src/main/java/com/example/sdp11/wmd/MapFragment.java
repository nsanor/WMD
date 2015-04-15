package com.example.sdp11.wmd;


import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Stack;

public class MapFragment extends Fragment {
    private final static String TAG = MapFragment.class.getSimpleName();

    private MapView mapView;
    private GoogleMap googleMap;
    private CameraPosition cp;

    private double latitude = 41.13747;
    private double longitude = -81.47430700000001;
    private Circle circle;
    private LatLng currentLocation;

    private Stack<Marker> markerStack;
    private Stack<Marker> userPoints;
    private Stack<Marker> transferredPoints;

    private Button plotHole;

    private Location mCurrentLocation;

    private String userPointsFilename = "user_points.txt";
    private String holeLocationFilename = "hole_location.txt";

    private Marker hole;
    private Marker locationMarker;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        View view = localInflater.inflate(R.layout.fragment_map, container, false);

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        markerStack = new Stack<Marker>();
        userPoints = new Stack<Marker>();
        transferredPoints = new Stack<Marker>();

        Button save = (Button) view.findViewById(R.id.button_save_points);
        Button clearUserPoints = (Button) view.findViewById(R.id.button_clear_user_points);
        Button refresh = (Button) view.findViewById(R.id.button_refresh_map);
        plotHole = (Button) view.findViewById(R.id.button_plot_hole);

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mapView.getMap();
        //for demo purposes, don't default to current location
//        mCurrentLocation = MainActivity.mCurrentLocation;
////        mCurrentLocation.setLatitude(41.075017);
////        mCurrentLocation.setLongitude(-81.510883);
//
//        // latitude and longitude
//        if(mCurrentLocation != null) {
//            latitude = mCurrentLocation.getLatitude();
//            longitude = mCurrentLocation.getLongitude();
//        }
//
//        final MarkerOptions locMarker = new MarkerOptions().position(new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)).title(latitude + ", " + longitude);
//        googleMap.addMarker(locMarker);
//        plotRadius(locMarker.getPosition(), TotalsData.getAverageDistance());
//        currentLocation = locMarker.getPosition();

        refreshCurrentLocation();

        //plotDemoData();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean mStackEmpty = markerStack.empty();
                if(!mStackEmpty) {
                    saveUserPoints();
                    Toast.makeText(getActivity(), "Points Saved!", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(getActivity(), "No Points To Save!", Toast.LENGTH_SHORT).show();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plotTransferredPointsFromFile();
            }
        });

        clearUserPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeUserPoints();
            }
        });

        plotHole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(plotHole.getText() == "Plot Hole") {
                    googleMap.setOnMapClickListener(new PlotHoleListener());
                    plotHole.setText("Cancel");
                }
                else {
                    googleMap.setOnMapClickListener(new UserPointsListener());
                    plotHole.setText("Plot Hole");
                }
            }
        });

        //calculateBounds();
        googleMap.setOnMapClickListener(new UserPointsListener());


        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(new LatLng(latitude, longitude)).zoom(14).build();
        refreshCamera();

        return view;
    }

    private void refreshCamera() {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(googleMap.getMaxZoomLevel() - 3).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
    }

    private void refreshCurrentLocation() {
        mCurrentLocation = MainActivity.mCurrentLocation;

        if(locationMarker != null) {
            locationMarker.remove();
        }

        if(circle != null) {
            circle.remove();
        }

        // latitude and longitude
        if(mCurrentLocation != null) {
            latitude = mCurrentLocation.getLatitude();
            longitude = mCurrentLocation.getLongitude();
        }

        MarkerOptions locationMarkerOptions = new MarkerOptions().position(new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person));
        locationMarker = googleMap.addMarker(locationMarkerOptions);
        plotRadius(locationMarkerOptions.getPosition(), TotalsData.getAverageDistance());
        currentLocation = locationMarkerOptions.getPosition();
    }

//    public boolean getDialog(String text) {
//        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which){
//                    case DialogInterface.BUTTON_POSITIVE:
//                        return true;
//                        break;
//
//                    case DialogInterface.BUTTON_NEGATIVE:
//                        //No button clicked
//                        break;
//                }
//            }
//        };
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
//                .setNegativeButton("No", dialogClickListener).show();
//    }
    private class UserPointsListener implements GoogleMap.OnMapClickListener {

        @Override
        public void onMapClick(LatLng point) {
            //lstLatLngs.add(point);
            Location location = new Location("");
            location.setLatitude(point.latitude);
            location.setLongitude(point.longitude);
            if (markerStack.empty()) {
                if (mCurrentLocation.distanceTo(location) <= TotalsData.getAverageDistance()) {
                    Marker marker = plotUserPoint(point);
                    markerStack.push(marker);
                } else
                    Toast.makeText(getActivity(), "Please select within radius", Toast.LENGTH_SHORT).show();
            } else {
                Location location2 = new Location("");
                location2.setLatitude(markerStack.peek().getPosition().latitude);
                location2.setLongitude(markerStack.peek().getPosition().longitude);

                if (location.distanceTo(location2) <= TotalsData.getAverageDistance()) {
                    Marker marker = plotUserPoint(point);
                    markerStack.push(marker);
                } else
                    Toast.makeText(getActivity(), "Please select within radius", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class PlotHoleListener implements GoogleMap.OnMapClickListener {

        @Override
        public void onMapClick(LatLng point) {
            if(hole != null) hole.remove();
            MarkerOptions holeOptions = new MarkerOptions().position(point).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_flag));
            hole = googleMap.addMarker(holeOptions);
            MainActivity.mBluetoothLEService.setHoleLocation(hole);
            googleMap.setOnMapClickListener(new UserPointsListener());
            plotHole.setText("Plot Hole");
        }
    }

    private void removeUserPoints() {
        FileOutputStream outputStream;
        String text = "";

        try {
            outputStream = getActivity().openFileOutput(userPointsFilename, Context.MODE_PRIVATE);
            outputStream.write(text.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Don't remove saved points at end
        boolean pointsToRemove = (!userPoints.empty() || !markerStack.empty());
        if(pointsToRemove){
            while(!userPoints.empty()) userPoints.pop().remove();
            while(!markerStack.empty()) markerStack.pop().remove();
            circle.remove();
            plotRadius(currentLocation, TotalsData.getAverageDistance());
        }
        //else Toast.makeText(getActivity(), "No Points To Remove!", Toast.LENGTH_SHORT).show();
    }

//    private void removeTransferredPoints() {
//        FileOutputStream outputStream;
//        String text = "";
//        String transferredPointsFilename = "transferred_points.txt";
//
//        try {
//            outputStream = getActivity().openFileOutput(transferredPointsFilename, Context.MODE_PRIVATE);
//            outputStream.write(text.getBytes());
//            outputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        //Don't remove saved points at end
//        boolean pointsToRemove = (!transferredPoints.empty());
//        if(pointsToRemove){
//            while(!transferredPoints.empty()) transferredPoints.pop().remove();
//        }
//    }

    private void writeHoleLocationToFile() {
        FileOutputStream outputStream;
        if(hole == null) return;
        String text = hole.getPosition().latitude + ", " + hole.getPosition().longitude;
        //Log.e(TAG, "writing to hole file");

        try {
            outputStream = getActivity().openFileOutput(holeLocationFilename, Context.MODE_PRIVATE);
            outputStream.write(text.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void plotHoleLocationFromFile() {
        try {
            InputStream inputStream = getActivity().openFileInput(holeLocationFilename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    String point[] = receiveString.split(",");
                    LatLng p = new LatLng(Double.parseDouble(point[0]), Double.parseDouble(point[1]));
                    if(hole != null) hole.remove();
                    MarkerOptions holeOptions = new MarkerOptions().position(p).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_flag));
                    hole = googleMap.addMarker(holeOptions);
                }

                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.i(TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.i(TAG, "Can not read file: " + e.toString());
        }
    }

    private void plotUserPointsFromFile() {
        try {
            InputStream inputStream = getActivity().openFileInput("user_points.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    String point[] = receiveString.split(",");
                    LatLng p = new LatLng(Double.parseDouble(point[0]), Double.parseDouble(point[1]));
                    Marker m = plotPoint(p, true);
                    userPoints.push(m);
                }

                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.i(TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.i(TAG, "Can not read file: " + e.toString());
        }
    }

    private void plotTransferredPointsFromFile() {
        while(!transferredPoints.empty()) transferredPoints.pop().remove();

        boolean first = true;
        Marker m;

        refreshCurrentLocation();
        refreshCamera();

        try {
            InputStream inputStream = getActivity().openFileInput("transferred_points.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = bufferedReader.readLine();

                while (receiveString != null) {
                    String point[] = receiveString.split(",");
                    LatLng p = new LatLng(Double.parseDouble(point[0]), Double.parseDouble(point[1]));
                    receiveString = bufferedReader.readLine();
                    if(first){
                        MarkerOptions mOptions = new MarkerOptions().position(p).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_x));
                        m = googleMap.addMarker(mOptions);
                        m.showInfoWindow();
                        first = false;
                    }
                    else if(receiveString == null) {
                        MarkerOptions mOptions = new MarkerOptions().position(p).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_star));
                        m = googleMap.addMarker(mOptions);
                    }
                    else {
                        m = plotPoint(p, false);
                    }
                    transferredPoints.push(m);
                }

                inputStream.close();
                //plotPolyLine();
            }
        }
        catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "Can not read file: " + e.toString());
        }
    }

    private void saveUserPoints() {
        FileOutputStream outputStream;
        String text = "";


        while(!markerStack.empty()) {
            Marker m = markerStack.pop();
            userPoints.push(m);
            LatLng point = m.getPosition();
            text += point.latitude + ", " + point.longitude + "\n";
        }

        try {
            outputStream = getActivity().openFileOutput(userPointsFilename, Context.MODE_APPEND);
            outputStream.write(text.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        circle.remove();
        //plotRadius(new LatLng(latitude, longitude), TotalsData.getAverageDistance());
    }

    @Override
    public void onResume() {

        super.onResume();
        mapView.onResume();

        MapsInitializer.initialize(getActivity());

        if (cp != null) {
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
            cp = null;
        }

        removeUserPoints();
        //removeTransferredPoints();
        plotTransferredPointsFromFile();
        plotUserPointsFromFile();
        plotHoleLocationFromFile();
        if(hole != null) MainActivity.mBluetoothLEService.setHoleLocation(hole);
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();

        cp = googleMap.getCameraPosition();
        writeHoleLocationToFile();
//        googleMap = null;

    }

    private Marker plotPoint(LatLng point, boolean user) {
        // create marker
        MarkerOptions marker = new MarkerOptions().position(point);

        if(user) {
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).alpha(0.7f);
        }
        else {
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).alpha(0.7f);
        }

        return googleMap.addMarker(marker);
    }

//    private void plotPolyLine() {
//        PolylineOptions line= new PolylineOptions().width(8).color(Color.BLACK);
//
//        for (LatLng point : points) {
//            line.add(point);
//        }
//
//        googleMap.addPolyline(line);
//    }

    private Marker plotUserPoint(LatLng point) {
        Marker newMarker = plotPoint(point, true);

        if(circle != null) circle.remove();

        plotRadius(point, TotalsData.getAverageDistance());

        return newMarker;
    }

    private void plotRadius(LatLng point, double radius) {
        // Instantiates a new CircleOptions object and defines the center and radius
        CircleOptions circleOptions = new CircleOptions().strokeWidth(5)
                .center(point)
                .radius(radius); // In meters

        // Get back the mutable Circle
        circle = googleMap.addCircle(circleOptions);
    }
}
