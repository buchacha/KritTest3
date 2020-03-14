package com.example.krittest3.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.krittest3.database.ForecastContract.ForecastEntry;

public class ForecastProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = ForecastProvider.class.getSimpleName();

    private static final int FORECASTS = 100;

    private static final int FORECAST_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    static {

        sUriMatcher.addURI(ForecastContract.CONTENT_AUTHORITY, ForecastContract.PATH_FORECASTS, FORECASTS);

        sUriMatcher.addURI(ForecastContract.CONTENT_AUTHORITY, ForecastContract.PATH_FORECASTS + "/#", FORECAST_ID);
    }

    private ForecastDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ForecastDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case FORECASTS:
                cursor = database.query(ForecastEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case FORECAST_ID:
                selection = ForecastEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(ForecastEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FORECASTS:
                return insertForecast(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertForecast(Uri uri, ContentValues values) {
        String name = values.getAsString(ForecastEntry.COLUMN_CITY);
        if (name == null) {
            throw new IllegalArgumentException("Forecast requires a city");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(ForecastEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FORECASTS:
                return updateForecast(uri, contentValues, selection, selectionArgs);
            case FORECAST_ID:
                selection = ForecastEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateForecast(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateForecast(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(ForecastEntry.COLUMN_CITY)) {
            String name = values.getAsString(ForecastEntry.COLUMN_CITY);
            if (name == null) {
                throw new IllegalArgumentException("Forecast requires a city");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();


        int rowsUpdated = database.update(ForecastEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FORECASTS:
                rowsDeleted = database.delete(ForecastEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FORECAST_ID:
                selection = ForecastEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(ForecastEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FORECASTS:
                return ForecastEntry.CONTENT_LIST_TYPE;
            case FORECAST_ID:
                return ForecastEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}