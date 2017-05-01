package com.example.yatee.hw9_a;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by yatee on 4/30/2017.
 */

public class Places  {
    String place;
    String id;
    double latitude;
    double longitude;


    public Places() {
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Places(String place, String id,double latitude,double longitude) {
        this.place = place;
        this.id = id;
        this.latitude=latitude;
        this.longitude=longitude;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlaces() {
        return place;
    }

    public void setPlace(String places) {
        this.place = places;
    }

    @Override
    public String toString() {
        return  place;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj != null & obj instanceof Places){
            return this.id.equals(((Places) obj).id);
        }
        return false;
    }
}