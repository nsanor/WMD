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

    public RawThrowData() {}

    public RawThrowData(long throwId, long holeId, long gameId, double startLat, double startLong, double endLat, double endLong, double startXAccel, double startYAccel, long startTime, long endTime, long syncTime) {
        this.throwId = throwId;
        this.holeId = holeId;
        this.gameId = gameId;
        this.startLat = startLat;
        this.startLong = startLong;
        this.endLat = endLat;
        this.endLong = endLong;
        this.startXAccel = startXAccel;
        this.startYAccel = startYAccel;
        this.startTime = startTime;
        this.endTime = endTime;
        this.syncTime = syncTime;
    }

    public double[] parseGPS(String GPSData) {
        String gps[] = GPSData.split(",");
        double time, latDeg, latMin, latitude, lonDeg, lonMin, longitude;
        longitude = latitude = -1;
        if ((gps[0].equals("$GPRMC")) && (gps[7] != null)) {
            time = Double.parseDouble(gps[1]);
            latDeg =Double.parseDouble(gps[3].substring(0, 2));
            latMin =Double.parseDouble(gps[3].substring(2, 8));
            latitude = latDeg + (latMin / 60);
            if (gps[4].equals(String.valueOf('S'))) latitude = -1 * latitude;
            lonDeg =Double.parseDouble(gps[5].substring(0, 3));
            lonMin =Double.parseDouble(gps[5].substring(3, 9));
            longitude = lonDeg + (lonMin / 60);
            if (gps[6].equals(String.valueOf('W'))) longitude = -1 * longitude;
        }

        double[] retvals = {latitude, longitude};
        return retvals;
    }


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
        return throwId + ", " + holeId + ", " + gameId + ", " + startLat + ", " + startLong + ", " + startXAccel + ", " + startYAccel + ", " + endLat + ", " + endLong;
    }

    //Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return getAllFields();
    }

}
