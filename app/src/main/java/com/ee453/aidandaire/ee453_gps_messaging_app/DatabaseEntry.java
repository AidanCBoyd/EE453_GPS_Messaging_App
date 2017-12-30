package com.ee453.aidandaire.ee453_gps_messaging_app;

/**
 * Created by boyd on 30/12/17.
 */

class DatabaseEntry {

    private double lng;
    private double lat;
    private String message;

    public DatabaseEntry(double latitude, double longitude, String text) {

        this.lat = latitude;
        this.lng = longitude;
        this.message = text;

    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
