package com.example.sdp11.wmd;

/**
 * Created by Student on 1/27/2015.
 */
public class Throw {
    private long id;
    private double start_lat;
    private double start_long;
    private double end_lat;
    private double end_long;
    private double start_x_accel;
    private double start_y_accel;

    Throw() {
        id = -1;
        start_lat = -1;
        start_long = -1;
        end_lat = -1;
        end_long = -1;
        start_x_accel = -1;
        start_y_accel = -1;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getStartLat() {
        return start_lat;
    }

    public void setStartLat(double start_lat) {
        this.start_lat = start_lat;
    }

    public double getStartLong() {
        return start_long;
    }

    public void setStartLong(double start_long) {
        this.start_long = start_long;
    }

    public double getEndLat() {
        return end_lat;
    }

    public void setEndLat(double end_lat) {
        this.end_lat = end_lat;
    }

    public double getEndLong() {
        return end_long;
    }

    public void setEndLong(double end_long) {
        this.end_long = end_long;
    }

    public double getStartXAccel() {
        return start_x_accel;
    }

    public void setStartXAccel(double start_x_accel) {
        this.start_x_accel = start_x_accel;
    }

    public double getStartYAccel() {
        return start_y_accel;
    }

    public void setStartYAccel(double start_y_accel) {
        this.start_y_accel = start_y_accel;
    }

    //Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return "test";
    }
}
