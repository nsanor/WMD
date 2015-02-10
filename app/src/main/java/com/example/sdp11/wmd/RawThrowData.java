package com.example.sdp11.wmd;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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
    private long startTime;
    private long endTime;
    private long syncTime;

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
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

    public long getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(long syncTime) {
        this.syncTime = syncTime;
    }

    //Convert from epoch to string
    public String convertDate(long d) {
        Date date = new Date(d);
        DateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        String formatted = format.format(date);
        return formatted;
    }

    public String getAllFields() {
        return String.valueOf(syncTime) + ", "  + convertDate(syncTime);//throwId + ", " + holeId + ", " + gameId + ", " + startLat + ", " + startLong + ", " + startXAccel + ", " + startYAccel + ", " + endLat + ", " + endLong;
    }

    //Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return getAllFields();
    }

}
