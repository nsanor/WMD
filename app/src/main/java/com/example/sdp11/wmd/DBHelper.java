package com.example.sdp11.wmd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Student on 1/27/2015.
 */
public class DBHelper extends SQLiteOpenHelper {
//    public static final String COLUMN_THROW_ID = "throw_id";
//    public static final String COLUMN_HOLE_ID = "hole_id";
//    public static final String COLUMN_GAME_ID = "game_id";
//    public static final String COLUMN_START_LAT = "starting_latitude";
//    public static final String COLUMN_START_LONG = "starting_longitude";
//    public static final String COLUMN_END_LAT = "ending_latitude";
//    public static final String COLUMN_END_LONG = "ending_longitude";
//    public static final String COLUMN_START_ACCEL_X = "starting_x_acceleration";
//    public static final String COLUMN_START_ACCEL_Y = "starting_y_acceleration";
//    public static final String COLUMN_START_TIME = "starting_time";
//    public static final String COLUMN_END_TIME = "ending_time";


    public static final String TABLE_THROWS = "throw_data";

    public static final String COLUMN_THROW_ID = "throw_id";
    public static final String COLUMN_HOLE_ID = "hole_id";
    public static final String COLUMN_GAME_ID = "game_id";
    public static final String COLUMN_INITIAL_DIRECTION = "initial_direction";
    public static final String COLUMN_FINAL_DIRECTION = "final_direction";
    public static final String COLUMN_TOTAL_DISTANCE = "total_distance";
    public static final String COLUMN_THROW_INTEGRITY = "throw_integrity";
    public static final String COLUMN_TOTAL_TIME = "total_time";
    public static final String COLUMN_SYNC_TIME = "sync_time";

    private static final String DATABASE_NAME = "throw_data.db";
    private static final int DATABASE_VERSION = 2;

    private static final String THROWS_TABLE_CREATE = "create table "
            + TABLE_THROWS + " ( "
            + COLUMN_THROW_ID + " integer primary key autoincrement, "
            + COLUMN_HOLE_ID + " integer not null, "
            + COLUMN_GAME_ID + " integer not null, "
            + COLUMN_INITIAL_DIRECTION + " integer not null, "
            + COLUMN_FINAL_DIRECTION + " integer not null, "
            + COLUMN_TOTAL_DISTANCE + " double not null, "
            + COLUMN_THROW_INTEGRITY + " double not null, "
            + COLUMN_TOTAL_TIME + " integer not null, "
            + COLUMN_SYNC_TIME + " integer not null"
            + ");";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_THROWS);
        db.execSQL(THROWS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_THROWS);
        onCreate(db);
    }

}
