package com.wolfmobileapps.gofix;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityRegistration extends AppCompatActivity {

    private static final String TAG = "ActivityRegistration";

    //views
    Spinner spinnerTypeOfUser;
    Spinner spinnerRegions;

    // lista wojewodztw
    private ArrayList<String> listRegions;
    ArrayAdapter<String> spinnerArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //views
        spinnerTypeOfUser = findViewById(R.id.spinnerTypeOfUser);
        spinnerRegions = findViewById(R.id.spinnerRegions);

        // action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Rejestracja");

        // metoda spinnera do wyboru czy specjalista czy zleceniodawca
        spinerTypeOfUser();

        // metoda spinnera do wyboru województwa

    }


    // metoda spinnera do wyboru czy specjalista czy zleceniodawca
    private void spinerTypeOfUser(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ActivityRegistration.this,
                R.array.array_type_of_user, android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeOfUser.setAdapter(adapter);
        spinnerTypeOfUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                switch (position){		 //ifem też można
                    case 0:
                        Toast.makeText(ActivityRegistration.this, text, Toast.LENGTH_SHORT).show();
                    case 1:
                        Toast.makeText(ActivityRegistration.this, text, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //nic nie musi być
            }
        });

        // pobranie listy województw, dodanie do listy i ustawienie na spinnerze
        getDataRegions(C.API + "regions");
    }

    // pobranie listy województw, dodanie do listy i ustawienie na spinnerze
    public void getDataRegions (String Url) {

        // lista wojewodztw
        listRegions = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(this); // utworzenie requst - może być inne np o stringa lub JsonArrray
        String url = Url; //url
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                // pobranie listy województw z API i zapisanie jej do listRegions
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String name = jsonObject.getString("name");
                        int id = jsonObject.getInt("id");
                        listRegions.add(id + ". " + name);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // ustawienie listRegions na spinnerRegions
                spinnerArrayAdapter = new ArrayAdapter<>(ActivityRegistration.this, android.R.layout.simple_list_item_1, listRegions);
                spinnerRegions.setAdapter(spinnerArrayAdapter);
                spinnerRegions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        // pobranie textu z okreslonego ustawienia
                        String text = parent.getItemAtPosition(position).toString();
                        Toast.makeText(ActivityRegistration.this, text, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        //nic nie musi być
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // do something when don"t getJSONObject
                Log.d(TAG, "onErrorResponse: " + error);

            }
        });
        queue.add(jsonArrayRequest); //wywołanie klasy
    }



}
