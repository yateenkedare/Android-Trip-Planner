package com.example.yatee.hw9_a;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, FetchData.IData {

    private GoogleMap mMap;
    private boolean mapsReady,dataReady;
    PolylineOptions data;
    Polyline line;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mapsReady = false;
        dataReady = false;

        String routeUrl=getIntent().getStringExtra("URL");
        new FetchData(MapsActivity.this).execute(routeUrl);

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mapsReady = true;

        if(dataReady){
            drawPoly();
        }
    }

    @Override
    public void setUpData(PolylineOptions data) {
        this.data = data;
        Log.d("TestAsync",data.toString());
        dataReady = true;
        if(mapsReady){
            drawPoly();
        }
    }

    @Override
    public Context getContext() {
        return null;
    }

    public void drawPoly(){
        LatLngBounds.Builder builder =  new LatLngBounds.Builder();
        for(LatLng mLocation: data.getPoints())
            builder.include(mLocation);
        mMap.addMarker(new MarkerOptions().position(data.getPoints().get(0)));
        mMap.addMarker(new MarkerOptions().position(data.getPoints().get(data.getPoints().size()-1)));
        LatLngBounds bound = builder.build();
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bound, 25), 1000, null);
        line = mMap.addPolyline(data);
        progressDialog.dismiss();
    }
}
