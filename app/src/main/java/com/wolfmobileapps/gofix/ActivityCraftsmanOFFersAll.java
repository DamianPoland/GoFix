package com.wolfmobileapps.gofix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityCraftsmanOFFersAll extends AppCompatActivity {

    private static final String TAG = "ActivityCraftsmanAllOFF";

    //views
    private TextView textViewCraftsmanOFFersAllNo;
    private ListView listViewCraftsmanOFFersAll;

    //shared pred
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    // lista zleceń
    private ArrayList<CraftsmanOffers> listOfCraftsmanOFFers;
    private AdapterForCraftsmanOFFers adapterForCraftsmanOFFers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_craftsman_offers_all);

        //views
        textViewCraftsmanOFFersAllNo = findViewById(R.id.textViewCraftsmanOFFersAllNo);
        listViewCraftsmanOFFersAll = findViewById(R.id.listViewCraftsmanOFFersAll);

        // shar pref
        shar = getSharedPreferences("sharName", MODE_PRIVATE);
        editor = shar.edit();

        // lista OFFers ściągnięcie z neta
        listOfCraftsmanOFFers = new ArrayList<>();
        getDataFromUrl();

        // ustawienie adaptera
        adapterForCraftsmanOFFers = new AdapterForCraftsmanOFFers(this,0, listOfCraftsmanOFFers);
        listViewCraftsmanOFFersAll.setAdapter(adapterForCraftsmanOFFers);

    }

    // pobranie listy zleceń i dodanie do listView
    public void getDataFromUrl () {

        RequestQueue queue = Volley.newRequestQueue(this); // utworzenie requst - może być inne np o stringa lub JsonArrray
        String url = C.API + "craftsman/orders/applied"; //url
        Log.d(TAG, "sendLogin: url: " + url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                // jeśli jest pusty JSON to wyłączy
                if (response.toString().equals("[]")) {
                    return;
                }

                // ukrycie textViewNoOrders
                textViewCraftsmanOFFersAllNo.setVisibility(View.INVISIBLE);
                Log.d(TAG, "onResponse: response: " + response);


                // przetworzenie danych z response na arrayList
                CraftsmanOffers craftsmanOffers = new CraftsmanOffers();
                listOfCraftsmanOFFers.addAll(craftsmanOffers.getDataFromUrlResponse(response));
                adapterForCraftsmanOFFers.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "getDataFromUrl.onErrorResponse: " + error);
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
        queue.add(jsonArrayRequest); //wywołanie klasy
    }
}


