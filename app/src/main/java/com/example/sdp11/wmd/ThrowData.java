package com.example.sdp11.wmd;

/**
 * Created by Student on 2/5/2015.
 */
public class ThrowData {
    private long throwId;
    private long holeId;
    private long gameId;
    private double initialDirection;
    private double finalDirection;
    private double throwIntegrity;
    private double totalDistance;
    private long totalTime;
    private long syncTime;

    public ThrowData() {

    }

    public ThrowData(long throwId, double start_lat, double start_long, double end_lat, double end_long, double start_x_accel, double start_y_accel, long startTime, long endTime) {
        this.throwId =throwId;
        this.totalDistance = calculateDistance(start_lat, start_long, end_lat, end_long);
        this.totalTime = endTime - startTime;
        this.throwIntegrity = 1;
    }

    public ThrowData(RawThrowData t) {
        this.throwId = t.getThrowId();
        this.totalDistance = calculateDistance(t.getStartLat(), t.getStartLong(), t.getEndLat(), t.getEndLong());
        this.totalTime = t.getEndTime() - t.getStartTime();
        this.throwIntegrity = 1;
    }

    public long getThrowId() {
        return throwId;
    }

    public void setThrowId(long throwId) {
        this.throwId = throwId;
    }

    public double getInitialDirection() {
        return initialDirection;
    }

    public void setInitialDirection(double initialDirection) {
        this.initialDirection = initialDirection;
    }

    public double getFinalDirection() {
        return finalDirection;
    }

    public void setFinalDirection(double finalDirection) {
        this.finalDirection = finalDirection;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getThrowIntegrity() {
        return throwIntegrity;
    }

    public void setThrowIntegrity(double throwIntegrity) {
        this.throwIntegrity = throwIntegrity;
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
        return throwId + " | " + totalDistance + " | " + throwIntegrity;
    }

    public long getHoleId() {
        return holeId;
    }

    public void setHoleId(long holeId) {
        this.holeId = holeId;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public long getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(long syncTime) {
        this.syncTime = syncTime;
    }
}
