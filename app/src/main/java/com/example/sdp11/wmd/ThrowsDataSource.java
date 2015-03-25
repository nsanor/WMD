package com.example.sdp11.wmd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Student on 1/27/2015.
 */
public class ThrowsDataSource {
    private final static String TAG = ThrowsDataSource.class.getSimpleName();
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    private String[] allColumns = { DBHelper.COLUMN_THROW_ID,
            DBHelper.COLUMN_HOLE_ID,
            DBHelper.COLUMN_GAME_ID,
            DBHelper.COLUMN_INITIAL_DIRECTION,
            DBHelper.COLUMN_FINAL_DIRECTION,
            DBHelper.COLUMN_TOTAL_DISTANCE,
            DBHelper.COLUMN_THROW_QUALITY,
            DBHelper.COLUMN_TOTAL_TIME,
            DBHelper.COLUMN_SYNC_TIME};

    private String[] totalsColumns = { DBHelper.COLUMN_HOLE_ID,
            DBHelper.COLUMN_GAME_ID,
            DBHelper.COLUMN_AVERAGE_DISTANCE,
            DBHelper.COLUMN_AVERAGE_ANGLE,
            DBHelper.COLUMN_AVERAGE_TIME,
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

    public void createThrow(double initial_direction, double final_direction, double total_distance, double throw_quality, long total_time) {

        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_HOLE_ID, TotalsData.getHoleId());
        values.put(DBHelper.COLUMN_GAME_ID, TotalsData.getGameId());
        values.put(DBHelper.COLUMN_INITIAL_DIRECTION, initial_direction);
        values.put(DBHelper.COLUMN_FINAL_DIRECTION, final_direction);
        values.put(DBHelper.COLUMN_TOTAL_DISTANCE, total_distance);
        values.put(DBHelper.COLUMN_THROW_QUALITY, throw_quality);
        values.put(DBHelper.COLUMN_TOTAL_TIME, total_time);

        //Calculate unix time from current time
        long now = System.currentTimeMillis();
        values.put(DBHelper.COLUMN_SYNC_TIME, now);

        long insertId = database.insert(DBHelper.TABLE_THROWS, null,values);
    }

    public void deleteThrow(RawThrowData t) {
        long id = t.getThrowId();
        database.delete(DBHelper.TABLE_THROWS, DBHelper.COLUMN_THROW_ID
                + " = " + id, null);
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

    public void loadTotalsData() {
        Cursor c = database.query(DBHelper.TABLE_TOTALS,
                totalsColumns, null, null, null, null, null);
        c.moveToFirst();
        if(c.getCount() > 0){
            TotalsData.setHoleId(c.getLong(0));
            TotalsData.setGameId(c.getLong(1));
            TotalsData.setAverageDistance(c.getDouble(2));
            TotalsData.setAverageAngle(c.getDouble(3));
            TotalsData.setAverageTime(c.getDouble(4));
            TotalsData.setThrowCount(c.getInt(5));
        }
        else {
            Log.e(TAG, "Cursor empty");
            TotalsData.setHoleId(1);
            TotalsData.setGameId(1);
            TotalsData.setAverageDistance(0);
            TotalsData.setAverageAngle(0);
            TotalsData.setAverageTime(0);
            TotalsData.setThrowCount(0);
        }
        c.close();
    }

    public void writeTotalsData() {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_HOLE_ID, TotalsData.getHoleId());
        values.put(DBHelper.COLUMN_GAME_ID, TotalsData.getGameId());
        values.put(DBHelper.COLUMN_AVERAGE_DISTANCE, TotalsData.getAverageDistance());
        values.put(DBHelper.COLUMN_AVERAGE_ANGLE, TotalsData.getAverageAngle());
        values.put(DBHelper.COLUMN_AVERAGE_TIME, TotalsData.getAverageTime());
        values.put(DBHelper.COLUMN_THROW_COUNT, TotalsData.getThrowCount());

        long insertId = database.insert(DBHelper.TABLE_TOTALS, null,values);
    }

    private ThrowData cursorToThrow(Cursor cursor) {
        ThrowData t = new ThrowData();
        t.setThrowId(cursor.getLong(0));
        t.setHoleId(cursor.getLong(1));
        t.setGameId(cursor.getLong(2));
        t.setInitialDirection(cursor.getDouble(3));
        t.setFinalDirection(cursor.getDouble(4));
        t.setThrowQuality(cursor.getDouble(5));
        t.setTotalDistance(cursor.getDouble(6));
        t.setTotalTime(cursor.getInt(7));
        t.setSyncTime(cursor.getInt(8));
        return t;
    }

    public long getMaxThrowId() {
        Cursor c = database.rawQuery("SELECT MAX(?) FROM " + DBHelper.TABLE_THROWS, new String[] {"throw_id"});
        c.moveToFirst();
        return c.getInt(0);
    }
}
