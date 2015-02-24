package com.example.sdp11.wmd;

import com.google.android.gms.internal.ge;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by nsanor on 2/10/2015.
 */
public class GPSDataPoint {
    private long throwId;
    private double latitude;
    private double longitude;
    private long time;

    public GPSDataPoint(double latitude, double longitude, long time) {
        this.throwId = TotalsData.getLastThrowId();
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }
}
