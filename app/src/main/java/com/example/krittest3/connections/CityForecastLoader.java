package com.example.krittest3.connections;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.content.AsyncTaskLoader;
import android.net.Uri;

import com.example.krittest3.connections.QueryUtils;
import com.example.krittest3.models.City;
import com.example.krittest3.models.Forecast;

public class CityForecastLoader extends AsyncTaskLoader<Forecast> {

    private String mUrl;
    private City mCity;

    public CityForecastLoader(@NonNull Context context, String url, City city) {
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
