package com.wolfmobileapps.gofix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ActivityCraftsmanData extends AppCompatActivity {

    private static final String TAG = "ActivityCraftsmanData";

    //views
    Button  buttonCraftmanAllOrders;
    Button buttonCraftmanOFFersAll;
    Button buttonCraftmanOFFersTaken;
    Button buttonCraftmanOFFersHistory;

    //shared pred
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_craftman_data);

        //views
        buttonCraftmanAllOrders = findViewById(R.id.buttonCraftmanAllOrders);
        buttonCraftmanOFFersAll = findViewById(R.id.buttonCraftmanOFFersAll);
        buttonCraftmanOFFersTaken = findViewById(R.id.buttonCraftmanOFFersTaken);
        buttonCraftmanOFFersHistory = findViewById(R.id.buttonCraftmanOFFersHistory);

        // action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Panel Użytkownika");

        // shar pref
        shar = getSharedPreferences("sharName", MODE_PRIVATE);
        editor = shar.edit();

        // button do otwarcia Activity z All Orders
        buttonCraftmanAllOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityCraftsmanData.this, ActivityCraftsmanAllOrders.class));
            }
        });

        // button do otwarcia Activity z All OFFers
        buttonCraftmanOFFersAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityCraftsmanData.this, ActivityCraftsmanOFFersAll.class));
            }
        });

        // button do otwarcia Activity z All OFFers
        buttonCraftmanOFFersTaken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityCraftsmanData.this, ActivityCraftsmanOFFersTaken.class));
            }
        });

        // button do otwarcia Activity z All OFFers
        buttonCraftmanOFFersHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityCraftsmanData.this, ActivityCraftsmanOFFersHistory.class));
            }
        });


    }


}
