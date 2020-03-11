package com.example.krittest3;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.content.AsyncTaskLoader;
import android.net.Uri;

import com.example.krittest3.models.City;
import com.example.krittest3.models.Forecast;

import java.util.ArrayList;

public class CityForecastsLoader extends AsyncTaskLoader<Forecast> {

    private String mUrl;
    private City mCity;

    public CityForecastsLoader(@NonNull Context context, String url, City city) {
        super(context);
        mUrl = url;
        this.mCity = city;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public Forecast loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        Uri baseUri = Uri.parse(mUrl);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendPath(mCity.getCoordinates());
        Forecast forecast = QueryUtils.fetchForecastData(uriBuilder.toString());

        return forecast;
    }
}
