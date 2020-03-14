package com.example.krittest3.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.krittest3.database.ForecastContract.ForecastEntry;


public class ForecastDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = ForecastDbHelper.class.getSimpleName();

    private static final String DATABASE_NAME = "forecastStorage.db";

    private static final int DATABASE_VERSION = 1;


    public ForecastDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_FORECAST_TABLE =  "CREATE TABLE " + ForecastEntry.TABLE_NAME + " ("
                + ForecastEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ForecastEntry.COLUMN_CITY + " TEXT NOT NULL, "
                + ForecastEntry.COLUMN_SUMMARY+ " TEXT, "
                + ForecastEntry.COLUMN_TEMPERATURE+ " REAL"
                + ");";

        db.execSQL(SQL_CREATE_FORECAST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}