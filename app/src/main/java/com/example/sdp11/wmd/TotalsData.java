package com.example.sdp11.wmd;

/**
 * Created by nsanor on 2/10/2015.
 */
public class TotalsData {
    //Average distance, angle, sync counter to differentiate throws, etc.
    //Used as a global variable.
    //Create all as static variables.
    static private double averageDistance;
    static private double averageAngle;
    static private int syncCount;
    static private long lastThrowId;
    static private long lastHoleId;
    static private long lastGameId;

    public static void loadTotalsData(long throwId) {
        averageDistance = 10;
        lastGameId = 1;
        lastHoleId = 1;
        lastThrowId = throwId;
    }

    public static double getAverageDistance() {
        return averageDistance;
    }

    public static void setAverageDistance(double averageDistance) {
        TotalsData.averageDistance = averageDistance;
    }

    public static double getAverageAngle() {
        return averageAngle;
    }

    public static void setAverageAngle(double averageAngle) {
        TotalsData.averageAngle = averageAngle;
    }

    public static int getSyncCount() {
        return syncCount;
    }

    public static void setSyncCount(int syncCount) {
        TotalsData.syncCount = syncCount;
    }

    public static long getLastThrowId() {
        return lastThrowId;
    }

    public static void setLastThrowId(long lastThrowId) {
        TotalsData.lastThrowId = lastThrowId;
    }

    public static long getLastHoleId() {
        return lastHoleId;
    }

    public static void setLastHoleId(long lastHoleId) {
        TotalsData.lastHoleId = lastHoleId;
    }

    public static long getLastGameId() {
        return lastGameId;
    }

    public static void setLastGameId(long lastGameId) {
        TotalsData.lastGameId = lastGameId;
    }
}
