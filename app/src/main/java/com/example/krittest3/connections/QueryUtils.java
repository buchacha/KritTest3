package com.example.krittest3.connections;

import android.text.TextUtils;
import android.util.Log;

import com.example.krittest3.models.Forecast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    public static String BASE_URL = "https://api.darksky.net/forecast/8ed453b19d8ca3c97c3cceaa16c501fd";

    private QueryUtils() {
    }

    public static Forecast fetchForecastData(String requestUrl) {
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        Forecast forecast = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Earthquake}s
        return forecast;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setReadTimeout(10000);
//            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the forecast JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static Forecast extractFeatureFromJson(String earthquakeJSON) {
        if (TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }

        Forecast forecast;
        String summary = "";
        String icon = "";
        Double temperature = 0.0;

        try {
            JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);

            JSONObject dailyInfo = baseJsonResponse.getJSONObject("daily");


            summary = dailyInfo.getString("summary");
            icon = dailyInfo.getString("icon");

            JSONObject dataInfo = dailyInfo.getJSONArray("data").getJSONObject(0);

            temperature = dataInfo.getDouble("temperatureMax");

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        forecast = new Forecast(summary, icon, temperature);
        return forecast;
    }

}
