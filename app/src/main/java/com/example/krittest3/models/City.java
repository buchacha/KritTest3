package com.example.krittest3.models;

public class City {

    private String name;
    private String coordinates;
    private Forecast forecast;

    public City(String name, String coordinates, Forecast forecast) {
        this.name = name;
        this.coordinates = coordinates;
        this.forecast = forecast;
    }

    public City(String name, String coordinates) {
        this.name = name;
        this.coordinates = coordinates;
    }

    public String getName() {
        return name;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public Forecast getForecast() throws Exception {
        if (forecast == null) {
            throw new Exception("You tried to get null forecast");
        }

        return forecast;
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }
}
