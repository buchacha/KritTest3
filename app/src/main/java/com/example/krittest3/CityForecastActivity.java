package com.example.krittest3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.krittest3.connections.CityForecastLoader;
import com.example.krittest3.connections.QueryUtils;
import com.example.krittest3.database.ForecastContract.ForecastEntry;
import com.example.krittest3.models.City;
import com.example.krittest3.models.Forecast;

import java.text.DecimalFormat;

public class CityForecastActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Forecast> {

    private static final String LOG_TAG = CityForecastActivity.class.getName();

    private int WEB_LOADER_ID = 1;
    private int DB_LOADER_ID = 2;
    Context mContext;

    private LoaderManager.LoaderCallbacks<Cursor> mDbLoader
            = new LoaderManager.LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String[] projection = {
                    ForecastEntry._ID,
                    ForecastEntry.COLUMN_CITY,
                    ForecastEntry.COLUMN_SUMMARY,
                    ForecastEntry.COLUMN_TEMPERATURE};

            String selection = ForecastEntry.COLUMN_CITY + "=?";
            String[] selectionArgs = { mCity.getName() };

            return new CursorLoader( mContext,
                    ForecastEntry.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            mProgressBar.setVisibility(View.GONE);

            if (cursor == null || cursor.getCount() < 1) {
                String summary = getString(R.string.iternet_connection_loss);
                summaryTextView.setText(summary);
                return;
            }

            if (cursor.moveToFirst()) {

                int cityColumnIndex = cursor.getColumnIndex(ForecastEntry.COLUMN_CITY);
                int summaryColumnIndex = cursor.getColumnIndex(ForecastEntry.COLUMN_SUMMARY);
                int temperatureColumnIndex = cursor.getColumnIndex(ForecastEntry.COLUMN_TEMPERATURE);

                String city = cursor.getString(cityColumnIndex);
                String summary = cursor.getString(summaryColumnIndex);
                double temperature = cursor.getDouble(temperatureColumnIndex);

                nameTextView.setText(city);
                summaryTextView.setText(summary + "\n" + getString(R.string.load_from_db));
                DecimalFormat df = new DecimalFormat("#.#");
                String temperatureText = df.format(temperature) + " C˚";
                temperatureTextView.setText(temperatureText);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            return;
        }
    };

    private TextView nameTextView;
    private TextView summaryTextView;
    private TextView temperatureTextView;
    private ImageView iconImageView;

    private City mCity;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_forecast);
        mContext = getApplicationContext();
        Intent i = getIntent();
        mCity = (City) i.getExtras().getSerializable("CITY");
        initViews();
        fillView();
        startLoading();
        getSupportActionBar().setTitle("Прогноз погоды в городе");
    }

    @Override
    public Loader<Forecast> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Uri.parse(QueryUtils.BASE_URL);

        return new CityForecastLoader(this, baseUri.toString(), mCity);
    }

    @Override
    public void onLoadFinished(Loader<Forecast> loader, Forecast forecast) {
        ContentValues values = new ContentValues();
        values.put(ForecastEntry.COLUMN_CITY, mCity.getName());
        values.put(ForecastEntry.COLUMN_SUMMARY, forecast.getSummary());
        values.put(ForecastEntry.COLUMN_TEMPERATURE, forecast.getTemperatureCel());

        Uri newUri = getContentResolver().insert(ForecastEntry.CONTENT_URI, values);
        mCity.setForecast(forecast);
        mProgressBar.setVisibility(View.GONE);
        updateView();
    }

    @Override
    public void onLoaderReset(Loader<Forecast> loader) {
        return;
    }

    private void startLoading() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            mProgressBar.setVisibility(View.VISIBLE);
            LoaderManager loaderManager = getLoaderManager();

            loaderManager.initLoader(WEB_LOADER_ID, null, this);
        } else {
            getLoaderManager().initLoader(DB_LOADER_ID, null, mDbLoader);

        }
    }

    private void initViews() {
        iconImageView = findViewById(R.id.desc_img_card);
        nameTextView = findViewById(R.id.name_text_view);
        summaryTextView = findViewById(R.id.desc_text_card);
        temperatureTextView = findViewById(R.id.temperature_text_view);
        mProgressBar = findViewById(R.id.loading_indicator);
    }

    private void fillView() {
        nameTextView.setText(mCity.getName());
    }

    private void updateView() {
        String temperature = "? C˚";
        String summary = getString(R.string.iternet_connection_loss);
        try {
            DecimalFormat df = new DecimalFormat("#.#");
            Double temperatureDouble = mCity.getForecast().getTemperatureCel();
            temperature = df.format(temperatureDouble) + " C˚";
            summary = mCity.getForecast().getSummary();
        } catch (Exception e) {
            Log.e(LOG_TAG, "error while update forecast", e);
        } finally {
            temperatureTextView.setText(temperature);
            summaryTextView.setText(summary);
        }
    }
}
