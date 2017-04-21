package com.example.yatee.hw9_a;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class DiscoverTripsActivity extends AppCompatActivity {

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_trips);

        fab = (FloatingActionButton) findViewById(R.id.fabAddNewTrip);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DiscoverTripsActivity.this,AddTripActivity.class);
                startActivity(intent);
            }
        });
    }
}
