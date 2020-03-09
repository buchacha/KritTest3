package com.example.krittest3;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.content.AsyncTaskLoader;
import android.net.Uri;

import com.example.krittest3.models.City;
import com.example.krittest3.models.Forecast;

import java.util.ArrayList;

public class CityForecastsLoader extends AsyncTaskLoader<ArrayList<City>> {

    private String mUrl;
    private ArrayList<City> mCities;

    public CityForecastsLoader(@NonNull Context context, String url, ArrayList<City> cities) {
        super(context);
        mUrl = url;
        this.mCities = new ArrayList<City>(cities);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public ArrayList<City> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

//        Perform a lot of similar http requests : time = mCities.size()*httpRequestTime;
//        Need to find better forecast API;

        ArrayList<City> resultCities = new ArrayList<>();
        for (City city : mCities) {
            Uri baseUri = Uri.parse(mUrl);
            Uri.Builder uriBuilder = baseUri.buildUpon();

            uriBuilder.appendPath(city.getCoordinates());
            Forecast forecast = QueryUtils.fetchForecastData(uriBuilder.toString());

            City newCity = new City(city.getName(), city.getCoordinates(), forecast);
            resultCities.add(newCity);
        }
        return resultCities;
    }
}
