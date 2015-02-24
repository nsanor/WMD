package com.example.sdp11.wmd;

/**
 * Created by nsanor on 2/10/2015.
 */
public class GPSDataString {
    private long throwId;
    private String GPSString;

    public GPSDataString(String GPSString) {
        this.throwId = TotalsData.getLastThrowId();
        this.GPSString = GPSString;
    }

    public long getThrowId() {
        return throwId;
    }

    public void setThrowId(long throwId) {
        this.throwId = throwId;
    }

    public String getGPSString() {
        return GPSString;
    }

    public void setGPSString(String GPSString) {
        this.GPSString = GPSString;
    }
}
