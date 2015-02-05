package com.example.sdp11.wmd;

/**
 * Created by Student on 1/27/2015.
 */
public class RawThrowData {
    private long throwId;
    private long holeId;
    private long gameId;
    private double startLat;
    private double startLong;
    private double endLat;
    private double endLong;
    private double startXAccel;
    private double startYAccel;
    private double startTime;
    private double endTime;

    public double getEndTime() {
        return endTime;
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public long getThrowId() {
        return throwId;
    }

    public void setThrowId(long throw_id) {
        this.throwId = throw_id;
    }

    public long getHoleId() {
        return holeId;
    }

    public void setHoleId(long hole_id) {
        this.holeId = hole_id;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long game_id) {
        this.gameId = game_id;
    }

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double start_lat) {
        this.startLat = start_lat;
    }

    public double getStartLong() {
        return startLong;
    }

    public void setStartLong(double start_long) {
        this.startLong = start_long;
    }

    public double getEndLat() {
        return endLat;
    }

    public void setEndLat(double end_lat) {
        this.endLat = end_lat;
    }

    public double getEndLong() {
        return endLong;
    }

    public void setEndLong(double end_long) {
        this.endLong = end_long;
    }

    public double getStartXAccel() {
        return startXAccel;
    }

    public void setStartXAccel(double start_x_accel) {
        this.startXAccel = start_x_accel;
    }

    public double getStartYAccel() {
        return startYAccel;
    }

    public void setStartYAccel(double start_y_accel) {
        this.startYAccel = start_y_accel;
    }

    public String getAllFields() {
        return throwId + ", " + holeId + ", " + gameId + ", " + startLat + ", " + startLong + ", " + startXAccel + ", " + startYAccel + ", " + endLat + ", " + endLong;
    }

    //Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return getAllFields();
    }
}
