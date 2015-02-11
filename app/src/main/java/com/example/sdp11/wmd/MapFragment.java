package com.example.sdp11.wmd;


import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {

    double latitude = 41.13747;
    double longitude = -81.47430700000001;
    LatLngBounds bounds;

    private GoogleMap googleMap;

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        // Gets the MapView from the XML layout and creates it
        MapView mapView = (MapView) view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        mapView.onResume();

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

        calculateBounds();

        //test data
//        plotPoint(mCurrentLocation.getLatitude() + 0.5, mCurrentLocation.getLongitude() + 0.5);
//        plotPoint(mCurrentLocation.getLatitude() - 0.5, mCurrentLocation.getLongitude() - 0.5);

        plotPoint(latitude, longitude);
        plotRadius(latitude, longitude, 1000);

        //Need ability to disable this when needed.
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                //lstLatLngs.add(point);
                googleMap.addMarker(new MarkerOptions().position(point));
            }
        });

        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(13).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        //googleMap.animateCamera(cu);

        return view;
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

    private void plotPoint(double lat, double lng) {
        // create marker
        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(lat, lng));

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        googleMap.addMarker(marker);
    }

    private void plotRadius(double lat, double lng, double radius) {
        // Instantiates a new CircleOptions object and defines the center and radius
        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(lat, lng))
                .radius(radius); // In meters

// Get back the mutable Circle
        Circle circle = googleMap.addCircle(circleOptions);
    }
}
