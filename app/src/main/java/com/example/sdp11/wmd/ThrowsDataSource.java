package com.example.sdp11.wmd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ThrowsDataSource {
    private final static String TAG = ThrowsDataSource.class.getSimpleName();
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    private String[] allColumns = { DBHelper.COLUMN_THROW_ID,
            DBHelper.COLUMN_GAME_ID,
            DBHelper.COLUMN_TOTAL_DISTANCE,
            DBHelper.COLUMN_TOTAL_ANGLE,
            DBHelper.COLUMN_SYNC_TIME};

    private String[] totalsColumns = { DBHelper.COLUMN_GAME_ID,
            DBHelper.COLUMN_AVERAGE_DISTANCE,
            DBHelper.COLUMN_AVERAGE_ANGLE,
            DBHelper.COLUMN_THROW_COUNT};

    public ThrowsDataSource(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
        database.close();
    }

    public void createThrow(double total_distance, double total_angle) {

        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_GAME_ID, TotalsData.getGameId());
        values.put(DBHelper.COLUMN_TOTAL_DISTANCE, total_distance);
        values.put(DBHelper.COLUMN_TOTAL_ANGLE, total_angle);

        //Calculate GPS time from current time
        String now = getCurrentTime();
        values.put(DBHelper.COLUMN_SYNC_TIME, now);

        database.insert(DBHelper.TABLE_THROWS, null,values);
    }

    private String getCurrentTime() {
        Calendar now = Calendar.getInstance();
        String leadingHour = "";
        String leadingMinute = "";
        String leadingSecond = "";

        int hour = now.get(Calendar.HOUR);
        if(hour < 10) leadingHour = "0";

        int minute = now.get(Calendar.MINUTE);
        if(minute < 10) leadingMinute = "0";

        int second = now.get(Calendar.SECOND);
        if(second < 1) leadingSecond = "00";
        if(second < 10) leadingSecond = "0";

        int millisecond = now.get(Calendar.MILLISECOND);

        String currentTime = leadingHour + hour + leadingMinute + minute + leadingSecond + second + "." + millisecond;
        Log.e(TAG, currentTime);
        return currentTime;
    }

    public void deleteAllThrows() {
        //database.rawQuery("delete from sqlite_sequence where name = 'throws'", null);
        database.delete(DBHelper.TABLE_THROWS, null,null);
    }

    public List<ThrowData> getAllThrows(long gameId) {
        List<ThrowData> ts = new ArrayList<ThrowData>();

        Cursor cursor = database.query(DBHelper.TABLE_THROWS,
                allColumns, DBHelper.COLUMN_GAME_ID + " = " + gameId, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ThrowData t = cursorToThrow(cursor);
            ts.add(t);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return ts;
    }

    public void deleteTotals() {
        //database.rawQuery("delete from sqlite_sequence where name = 'throws'", null);
        database.delete(DBHelper.TABLE_TOTALS, null,null);
    }

    public void loadTotalsData() {

        Cursor c = database.query(DBHelper.TABLE_TOTALS,
                totalsColumns, null, null, null, null, null);
        if(!c.moveToFirst()) {
            Log.e(TAG, "Cursor empty");
            TotalsData.setGameId(1);
            TotalsData.setAverageDistance(10);
            TotalsData.setAverageAngle(0);
            TotalsData.setThrowCount(1);
        }
        else{
            Log.e(TAG, c.getLong(0) + ", " + c.getDouble(1)+ ", " + c.getDouble(2) + ", " + c.getInt(3));
            TotalsData.setGameId(c.getLong(0));
            TotalsData.setAverageDistance(c.getDouble(1));
            TotalsData.setAverageAngle(c.getDouble(2));
            TotalsData.setThrowCount(c.getInt(3));
        }

        c.close();
    }

    public void writeTotalsData() {
        deleteTotals();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_GAME_ID, TotalsData.getGameId());
        values.put(DBHelper.COLUMN_AVERAGE_DISTANCE, TotalsData.getAverageDistance());
        values.put(DBHelper.COLUMN_AVERAGE_ANGLE, TotalsData.getAverageAngle());
        values.put(DBHelper.COLUMN_THROW_COUNT, TotalsData.getThrowCount());

        database.insert(DBHelper.TABLE_TOTALS, null,values);
    }

    private ThrowData cursorToThrow(Cursor cursor) {
        ThrowData t = new ThrowData();
        t.setThrowId(cursor.getLong(0));
        t.setGameId(cursor.getLong(1));
        t.setTotalDistance(cursor.getDouble(2));
        t.setTotalAngle(cursor.getDouble(3));
        t.setSyncTime(cursor.getDouble(4));
        return t;
    }

//    public boolean isThrowsEmpty(long gameId) {
//        Cursor cursor = database.query(DBHelper.TABLE_THROWS,
//                allColumns, DBHelper.COLUMN_GAME_ID + " = " + gameId, null, null, null, null);
//
//        return !cursor.moveToFirst();
//    }
//
//    public long getMaxThrowId() {
//        Cursor c = database.rawQuery("SELECT MAX(?) FROM " + DBHelper.TABLE_THROWS, new String[] {"throw_id"});
//        c.moveToFirst();
//        return c.getInt(0);
//    }
//
//    public long getMaxThrowCount() {
//        Cursor c = database.rawQuery("SELECT MAX(?) FROM " + DBHelper.TABLE_TOTALS, new String[] {"throw_count"});
//        c.moveToFirst();
//        return c.getInt(0);
//    }
}
