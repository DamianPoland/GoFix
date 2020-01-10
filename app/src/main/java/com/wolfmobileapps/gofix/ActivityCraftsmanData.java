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

    // pobranie danych o craftsmanie
    public void getDataFromUrl () {

        RequestQueue queue = Volley.newRequestQueue(this); // utworzenie requst - może być inne np o stringa lub JsonArrray
        String url = C.API + "craftsman/orders/applied"; //url
        Log.d(TAG, "sendLogin: url: " + url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                // jeśli jest pusty JSON to wyłączy
                if (response.toString().equals("[]")) {
                    return;
                }

                try {
                    String service_id = response.getString("service_id"); // pobranie nazwy craftsmana
                    String email= response.getString("service_id"); // pobranie emaila craftsmana
                    int id = response.getInt("id"); // pobranie punktów craftsmana


                    // ustawienie danych w textViews
                    textViewCraftsmanDataName.setText("Example");
                    textViewCraftsmanDataEmail.setText("Example");
                    textViewCraftsmanDataBalance.setText("Example");

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
