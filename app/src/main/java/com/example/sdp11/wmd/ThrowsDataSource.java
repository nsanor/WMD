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
            DBHelper.COLUMN_START_LAT,
            DBHelper.COLUMN_START_LONG,
            DBHelper.COLUMN_END_LAT,
            DBHelper.COLUMN_END_LONG,
            DBHelper.COLUMN_START_ACCEL_X,
            DBHelper.COLUMN_START_ACCEL_Y};

    private String[] mainColumns = { DBHelper.COLUMN_THROW_ID,
            DBHelper.COLUMN_INITIAL_DIRECTION,
            DBHelper.COLUMN_FINAL_DIRECTION,
            DBHelper.COLUMN_TOTAL_DISTANCE,
            DBHelper.COLUMN_THROW_INTEGRITY,
            DBHelper.COLUMN_TOTAL_TIME};

    public ThrowsDataSource(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createThrow(long hole_id, long game_id, double start_lat, double start_long, double end_lat, double end_long, double start_x_accel, double start_y_accel) {
//
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_HOLE_ID, start_lat);
        values.put(DBHelper.COLUMN_GAME_ID, start_lat);
        values.put(DBHelper.COLUMN_START_LAT, start_lat);
        values.put(DBHelper.COLUMN_START_LONG, start_long);
        values.put(DBHelper.COLUMN_END_LAT, end_lat);
        values.put(DBHelper.COLUMN_END_LONG, end_long);
        values.put(DBHelper.COLUMN_START_ACCEL_X, start_x_accel);
        values.put(DBHelper.COLUMN_START_ACCEL_Y, start_y_accel);

        long insertId = database.insert(DBHelper.TABLE_THROWS, null,values);

//        Cursor cursor = database.query(DBHelper.TABLE_THROWS,
//                allColumns, DBHelper.COLUMN_ID + " = " + insertId, null,
//                null, null, null);
//        cursor.moveToFirst();s
//        Throw newThrow = cursorToThrow(cursor);
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

    public List<RawThrowData> getAllThrows() {
        List<RawThrowData> ts = new ArrayList<RawThrowData>();

        Cursor cursor = database.query(DBHelper.TABLE_THROWS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            RawThrowData t = cursorToThrow(cursor);
            ts.add(t);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return ts;
    }

    public List<CalculatedThrowData> getAllThrowsShort() {
        List<CalculatedThrowData> ts = new ArrayList<CalculatedThrowData>();

        Cursor cursor = database.query(DBHelper.TABLE_CALC,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
//        while (!cursor.isAfterLast()) {
//            RawThrowData t = cursorToThrow(cursor);
//            ts.add(t);
//            cursor.moveToNext();
//        }
        // make sure to close the cursor
        cursor.close();
        return ts;
    }

    private RawThrowData cursorToThrow(Cursor cursor) {
        RawThrowData t = new RawThrowData();
        t.setThrowId(cursor.getLong(0));
        t.setHoleId(cursor.getLong(1));
        t.setGameId(cursor.getLong(2));
        t.setStartLat(cursor.getDouble(3));
        t.setStartLong(cursor.getDouble(4));
        t.setEndLat(cursor.getDouble(5));
        t.setEndLong(cursor.getDouble(6));
        t.setStartXAccel(cursor.getDouble(7));
        t.setStartYAccel(cursor.getDouble(8));
        return t;
    }

}
