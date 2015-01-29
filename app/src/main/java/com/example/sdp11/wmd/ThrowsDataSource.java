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
    private String[] allColumns = { DBHelper.COLUMN_ID,
            DBHelper.COLUMN_START_LAT,
            DBHelper.COLUMN_START_LONG,
            DBHelper.COLUMN_END_LAT,
            DBHelper.COLUMN_END_LONG,
            DBHelper.COLUMN_START_ACCEL_X,
            DBHelper.COLUMN_START_ACCEL_Y};

    public ThrowsDataSource(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Throw createThrow(double start_lat, double start_long, double end_lat, double end_long, double start_x_accel, double start_y_accel) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_START_LAT, start_lat);
        values.put(DBHelper.COLUMN_START_LONG, start_long);
        values.put(DBHelper.COLUMN_END_LAT, end_lat);
        values.put(DBHelper.COLUMN_END_LONG, end_long);
        values.put(DBHelper.COLUMN_START_ACCEL_X, start_x_accel);
        values.put(DBHelper.COLUMN_START_ACCEL_Y, start_y_accel);

        long insertId = database.insert(DBHelper.TABLE_THROWS, null,
                values);

        Cursor cursor = database.query(DBHelper.TABLE_THROWS,
                allColumns, DBHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Throw newThrow = cursorToThrow(cursor);
        cursor.close();
        return newThrow;

    }

    public void deleteThrow(Throw t) {
        long id = t.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(DBHelper.TABLE_THROWS, DBHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Throw> getAllThrows() {
        List<Throw> ts = new ArrayList<Throw>();

        Cursor cursor = database.query(DBHelper.TABLE_THROWS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Throw t = cursorToThrow(cursor);
            ts.add(t);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return ts;
    }

    private Throw cursorToThrow(Cursor cursor) {
        Throw t = new Throw();
        t.setId(cursor.getLong(0));
        t.setStartLat(cursor.getDouble(1));
        t.setStartLong(cursor.getDouble(2));
        t.setEndLat(cursor.getDouble(3));
        t.setEndLong(cursor.getDouble(4));
        t.setStartXAccel(cursor.getDouble(5));
        t.setStartYAccel(cursor.getDouble(6));
        return t;
    }

}
