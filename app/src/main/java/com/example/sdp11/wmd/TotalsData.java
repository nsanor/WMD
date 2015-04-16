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
    static private int throwCount;
    static private long throwId;
    static private long gameId;

    //These are to tell the difference between holes and games.
    private static double lastLat;
    private static double lastLong;

//    public static void loadTotalsData(long throwId) {
//        averageDistance = 10;
//        gameId = 1;
//        holeId = 1;
//        TotalsData.throwId = throwId;
//    }

    public static void resetData() {
        averageDistance = 10;
        averageAngle = 0;
        throwCount = 1;
        throwId = 1;
        gameId = 1;
    }

    public static void updateThrowCount(){
        TotalsData.throwCount++;
    }

    public static void updateGameId(){
        TotalsData.gameId++;
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

    public static int getThrowCount() {
        return throwCount;
    }

    public static void setThrowCount(int throwCount) {
        TotalsData.throwCount = throwCount;
    }

    public static long getThrowId() {
        return throwId;
    }

    public static void setThrowId(long throwId) {
        TotalsData.throwId = throwId;
    }

    public static long getGameId() {
        return gameId;
    }

    public static void setGameId(long gameId) {
        TotalsData.gameId = gameId;
    }

    public static double getLastLat() {
        return lastLat;
    }

    public static void setLastLat(double lastLat) {
        TotalsData.lastLat = lastLat;
    }

    public static double getLastLong() {
        return lastLong;
    }

    public static void setLastLong(double lastLong) {
        TotalsData.lastLong = lastLong;
    }

}
