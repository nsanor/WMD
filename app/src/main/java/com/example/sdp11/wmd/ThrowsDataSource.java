package com.example.sdp11.wmd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Student on 1/27/2015.
 */
public class ThrowsDataSource {
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

    public ThrowsDataSource(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createThrow(long hole_id, long game_id, double initial_direction, double final_direction, double total_distance, double throw_quality, long total_time) {

        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_HOLE_ID, 1);
        values.put(DBHelper.COLUMN_GAME_ID, 1);
        values.put(DBHelper.COLUMN_INITIAL_DIRECTION, initial_direction);
        values.put(DBHelper.COLUMN_FINAL_DIRECTION, final_direction);
        values.put(DBHelper.COLUMN_TOTAL_DISTANCE, total_distance);
        values.put(DBHelper.COLUMN_THROW_QUALITY, throw_quality);
        values.put(DBHelper.COLUMN_TOTAL_TIME, total_time);

        //Calculate unix time from current time
        long now = System.currentTimeMillis();
        values.put(DBHelper.COLUMN_SYNC_TIME, now);

        long insertId = database.insert(DBHelper.TABLE_THROWS, null,values);

        //Need to get new max throw id to create calc data
//        Log.i("TEST", database.toString());
//        Log.i("ID Test", String.valueOf(getMaxThrowId(database)));
        //Create new entry in ThrowData here.
        //ThrowData calc = new ThrowData(throwId, start_lat, double start_long, double end_lat, double end_long, double start_x_accel, double start_y_accel, long startTime, long endTime);

        //Update globals in TotalsData here.

//        Cursor cursor = database.query(DBHelper.TABLE_THROWS,
//                rawColumns, DBHelper.COLUMN_ID + " = " + insertId, null,
//                null, null, null);
//        cursor.moveToFirst();
//        Throw newThrow = cursorToRawThrow(cursor);
//        cursor.close();
//        return newThrow;



    }

    public void deleteThrow(RawThrowData t) {
        long id = t.getThrowId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(DBHelper.TABLE_THROWS, DBHelper.COLUMN_THROW_ID
                + " = " + id, null);
    }

    public void deleteAllThrows() {
        //database.rawQuery("delete from sqlite_sequence where name = 'throws'", null);
        database.delete(DBHelper.TABLE_THROWS, null,null);
    }

    public List<ThrowData> getAllThrows() {
        List<ThrowData> ts = new ArrayList<ThrowData>();

        Cursor cursor = database.query(DBHelper.TABLE_THROWS,
                allColumns, null, null, null, null, null);

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
