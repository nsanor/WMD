package com.example.sdp11.wmd;

import android.location.Location;

/**
 * Created by nsanor on 2/10/2015.
 */
public class GPSDataPoint {
    private long throwId;
    private double latitude;
    private double longitude;
    private double time;
    private Location loc;

    public GPSDataPoint(double latitude, double longitude) {
        this.throwId = TotalsData.getThrowId();
        this.latitude = latitude;
        this.longitude = longitude;
        this.loc = new Location("");
        this.loc.setLatitude(latitude);
        this.loc.setLongitude(longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getTime() {
        return time;
    }

    public Location getLoc() {
        return loc;
    }
}
