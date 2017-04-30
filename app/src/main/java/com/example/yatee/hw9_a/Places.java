package com.example.yatee.hw9_a;

import java.util.ArrayList;

/**
 * Created by yatee on 4/30/2017.
 */

public class Places  {
    String place;
    String id;

    public Places() {
    }

    public Places(String place, String id) {
        this.place = place;
        this.id = id;
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
}
