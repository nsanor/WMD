package com.example.sdp11.wmd;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class ThrowData implements Parcelable {
    private long throwId;
    private long gameId;
    private double totalDistance;
    private double totalAngle;
    private double syncTime;

    public double getTotalAngle() {
        return totalAngle;
    }

    public ThrowData(){}

    public ThrowData(Parcel in) {
        String[] data = new String[5];

        in.readStringArray(data);
        this.throwId = Long.valueOf(data[0]);
        this.gameId = Long.valueOf(data[1]);
        this.totalDistance = Double.valueOf(data[2]);
        this.totalAngle = Double.valueOf(data[3]);
        this.syncTime = Double.valueOf(data[4]);
        Log.e("Throw data", data[4]);
    }

    public long getThrowId() {
        return throwId;
    }

    public void setThrowId(long throwId) {
        this.throwId = throwId;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    double degreesToRadians(double degrees) {
        return degrees*(Math.PI/180);
    }

    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double r = 3963.191;
        double lat1rad = degreesToRadians(lat1);
        double lat2rad = degreesToRadians(lat2);
        double long1rad = degreesToRadians(lng1);
        double long2rad = degreesToRadians(lng2);
        double diffLong = long2rad - long1rad;
        double e = Math.acos(Math.sin(lat1rad)*Math.sin(lat2rad) + Math.cos(lat1rad)*Math.cos(lat2rad)*Math.cos(diffLong));
        return r * e;
    }

    //Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return throwId + " | " + totalDistance + " | " + totalAngle;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public void setTotalAngle(double totalAngle) {
        this.totalAngle = totalAngle;
    }

    public double getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(double syncTime) {
        this.syncTime = syncTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {String.valueOf(this.throwId),
                String.valueOf(this.gameId),
                String.valueOf(this.totalDistance),
                String.valueOf(this.totalAngle),
                String.valueOf(this.syncTime)});
        Log.e("Throw data", String.valueOf(this.syncTime));
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ThrowData createFromParcel(Parcel in) {
            return new ThrowData(in);
        }

        public ThrowData[] newArray(int size) {
            return new ThrowData[size];
        }
    };
}
