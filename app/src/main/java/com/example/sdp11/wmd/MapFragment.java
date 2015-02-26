package com.example.sdp11.wmd;


import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Stack;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {

    double latitude = 41.13747;
    double longitude = -81.47430700000001;
    LatLngBounds bounds;
    private Circle circle;

    private MapView mapView;
    private GoogleMap googleMap;
    private CameraPosition cp;
    private Button mode;
    private Button undo;
    private TextView label;

    private boolean planning = false;

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
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        final Stack<Marker> markerStack = new Stack<Marker>();

        mode = (Button) view.findViewById(R.id.button_mode);
        undo = (Button) view.findViewById(R.id.button_undo);
        label = (TextView) view.findViewById(R.id.mode_label);

        if(planning) {
            undo.setVisibility(View.VISIBLE);
            label.setText("Planning Mode");
        }



        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mapView.getMap();
        //for demo purposes, don't default to current location
        final Location mCurrentLocation = new Location(""); //MainActivity.getmCurrentLocation();
        mCurrentLocation.setLatitude(41.075017);
        mCurrentLocation.setLongitude(-81.510883);

        // latitude and longitude
        if(mCurrentLocation != null) {
            latitude = mCurrentLocation.getLatitude();
            longitude = mCurrentLocation.getLongitude();
        }

        final MarkerOptions locMarker = new MarkerOptions().position(new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)).title("You are here");
        googleMap.addMarker(locMarker);
        plotRadius(locMarker.getPosition(), TotalsData.getAverageDistance());

        plotDemoData();

        mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!planning) {
                    undo.setVisibility(View.VISIBLE);
                    label.setText("Planning Mode");
                    planning = true;
                }
                else {
                    undo.setVisibility(View.INVISIBLE);
                    label.setText("Normal Mode");
                    planning = false;
                }
            }
        });

        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!markerStack.empty()) {

                    //Remove last marker that was placed
                    Marker marker = markerStack.pop();
                    marker.remove();
                    //Remove the circle for that marker
                    if(circle != null) circle.remove();
                    //Get the next to last marker and re-add circle
                    if (!markerStack.empty()) {
                        marker = markerStack.pop();
                        plotRadius(marker.getPosition(), TotalsData.getAverageDistance());
                        markerStack.push(marker);
                    }
                }
                if(markerStack.empty()) plotRadius(locMarker.getPosition(), TotalsData.getAverageDistance());
            }
        });


        //calculateBounds();
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                //lstLatLngs.add(point);
                Location location = new Location("");
                location.setLatitude(point.latitude);
                location.setLongitude(point.longitude);
                if(!planning) {
                    Toast.makeText(getActivity(), "Please turn on planning mode", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (markerStack.empty()) {
                    if(mCurrentLocation.distanceTo(location) <= TotalsData.getAverageDistance()){
                        Marker marker = plotUserPoint(point);
                        markerStack.push(marker);
                    }
                    else Toast.makeText(getActivity(), "Please select within radius", Toast.LENGTH_SHORT).show();
                }
                else {
                    Location location2 = new Location("");
                    location2.setLatitude(markerStack.peek().getPosition().latitude);
                    location2.setLongitude(markerStack.peek().getPosition().longitude);

                    if(location.distanceTo(location2) <= TotalsData.getAverageDistance()) {
                        Marker marker = plotUserPoint(point);
                        markerStack.push(marker);
                    }
                    else Toast.makeText(getActivity(), "Please select within radius", Toast.LENGTH_SHORT).show();
                }
            }
        });


        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(new LatLng(latitude, longitude)).zoom(14).build();
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(googleMap.getMaxZoomLevel() - 3).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        return view;
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
        else Log.e("", "Not saved");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();

        cp = googleMap.getCameraPosition();
//        googleMap = null;


    }

    private void calculateBounds() {
        //Add formula to calculate distance between lat and long lines at current location
        //Want ~5 mile bounds (8.4 km)
        //About 69km between lines on average
        //About .12 degrees for bounds, 0.6 on either side
        LatLng northeast = new LatLng(latitude + 0.06, longitude + 0.06);
        LatLng southwest = new LatLng(latitude - 0.06, longitude - 0.06);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(northeast);
        builder.include(southwest);
        bounds = builder.build();
    }

    private Marker plotPoint(LatLng point, boolean user) {
        // create marker
        MarkerOptions marker = new MarkerOptions().position(point);

        if(user) {
            // Changing marker icon
            marker.icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        }
        else marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        Marker newMarker = googleMap.addMarker(marker);
        return newMarker;
    }

    private Marker plotUserPoint(LatLng point) {
        Marker newMarker = plotPoint(point, true);

        if(circle != null) circle.remove();

        plotRadius(point, TotalsData.getAverageDistance());

        return newMarker;
    }

    private void plotRadius(LatLng point, double radius) {
        // Instantiates a new CircleOptions object and defines the center and radius
        CircleOptions circleOptions = new CircleOptions()
                .center(point)
                .radius(radius); // In meters

// Get back the mutable Circle
        circle = googleMap.addCircle(circleOptions);
    }

    private void plotDemoData() {
        //Plot Demo Data
        plotPoint(new LatLng(41.075017, -81.510883), false);
        plotPoint(new LatLng(41.075017, -81.510883), false);
        plotPoint(new LatLng(41.075017, -81.510883), false);
        plotPoint(new LatLng(41.075017, -81.510883), false);
        plotPoint(new LatLng(41.075017, -81.510883), false);
        plotPoint(new LatLng(41.075017, -81.510883), false);
        plotPoint(new LatLng(41.075017, -81.510883), false);
        plotPoint(new LatLng(41.075033, -81.510883), false);
        plotPoint(new LatLng(41.075033, -81.510883), false);
        plotPoint(new LatLng(41.075033, -81.510883), false);
        plotPoint(new LatLng(41.075033, -81.510883), false);
        plotPoint(new LatLng(41.075033, -81.510883), false);
        plotPoint(new LatLng(41.075033, -81.510883), false);
        plotPoint(new LatLng(41.075033, -81.510883), false);
        plotPoint(new LatLng(41.075050, -81.510867), false);
        plotPoint(new LatLng(41.075050, -81.510867), false);
        plotPoint(new LatLng(41.075050, -81.510867), false);
        plotPoint(new LatLng(41.075050, -81.510867), false);
        plotPoint(new LatLng(41.075050, -81.510867), false);
        plotPoint(new LatLng(41.075050, -81.510867), false);
        plotPoint(new LatLng(41.075050, -81.510867), false);
        plotPoint(new LatLng(41.075067, -81.510867), false);
        plotPoint(new LatLng(41.075067, -81.510867), false);
        plotPoint(new LatLng(41.075067, -81.510867), false);
        plotPoint(new LatLng(41.075067, -81.510867), false);
        plotPoint(new LatLng(41.075067, -81.510867), false);
        plotPoint(new LatLng(41.075067, -81.510867), false);
        plotPoint(new LatLng(41.075083, -81.510867), false);
        plotPoint(new LatLng(41.075083, -81.510867), false);
        plotPoint(new LatLng(41.075083, -81.510867), false);
        plotPoint(new LatLng(41.075083, -81.510867), false);
        plotPoint(new LatLng(41.075083, -81.510867), false);
        plotPoint(new LatLng(41.075083, -81.510867), false);
        plotPoint(new LatLng(41.075083, -81.510867), false);
        plotPoint(new LatLng(41.075100, -81.510867), false);
        plotPoint(new LatLng(41.075100, -81.510867), false);
        plotPoint(new LatLng(41.075100, -81.510867), false);
        plotPoint(new LatLng(41.075100, -81.510867), false);
        plotPoint(new LatLng(41.075100, -81.510867), false);
        plotPoint(new LatLng(41.075100, -81.510867), false);
        plotPoint(new LatLng(41.075117, -81.510867), false);
        plotPoint(new LatLng(41.075117, -81.510867), false);
        plotPoint(new LatLng(41.075117, -81.510867), false);
        plotPoint(new LatLng(41.075117, -81.510867), false);
        plotPoint(new LatLng(41.075117, -81.510867), false);
        plotPoint(new LatLng(41.075117, -81.510867), false);
        plotPoint(new LatLng(41.075133, -81.510867), false);
        plotPoint(new LatLng(41.075133, -81.510867), false);
        plotPoint(new LatLng(41.075133, -81.510867), false);
        plotPoint(new LatLng(41.075133, -81.510850), false);
        plotPoint(new LatLng(41.075133, -81.510850), false);
        plotPoint(new LatLng(41.075133, -81.510850), false);
        plotPoint(new LatLng(41.075133, -81.510850), false);
        plotPoint(new LatLng(41.075150, -81.510850), false);
        plotPoint(new LatLng(41.075150, -81.510850), false);
        plotPoint(new LatLng(41.075150, -81.510850), false);
        plotPoint(new LatLng(41.075150, -81.510850), false);
        plotPoint(new LatLng(41.075150, -81.510850), false);
        plotPoint(new LatLng(41.075150, -81.510850), false);
        plotPoint(new LatLng(41.075167, -81.510850), false);
        plotPoint(new LatLng(41.075167, -81.510850), false);
        plotPoint(new LatLng(41.075167, -81.510850), false);
        plotPoint(new LatLng(41.075167, -81.510850), false);
        plotPoint(new LatLng(41.075167, -81.510833), false);
        plotPoint(new LatLng(41.075167, -81.510833), false);
        plotPoint(new LatLng(41.075167, -81.510833), false);
        plotPoint(new LatLng(41.075183, -81.510833), false);
        plotPoint(new LatLng(41.075183, -81.510833), false);
        plotPoint(new LatLng(41.075183, -81.510833), false);
        plotPoint(new LatLng(41.075183, -81.510833), false);
        plotPoint(new LatLng(41.075183, -81.510833), false);
        plotPoint(new LatLng(41.075183, -81.510833), false);
        plotPoint(new LatLng(41.075200, -81.510817), false);
        plotPoint(new LatLng(41.075200, -81.510817), false);
        plotPoint(new LatLng(41.075200, -81.510817), false);
        plotPoint(new LatLng(41.075200, -81.510817), false);
        plotPoint(new LatLng(41.075200, -81.510817), false);
        plotPoint(new LatLng(41.075200, -81.510817), false);
        plotPoint(new LatLng(41.075200, -81.510817), false);
        plotPoint(new LatLng(41.075200, -81.510817), false);
        plotPoint(new LatLng(41.075217, -81.510800), false);
        plotPoint(new LatLng(41.075217, -81.510800), false);
        plotPoint(new LatLng(41.075217, -81.510800), false);
        plotPoint(new LatLng(41.075217, -81.510800), false);
        plotPoint(new LatLng(41.075217, -81.510800), false);
        plotPoint(new LatLng(41.075217, -81.510800), false);
        plotPoint(new LatLng(41.075217, -81.510783), false);
        plotPoint(new LatLng(41.075217, -81.510783), false);
        plotPoint(new LatLng(41.075217, -81.510783), false);
        plotPoint(new LatLng(41.075233, -81.510783), false);
        plotPoint(new LatLng(41.075233, -81.510783), false);
        plotPoint(new LatLng(41.075233, -81.510767), false);
        plotPoint(new LatLng(41.075233, -81.510767), false);
        plotPoint(new LatLng(41.075233, -81.510767), false);
        plotPoint(new LatLng(41.075233, -81.510767), false);
        plotPoint(new LatLng(41.075233, -81.510767), false);
        plotPoint(new LatLng(41.075233, -81.510767), false);
        plotPoint(new LatLng(41.075233, -81.510750), false);
        plotPoint(new LatLng(41.075233, -81.510750), false);
        plotPoint(new LatLng(41.075233, -81.510750), false);
        plotPoint(new LatLng(41.075250, -81.510750), false);
        plotPoint(new LatLng(41.075250, -81.510750), false);
        plotPoint(new LatLng(41.075250, -81.510733), false);
        plotPoint(new LatLng(41.075250, -81.510733), false);
        plotPoint(new LatLng(41.075250, -81.510733), false);
        plotPoint(new LatLng(41.075250, -81.510733), false);
        plotPoint(new LatLng(41.075250, -81.510733), false);
        plotPoint(new LatLng(41.075250, -81.510717), false);
        plotPoint(new LatLng(41.075250, -81.510717), false);
        plotPoint(new LatLng(41.075250, -81.510717), false);
        plotPoint(new LatLng(41.075250, -81.510717), false);
        plotPoint(new LatLng(41.075250, -81.510717), false);
        plotPoint(new LatLng(41.075250, -81.510700), false);
        plotPoint(new LatLng(41.075250, -81.510700), false);
        plotPoint(new LatLng(41.075250, -81.510700), false);
        plotPoint(new LatLng(41.075250, -81.510700), false);
        plotPoint(new LatLng(41.075250, -81.510683), false);
        plotPoint(new LatLng(41.075267, -81.510683), false);
        plotPoint(new LatLng(41.075267, -81.510683), false);
        plotPoint(new LatLng(41.075267, -81.510683), false);
        plotPoint(new LatLng(41.075267, -81.510683), false);
        plotPoint(new LatLng(41.075267, -81.510667), false);
        plotPoint(new LatLng(41.075267, -81.510667), false);
        plotPoint(new LatLng(41.075267, -81.510667), false);
        plotPoint(new LatLng(41.075267, -81.510667), false);
        plotPoint(new LatLng(41.075267, -81.510667), false);
        plotPoint(new LatLng(41.075267, -81.510650), false);
        plotPoint(new LatLng(41.075267, -81.510650), false);
        plotPoint(new LatLng(41.075267, -81.510650), false);
        plotPoint(new LatLng(41.075267, -81.510650), false);
        plotPoint(new LatLng(41.075267, -81.510650), false);
        plotPoint(new LatLng(41.075267, -81.510633), false);
        plotPoint(new LatLng(41.075267, -81.510633), false);
        plotPoint(new LatLng(41.075267, -81.510633), false);
        plotPoint(new LatLng(41.075283, -81.510633), false);
        plotPoint(new LatLng(41.075283, -81.510633), false);
        plotPoint(new LatLng(41.075283, -81.510617), false);
        plotPoint(new LatLng(41.075283, -81.510617), false);
        plotPoint(new LatLng(41.075283, -81.510617), false);
        plotPoint(new LatLng(41.075283, -81.510617), false);
        plotPoint(new LatLng(41.075283, -81.510617), false);
        plotPoint(new LatLng(41.075283, -81.510600), false);
        plotPoint(new LatLng(41.075283, -81.510600), false);
        plotPoint(new LatLng(41.075283, -81.510600), false);
        plotPoint(new LatLng(41.075283, -81.510600), false);
        plotPoint(new LatLng(41.075283, -81.510583), false);
        plotPoint(new LatLng(41.075283, -81.510583), false);
        plotPoint(new LatLng(41.075283, -81.510583), false);
        plotPoint(new LatLng(41.075283, -81.510583), false);
        plotPoint(new LatLng(41.075283, -81.510583), false);
        plotPoint(new LatLng(41.075283, -81.510567), false);
        plotPoint(new LatLng(41.075283, -81.510567), false);
        plotPoint(new LatLng(41.075283, -81.510567), false);
        plotPoint(new LatLng(41.075283, -81.510567), false);
        plotPoint(new LatLng(41.075283, -81.510567), false);
        plotPoint(new LatLng(41.075283, -81.510550), false);
        plotPoint(new LatLng(41.075283, -81.510550), false);
        plotPoint(new LatLng(41.075283, -81.510550), false);
        plotPoint(new LatLng(41.075283, -81.510550), false);
        plotPoint(new LatLng(41.075283, -81.510550), false);
        plotPoint(new LatLng(41.075283, -81.510533), false);
        plotPoint(new LatLng(41.075283, -81.510533), false);
        plotPoint(new LatLng(41.075283, -81.510533), false);
        plotPoint(new LatLng(41.075283, -81.510533), false);
        plotPoint(new LatLng(41.075283, -81.510533), false);
        plotPoint(new LatLng(41.075283, -81.510517), false);
        plotPoint(new LatLng(41.075283, -81.510517), false);
        plotPoint(new LatLng(41.075283, -81.510517), false);
        plotPoint(new LatLng(41.075283, -81.510517), false);
        plotPoint(new LatLng(41.075283, -81.510500), false);
        plotPoint(new LatLng(41.075283, -81.510500), false);
        plotPoint(new LatLng(41.075283, -81.510500), false);
        plotPoint(new LatLng(41.075283, -81.510500), false);
        plotPoint(new LatLng(41.075283, -81.510500), false);
        plotPoint(new LatLng(41.075283, -81.510483), false);
        plotPoint(new LatLng(41.075283, -81.510483), false);
        plotPoint(new LatLng(41.075283, -81.510483), false);
        plotPoint(new LatLng(41.075283, -81.510483), false);
        plotPoint(new LatLng(41.075300, -81.510483), false);
        plotPoint(new LatLng(41.075300, -81.510467), false);
        plotPoint(new LatLng(41.075300, -81.510467), false);
        plotPoint(new LatLng(41.075300, -81.510467), false);
        plotPoint(new LatLng(41.075300, -81.510467), false);
        plotPoint(new LatLng(41.075300, -81.510467), false);
        plotPoint(new LatLng(41.075300, -81.510450), false);
        plotPoint(new LatLng(41.075300, -81.510450), false);
        plotPoint(new LatLng(41.075300, -81.510450), false);
        plotPoint(new LatLng(41.075300, -81.510450), false);
        plotPoint(new LatLng(41.075300, -81.510450), false);
        plotPoint(new LatLng(41.075300, -81.510433), false);
        plotPoint(new LatLng(41.075300, -81.510433), false);
        plotPoint(new LatLng(41.075300, -81.510433), false);
        plotPoint(new LatLng(41.075300, -81.510433), false);
        plotPoint(new LatLng(41.075300, -81.510433), false);
        plotPoint(new LatLng(41.075300, -81.510417), false);
        plotPoint(new LatLng(41.075300, -81.510417), false);
        plotPoint(new LatLng(41.075300, -81.510417), false);
        plotPoint(new LatLng(41.075300, -81.510417), false);
        plotPoint(new LatLng(41.075300, -81.510417), false);
        plotPoint(new LatLng(41.075300, -81.510417), false);
        plotPoint(new LatLng(41.075300, -81.510400), false);
        plotPoint(new LatLng(41.075300, -81.510400), false);
        plotPoint(new LatLng(41.075300, -81.510400), false);
        plotPoint(new LatLng(41.075300, -81.510400), false);
        plotPoint(new LatLng(41.075300, -81.510400), false);
        plotPoint(new LatLng(41.075300, -81.510383), false);
        plotPoint(new LatLng(41.075300, -81.510383), false);
        plotPoint(new LatLng(41.075300, -81.510383), false);
        plotPoint(new LatLng(41.075300, -81.510383), false);
        plotPoint(new LatLng(41.075300, -81.510383), false);
        plotPoint(new LatLng(41.075300, -81.510367), false);
        plotPoint(new LatLng(41.075300, -81.510367), false);
        plotPoint(new LatLng(41.075300, -81.510367), false);
        plotPoint(new LatLng(41.075300, -81.510367), false);
        plotPoint(new LatLng(41.075300, -81.510367), false);
        plotPoint(new LatLng(41.075300, -81.510350), false);
        plotPoint(new LatLng(41.075300, -81.510350), false);
        plotPoint(new LatLng(41.075300, -81.510350), false);
        plotPoint(new LatLng(41.075300, -81.510350), false);
        plotPoint(new LatLng(41.075300, -81.510350), false);
        plotPoint(new LatLng(41.075300, -81.510333), false);
        plotPoint(new LatLng(41.075300, -81.510333), false);
        plotPoint(new LatLng(41.075300, -81.510333), false);
        plotPoint(new LatLng(41.075300, -81.510333), false);
        plotPoint(new LatLng(41.075300, -81.510317), false);
        plotPoint(new LatLng(41.075300, -81.510317), false);
        plotPoint(new LatLng(41.075300, -81.510317), false);
        plotPoint(new LatLng(41.075300, -81.510317), false);
        plotPoint(new LatLng(41.075300, -81.510317), false);
        plotPoint(new LatLng(41.075300, -81.510300), false);
        plotPoint(new LatLng(41.075300, -81.510300), false);
        plotPoint(new LatLng(41.075300, -81.510300), false);
        plotPoint(new LatLng(41.075300, -81.510300), false);
        plotPoint(new LatLng(41.075300, -81.510300), false);
        plotPoint(new LatLng(41.075283, -81.510283), false);
        plotPoint(new LatLng(41.075283, -81.510283), false);
        plotPoint(new LatLng(41.075283, -81.510283), false);
        plotPoint(new LatLng(41.075283, -81.510283), false);
        plotPoint(new LatLng(41.075283, -81.510283), false);
        plotPoint(new LatLng(41.075283, -81.510267), false);
        plotPoint(new LatLng(41.075283, -81.510267), false);
        plotPoint(new LatLng(41.075283, -81.510267), false);
        plotPoint(new LatLng(41.075283, -81.510267), false);
        plotPoint(new LatLng(41.075283, -81.510250), false);
        plotPoint(new LatLng(41.075283, -81.510250), false);
        plotPoint(new LatLng(41.075283, -81.510250), false);
        plotPoint(new LatLng(41.075283, -81.510250), false);
        plotPoint(new LatLng(41.075283, -81.510250), false);
        plotPoint(new LatLng(41.075283, -81.510250), false);
        plotPoint(new LatLng(41.075300, -81.510233), false);
        plotPoint(new LatLng(41.075300, -81.510233), false);
        plotPoint(new LatLng(41.075300, -81.510233), false);
        plotPoint(new LatLng(41.075300, -81.510233), false);
        plotPoint(new LatLng(41.075300, -81.510233), false);
        plotPoint(new LatLng(41.075300, -81.510217), false);
        plotPoint(new LatLng(41.075300, -81.510217), false);
        plotPoint(new LatLng(41.075300, -81.510217), false);
        plotPoint(new LatLng(41.075300, -81.510217), false);
        plotPoint(new LatLng(41.075300, -81.510217), false);
        plotPoint(new LatLng(41.075300, -81.510217), false);
        plotPoint(new LatLng(41.075300, -81.510200), false);
        plotPoint(new LatLng(41.075283, -81.510200), false);
        plotPoint(new LatLng(41.075283, -81.510200), false);
        plotPoint(new LatLng(41.075283, -81.510200), false);
        plotPoint(new LatLng(41.075283, -81.510183), false);
        plotPoint(new LatLng(41.075283, -81.510183), false);
        plotPoint(new LatLng(41.075283, -81.510183), false);
        plotPoint(new LatLng(41.075283, -81.510183), false);
        plotPoint(new LatLng(41.075283, -81.510183), false);
        plotPoint(new LatLng(41.075283, -81.510167), false);
        plotPoint(new LatLng(41.075283, -81.510167), false);
        plotPoint(new LatLng(41.075283, -81.510167), false);
        plotPoint(new LatLng(41.075283, -81.510167), false);
        plotPoint(new LatLng(41.075283, -81.510167), false);
        plotPoint(new LatLng(41.075283, -81.510150), false);
        plotPoint(new LatLng(41.075283, -81.510150), false);
        plotPoint(new LatLng(41.075283, -81.510150), false);
        plotPoint(new LatLng(41.075283, -81.510150), false);
        plotPoint(new LatLng(41.075283, -81.510133), false);
        plotPoint(new LatLng(41.075283, -81.510133), false);
        plotPoint(new LatLng(41.075283, -81.510133), false);
        plotPoint(new LatLng(41.075283, -81.510133), false);
        plotPoint(new LatLng(41.075283, -81.510133), false);
        plotPoint(new LatLng(41.075283, -81.510117), false);
        plotPoint(new LatLng(41.075283, -81.510117), false);
        plotPoint(new LatLng(41.075283, -81.510117), false);
        plotPoint(new LatLng(41.075283, -81.510117), false);
        plotPoint(new LatLng(41.075283, -81.510117), false);
        plotPoint(new LatLng(41.075283, -81.510100), false);
        plotPoint(new LatLng(41.075283, -81.510100), false);
        plotPoint(new LatLng(41.075283, -81.510100), false);
        plotPoint(new LatLng(41.075283, -81.510100), false);
        plotPoint(new LatLng(41.075283, -81.510083), false);
        plotPoint(new LatLng(41.075283, -81.510083), false);
        plotPoint(new LatLng(41.075283, -81.510083), false);
        plotPoint(new LatLng(41.075283, -81.510083), false);
        plotPoint(new LatLng(41.075283, -81.510083), false);
        plotPoint(new LatLng(41.075283, -81.510067), false);
        plotPoint(new LatLng(41.075283, -81.510067), false);
        plotPoint(new LatLng(41.075283, -81.510067), false);
        plotPoint(new LatLng(41.075267, -81.510067), false);
        plotPoint(new LatLng(41.075267, -81.510067), false);
        plotPoint(new LatLng(41.075267, -81.510050), false);
        plotPoint(new LatLng(41.075267, -81.510050), false);
        plotPoint(new LatLng(41.075267, -81.510050), false);
        plotPoint(new LatLng(41.075267, -81.510050), false);
        plotPoint(new LatLng(41.075267, -81.510050), false);
        plotPoint(new LatLng(41.075267, -81.510050), false);
        plotPoint(new LatLng(41.075267, -81.510033), false);
        plotPoint(new LatLng(41.075267, -81.510033), false);
        plotPoint(new LatLng(41.075267, -81.510033), false);
        plotPoint(new LatLng(41.075267, -81.510033), false);
        plotPoint(new LatLng(41.075267, -81.510033), false);
        plotPoint(new LatLng(41.075267, -81.510033), false);
        plotPoint(new LatLng(41.075267, -81.510033), false);
        plotPoint(new LatLng(41.075267, -81.510033), false);
        plotPoint(new LatLng(41.075267, -81.510033), false);
        plotPoint(new LatLng(41.075250, -81.510033), false);
        plotPoint(new LatLng(41.075250, -81.510033), false);
        plotPoint(new LatLng(41.075250, -81.510017), false);
        plotPoint(new LatLng(41.075250, -81.510017), false);
        plotPoint(new LatLng(41.075250, -81.510017), false);
        plotPoint(new LatLng(41.075250, -81.510017), false);
        plotPoint(new LatLng(41.075250, -81.510017), false);
        plotPoint(new LatLng(41.075250, -81.510017), false);
        plotPoint(new LatLng(41.075250, -81.510017), false);
        plotPoint(new LatLng(41.075250, -81.510017), false);
        plotPoint(new LatLng(41.075233, -81.510017), false);
        plotPoint(new LatLng(41.075233, -81.510000), false);
        plotPoint(new LatLng(41.075233, -81.510000), false);
        plotPoint(new LatLng(41.075233, -81.510000), false);
        plotPoint(new LatLng(41.075233, -81.510000), false);
        plotPoint(new LatLng(41.075233, -81.510000), false);
        plotPoint(new LatLng(41.075233, -81.510000), false);
        plotPoint(new LatLng(41.075233, -81.510000), false);
        plotPoint(new LatLng(41.075233, -81.510000), false);
        plotPoint(new LatLng(41.075233, -81.510000), false);
        plotPoint(new LatLng(41.075233, -81.509983), false);
        plotPoint(new LatLng(41.075233, -81.509983), false);
        plotPoint(new LatLng(41.075217, -81.509983), false);
        plotPoint(new LatLng(41.075217, -81.509983), false);
        plotPoint(new LatLng(41.075217, -81.509983), false);
        plotPoint(new LatLng(41.075217, -81.509983), false);
        plotPoint(new LatLng(41.075217, -81.509983), false);
        plotPoint(new LatLng(41.075217, -81.509967), false);
        plotPoint(new LatLng(41.075217, -81.509967), false);
        plotPoint(new LatLng(41.075217, -81.509967), false);
        plotPoint(new LatLng(41.075217, -81.509967), false);
        plotPoint(new LatLng(41.075217, -81.509967), false);
        plotPoint(new LatLng(41.075217, -81.509950), false);
        plotPoint(new LatLng(41.075200, -81.509950), false);
        plotPoint(new LatLng(41.075200, -81.509950), false);
        plotPoint(new LatLng(41.075200, -81.509950), false);
        plotPoint(new LatLng(41.075200, -81.509950), false);
        plotPoint(new LatLng(41.075200, -81.509950), false);
        plotPoint(new LatLng(41.075200, -81.509933), false);
        plotPoint(new LatLng(41.075200, -81.509933), false);
        plotPoint(new LatLng(41.075200, -81.509933), false);
        plotPoint(new LatLng(41.075200, -81.509933), false);
        plotPoint(new LatLng(41.075200, -81.509933), false);
        plotPoint(new LatLng(41.075200, -81.509933), false);
        plotPoint(new LatLng(41.075200, -81.509917), false);
        plotPoint(new LatLng(41.075200, -81.509917), false);
        plotPoint(new LatLng(41.075200, -81.509917), false);
        plotPoint(new LatLng(41.075200, -81.509917), false);
        plotPoint(new LatLng(41.075200, -81.509917), false);
        plotPoint(new LatLng(41.075200, -81.509900), false);
        plotPoint(new LatLng(41.075200, -81.509900), false);
        plotPoint(new LatLng(41.075183, -81.509900), false);
        plotPoint(new LatLng(41.075183, -81.509900), false);
        plotPoint(new LatLng(41.075183, -81.509900), false);
        plotPoint(new LatLng(41.075183, -81.509900), false);
        plotPoint(new LatLng(41.075183, -81.509900), false);
        plotPoint(new LatLng(41.075183, -81.509883), false);
        plotPoint(new LatLng(41.075183, -81.509883), false);
        plotPoint(new LatLng(41.075183, -81.509883), false);
        plotPoint(new LatLng(41.075183, -81.509883), false);
        plotPoint(new LatLng(41.075183, -81.509883), false);
        plotPoint(new LatLng(41.075183, -81.509867), false);
        plotPoint(new LatLng(41.075183, -81.509867), false);
        plotPoint(new LatLng(41.075183, -81.509867), false);
        plotPoint(new LatLng(41.075167, -81.509867), false);
        plotPoint(new LatLng(41.075167, -81.509867), false);
        plotPoint(new LatLng(41.075167, -81.509850), false);
        plotPoint(new LatLng(41.075167, -81.509850), false);
        plotPoint(new LatLng(41.075167, -81.509850), false);
        plotPoint(new LatLng(41.075167, -81.509850), false);
        plotPoint(new LatLng(41.075167, -81.509850), false);
        plotPoint(new LatLng(41.075167, -81.509850), false);
        plotPoint(new LatLng(41.075167, -81.509833), false);
        plotPoint(new LatLng(41.075167, -81.509833), false);
        plotPoint(new LatLng(41.075167, -81.509833), false);
        plotPoint(new LatLng(41.075150, -81.509833), false);
        plotPoint(new LatLng(41.075150, -81.509833), false);
        plotPoint(new LatLng(41.075150, -81.509833), false);
        plotPoint(new LatLng(41.075150, -81.509833), false);
        plotPoint(new LatLng(41.075150, -81.509833), false);
        plotPoint(new LatLng(41.075150, -81.509817), false);
        plotPoint(new LatLng(41.075150, -81.509817), false);
        plotPoint(new LatLng(41.075133, -81.509817), false);
        plotPoint(new LatLng(41.075133, -81.509817), false);
        plotPoint(new LatLng(41.075133, -81.509817), false);
        plotPoint(new LatLng(41.075133, -81.509817), false);
        plotPoint(new LatLng(41.075133, -81.509817), false);
        plotPoint(new LatLng(41.075133, -81.509817), false);
        plotPoint(new LatLng(41.075117, -81.509817), false);
        plotPoint(new LatLng(41.075117, -81.509817), false);
        plotPoint(new LatLng(41.075117, -81.509817), false);
        plotPoint(new LatLng(41.075117, -81.509817), false);
        plotPoint(new LatLng(41.075117, -81.509817), false);
        plotPoint(new LatLng(41.075117, -81.509817), false);
        plotPoint(new LatLng(41.075117, -81.509817), false);
        plotPoint(new LatLng(41.075100, -81.509817), false);
        plotPoint(new LatLng(41.075100, -81.509817), false);
        plotPoint(new LatLng(41.075100, -81.509833), false);
        plotPoint(new LatLng(41.075100, -81.509833), false);
        plotPoint(new LatLng(41.075100, -81.509833), false);
        plotPoint(new LatLng(41.075100, -81.509833), false);
        plotPoint(new LatLng(41.075100, -81.509833), false);
    }
}
