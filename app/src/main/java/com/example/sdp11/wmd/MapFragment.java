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
            }
        });

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mapView.getMap();
        Location mCurrentLocation = MainActivity.getmCurrentLocation();



        // latitude and longitude
        if(mCurrentLocation != null) {
            latitude = mCurrentLocation.getLatitude();
            longitude = mCurrentLocation.getLongitude();
        }

        MarkerOptions locMarker = new MarkerOptions().position(new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)).title("You are here");
        googleMap.addMarker(locMarker);

        //calculateBounds();
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                //lstLatLngs.add(point);
                if(planning) {
                    Marker marker = plotUserPoint(point);
                    markerStack.push(marker);
                }
            }
        });


        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(new LatLng(latitude, longitude)).zoom(14).build();
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(googleMap.getMaxZoomLevel() - 2).build();
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
}
