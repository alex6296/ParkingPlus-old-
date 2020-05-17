package com.example.database;

public class tempLongitude {
    double Longitude;
    double Lattitude;

    public tempLongitude(){

    }
    public tempLongitude(double longitude, double lattitude) {
        this.Longitude = longitude;
        this.Lattitude = lattitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        this.Longitude = longitude;
    }

    public double getLattitude() {
        return Lattitude;
    }

    public void setLattitude(double lattitude) {
        this.Lattitude = lattitude;
    }
}
