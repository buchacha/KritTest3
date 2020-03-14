package com.example.krittest3.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ForecastContract {

    private ForecastContract() {}

    public static final String CONTENT_AUTHORITY = "com.example.krittest3.database";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final String PATH_FORECASTS = "forecasts";

    public static final class ForecastEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FORECASTS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FORECASTS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_FORECASTS;

        public final static String TABLE_NAME = "forecast";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_CITY ="city";

        public final static String COLUMN_SUMMARY = "summary";

        public final static String COLUMN_TEMPERATURE = "temperature";

    }
}
