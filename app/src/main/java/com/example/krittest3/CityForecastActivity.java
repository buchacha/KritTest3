package com.example.krittest3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.krittest3.models.City;
import com.example.krittest3.models.Forecast;

import java.text.DecimalFormat;

public class CityForecastActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Forecast>  {

    private static final String LOG_TAG = CityForecastActivity.class.getName();

    private int LOADER_ID = 1;

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

        return new CityForecastsLoader(this, baseUri.toString(), mCity);
    }

    @Override
    public void onLoadFinished(Loader<Forecast> loader, Forecast forecast) {
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

            loaderManager.initLoader(LOADER_ID, null, this);
        } else {
            mProgressBar.setVisibility(View.GONE);
            String summary = "If you see it, then you disabled from internet";
            summaryTextView.setText(summary);

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
    private void updateView(){
        String temperature = "? C˚";
        String summary = "If you see it, then you disabled from internet";
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
