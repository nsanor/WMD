package com.example.sdp11.wmd;

/**
 * Created by Student on 1/27/2015.
 */
public class Throw {
    private long throw_id;
    private long hole_id;
    private long game_id;
    private double start_lat;
    private double start_long;
    private double end_lat;
    private double end_long;
    private double start_x_accel;
    private double start_y_accel;

    public long getThrowId() {
        return throw_id;
    }

    public void setThrowId(long throw_id) {
        this.throw_id = throw_id;
    }

    public long getHoleId() {
        return hole_id;
    }

    public void setHoleId(long hole_id) {
        this.hole_id = hole_id;
    }

    public long getGameId() {
        return game_id;
    }

    public void setGameId(long game_id) {
        this.game_id = game_id;
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

    public String getAllFields() {
        return throw_id + ", " + hole_id + ", " + game_id + ", " + start_lat + ", " + start_long + ", " + start_x_accel + ", " + start_y_accel + ", " + end_lat + ", " + end_long;
    }

    //Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return getAllFields();
    }
}
