package com.wolfmobileapps.gofix;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ActivityOrderDescription extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_description);

        // action bar
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Zlecenie");
    }
}
