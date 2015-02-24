package com.example.sdp11.wmd;

/**
 * Created by nsanor on 2/10/2015.
 */
public class IMUDataString {
    private long throwId;
    private String IMUString;

    public IMUDataString(String IMUString) {
        this.throwId = TotalsData.getLastThrowId();
        this.IMUString = IMUString;
    }

    public long getThrowId() {
        return throwId;
    }

    public void setThrowId(long throwId) {
        this.throwId = throwId;
    }

    public String getIMUString() {
        return IMUString;
    }

    public void setIMUString(String IMUString) {
        this.IMUString = IMUString;
    }
}
