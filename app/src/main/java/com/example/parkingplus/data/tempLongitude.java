package com.example.parkingplus.data;

public class tempLongitude {
    private double Longitude;
    private double Latitude;

    public tempLongitude(){

    }

    public tempLongitude(double longitude, double latitude) {
        this.Longitude = longitude;
        this.Latitude = latitude;
    }

    public double getLongitude()
    {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        this.Longitude = longitude;
    }

    public double getLatitude() {

        return Latitude;
    }

    public void setLatitude(double latitude) {
        this.Latitude = latitude;
    }
}
