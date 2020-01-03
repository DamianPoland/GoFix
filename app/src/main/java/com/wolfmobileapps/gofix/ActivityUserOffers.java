package com.wolfmobileapps.gofix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class ActivityUserOffers extends AppCompatActivity {

    private static final String TAG = "ActivityUserOffers";

    //views
    TextView textViewNoOffers;

    //shared pred
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    // order ID z poprzedniego intent do api
    int orderID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_offers);

        //views
        textViewNoOffers = findViewById(R.id.textViewNoOffers);

        // shar pref
        shar = getSharedPreferences("sharName", MODE_PRIVATE);
        editor = shar.edit();

        // pobranie order ID z Intent
        Intent intent = getIntent();
        orderID = intent.getIntExtra(C.KEY_FOR_INTENT_TO_ORDER_ID, 0);

        // pobranie danych do listViewOffers
        String apiUrl = C.API + "client/order/" + orderID + "/offers";
        getDataFromUrl(apiUrl);
    }

    // pobranie listy zleceń i dodanie do listView
    public void getDataFromUrl (String Url) {

        Log.d(TAG, "sendLogin: Url: " + Url);

        RequestQueue queue = Volley.newRequestQueue(this); // utworzenie requst - może być inne np o stringa lub JsonArrray
        String url = Url; //url
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                // ukrycie textViewNoOrders
                textViewNoOffers.setVisibility(View.INVISIBLE);

                Log.d(TAG, "onResponse: response: " + response);

                //TODO
                // pobranie JSonArray i zapisanie do listy - zrobić klase Order i adaptera
//                for (int i = 0; i < response.length(); i++) {
//                    try {
//                        JSONObject jsonObject = response.getJSONObject(i);
//                        int idOfCurrentIndustry = jsonObject.getInt("id");
//                        String nameOfCurrentIndustry = jsonObject.getString("name");
//                        listOfOrders.add(new Order(???);
//                        adapterForOrders.notifyDataSetChanged();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // do something when don"t getJSONObject
                Log.d(TAG, "getDataFromUrl.onErrorResponse: " + error);

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
