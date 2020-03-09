package com.example.krittest3.models;

public class Forecast {

    private String summary;
    private String icon;
    private Double temperature;

    public Forecast(String summary, String icon, Double temperature) {
        this.summary = summary;
        this.icon = icon;
        this.temperature = temperature;
    }

//    public void setSummary(String summary) {
//        this.summary = summary;
//    }
//
//    public void setIcon(String icon) {
//        this.icon = icon;
//    }
//
//    public void setTemperatureFar(Double temperature) {
//        this.temperature = temperature;
//    }

    public String getSummary() {
        return summary;
    }

    public String getIcon() {
        return icon;
    }

    public Double getTemperatureCel() {
        return (temperature-32)*5/9;
    }
}
