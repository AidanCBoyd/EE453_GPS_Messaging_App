package com.ee453.aidandaire.ee453_gps_messaging_app;

/**
 * Created by boyd on 09/10/17.
 */

public class LocationData {

    private double latitude, longitude;

    public LocationData(double lat, double longit) {
        this.latitude = lat;
        this.longitude = longit;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
