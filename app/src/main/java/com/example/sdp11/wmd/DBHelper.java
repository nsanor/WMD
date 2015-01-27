package com.example.sdp11.wmd;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Student on 1/27/2015.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String TABLE_THROWS = "throws";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_START_LAT = "starting_latitude";
    public static final String COLUMN_START_LONG = "starting_longitude";
    public static final String COLUMN_END_LAT = "ending_latitude";
    public static final String COLUMN_END_LONG = "ending_longitude";
    public static final String COLUMN_START_ACCEL_X = "starting_x_acceleration";
    public static final String COLUMN_START_ACCEL_Y = "starting_y_acceleration";

    private static final String DATABASE_NAME = "throws.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_THROWS + "(" + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_START_LAT + " double not null,"
            + COLUMN_START_LONG + "double not null,"
            + COLUMN_END_LAT + "double not null,"
            + COLUMN_END_LONG + "double not null,"
            + COLUMN_START_ACCEL_X + "double not null,"
            + COLUMN_START_ACCEL_Y + "double not null,"
            ;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_THROWS);
        onCreate(db);
    }
}
