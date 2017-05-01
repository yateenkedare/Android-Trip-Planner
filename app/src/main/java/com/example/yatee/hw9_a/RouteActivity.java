package com.example.yatee.hw9_a;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.PolylineOptions;

public class RouteActivity extends AppCompatActivity implements FetchData.IData{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        String routeUrl=getIntent().getStringExtra("URL");
        new FetchData(RouteActivity.this).execute(routeUrl);


    }

    @Override
    public void setUpData(PolylineOptions data) {
        Log.d("TestAsync",data.toString());

    }

    @Override
    public Context getContext() {
        return null;
    }
}
