package com.example.krittest3;

import android.util.Log;

import com.example.krittest3.models.City;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class CityCSVFile {
    InputStream inputStream;
    private final int CITIES_N = 2000;

    public CityCSVFile(InputStream inputStream){
        this.inputStream = inputStream;
    }

    public ArrayList<City> read(){
        ArrayList<City> resultList = new ArrayList<City>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
        try {
            String csvLine;
            csvLine = reader.readLine();
            int n = 0;
            while ((csvLine = reader.readLine()) != null && n < CITIES_N) {
                String[] splitted = csvLine.split(",");
                String lat1 = splitted[3].replace("\"", "");
                String lat2 = splitted[4].replace("\"", "");
                String lng1 = splitted[5].replace("\"", "");
                String lng2 = splitted[6].replace("\"", "");
                String coordinates = lat1+"."+lat2+","+lng1+"."+lng2;
                City city = new City(splitted[0], coordinates);
                resultList.add(city);
                n++;
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: "+ex);
        }
        finally {
            try {
                inputStream.close();
            }
            catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: "+e);
            }
        }
        return resultList;
    }
}