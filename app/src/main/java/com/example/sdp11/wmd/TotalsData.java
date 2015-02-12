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
}
