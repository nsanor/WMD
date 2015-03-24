package com.example.sdp11.wmd;

/**
 * Created by nsanor on 2/10/2015.
 */
public class GPSDataPoint {
    private long throwId;
    private double latitude;
    private double longitude;
    private long time;

    public GPSDataPoint(double latitude, double longitude, long time) {
        this.throwId = TotalsData.getThrowId();
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getTime() {
        return time;
    }
}
