package com.wolfmobileapps.gofix;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ActivityUserMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        // action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Panel Użytkownika");
    }
}
