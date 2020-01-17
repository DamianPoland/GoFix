package com.wolfmobileapps.gofix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityCraftsmanData extends AppCompatActivity {

    private static final String TAG = "ActivityCraftsmanData";

    //views
    private TextView textViewCraftsmanDataName;
    private TextView textViewCraftsmanDataEmail;
    private TextView textViewCraftsmanRating;
    private TextView textViewCraftsmanDataBalance;
    private Button buttonChangeCraftsmanData;
    private Button  buttonCraftmanAllOrders;
    private Button buttonCraftmanOFFersAll;
    private Button buttonCraftmanOFFersTaken;
    private Button buttonCraftmanOFFersHistory;

    //shared pred
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_craftman_data);

        //views
        textViewCraftsmanDataName = findViewById(R.id.textViewCraftsmanDataName);
        textViewCraftsmanDataEmail = findViewById(R.id.textViewCraftsmanDataEmail);
        textViewCraftsmanDataBalance = findViewById(R.id.textViewCraftsmanDataBalance);
        textViewCraftsmanRating = findViewById(R.id.textViewCraftsmanRating);
        buttonChangeCraftsmanData = findViewById(R.id.buttonChangeCraftsmanData);
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

        // pobranie danych o craftsmanie
        getDataFromUrl();

        // button do zmiany danych - czyli otwarcia strony GoFix.pl
        buttonChangeCraftsmanData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri webpage = Uri.parse("http://www.gofix.pl");
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

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

    // pobranie danych o craftsmanie z API i wstawienie do textView
    public void getDataFromUrl () {

        RequestQueue queue = Volley.newRequestQueue(this); // utworzenie requst - może być inne np o stringa lub JsonArrray
        String url = C.API + "user/preferences"; //url
        Log.d(TAG, "sendLogin: url: " + url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d(TAG, "onResponse: response: " + response);

                //pobiera danae i wstawia do textViews
                try {
                    String name = response.getString("name"); // pobranie nazwy craftsmana
                    String email= response.getString("email"); // pobranie emaila craftsmana
                    int balance = response.getInt("balance"); // pobranie punktów craftsmana
                    float craftsman_rating = Float.parseFloat("" + response.getDouble("rating")); // pobranie ratingu craftsmana

                    // ustawienie danych w textViews
                    textViewCraftsmanDataName.setText(name);
                    textViewCraftsmanDataEmail.setText(email);
                    // jeśli craftsman nie będzi miałjeszcze ocen to będzi 0 i wtedy pokazę brak ocen
                    if (craftsman_rating == 0) {
                        textViewCraftsmanRating.setText("Brak ocen");
                    } else {
                        textViewCraftsmanRating.setText("" + craftsman_rating + "/10");
                    }
                    textViewCraftsmanDataBalance.setText("" + balance);

                    // zapisanie ilości punktów do shar żeby jak będzie 0 to nie pozwolił na dalsze dodawanie ofert przez craftsmana
                    editor.putInt(C.KEY_FOR_BALANCE_SHAR, balance);
                    editor.apply();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, TAG + "getDataFromUrl.onErrorResponse: " + error);
                // jeśli jest response: com.android.volley.ParseError: org.json.JSONException: Value [] of type org.json.JSONArray cannot be converted to JSONObject - to znaczy że nie ma zleceń w tym województwie(regionie)

            }
        }) {    //this is the part, that adds the header to the request
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json"); //  header format wysłanej wiadomości - JSON
                params.put("Accept", "application/json"); //  header format otrzymanej wiadomości -JSON
                params.put("Consumer", C.HEDDER_CUSTOMER); //  header Consumer
                params.put("Authorization", C.HEDDER_BEARER + shar.getString(C.KEY_FOR_SHAR_TOKEN, "")); //  header Authorization
                return params;
            }
        };
        queue.add(jsonObjectRequest); //wywołanie klasy
    }
}
