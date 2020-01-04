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
import org.json.JSONException;
import org.json.JSONObject;

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
    private ArrayList<CraftsmanOffersAll> listOfCraftsmanOFFersAll;
    private AdapterForCraftsmanOFFersAll adapterForCraftsmanOFFersAll;

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
        listOfCraftsmanOFFersAll = new ArrayList<>();
        getDataFromUrl();
        // ustawienie adaptera
        adapterForCraftsmanOFFersAll = new AdapterForCraftsmanOFFersAll(this,0, listOfCraftsmanOFFersAll);
        listViewCraftsmanOFFersAll.setAdapter(adapterForCraftsmanOFFersAll);

    }

    // pobranie listy zleceń i dodanie do listView
    public void getDataFromUrl () {

        RequestQueue queue = Volley.newRequestQueue(this); // utworzenie requst - może być inne np o stringa lub JsonArrray
        String url = C.API + "craftsman/offers"; //url
        Log.d(TAG, "sendLogin: url: " + url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                // ukrycie textViewNoOrders
                textViewCraftsmanOFFersAllNo.setVisibility(View.INVISIBLE);
                Log.d(TAG, "onResponse: response: " + response);

                // pobranie JSonArray i zapisanie do listOfOrders
                for (int i = 0; i < response.length(); i++) {
                    try {

                        // pobranie danych z JSONA i zapisanie do listy listOfCraftsmanOFFersAll
                        JSONObject jsonObject = response.getJSONObject(i);
                        int id = jsonObject.getInt("id"); // id zlecenia
                        int order_id = jsonObject.getInt("order_id");
                        String details = jsonObject.getString("details");
                        String price = jsonObject.getString("price");
                        String city = jsonObject.getString("city");
                        String client_name = jsonObject.getString("client_name");

                        Log.d(TAG, "onResponse: \nid: " + id + "\norder_id: " + order_id + "\ndetails: " + details + "\nprice: " + price + "\ncity: " + city + "\nclient_name: " + client_name);
                        CraftsmanOffersAll craftsmanOffersAll = new CraftsmanOffersAll(id, order_id, details, price, city, client_name);
                        listOfCraftsmanOFFersAll.add(craftsmanOffersAll);
                        adapterForCraftsmanOFFersAll.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
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

class CraftsmanOffersAll {

    private int id;
    private int order_id;
    private String details;
    private String price;
    private String city;
    private String client_name;

    public CraftsmanOffersAll(int id, int order_id, String details, String price, String city, String client_name) {
        this.id = id;
        this.order_id = order_id;
        this.details = details;
        this.price = price;
        this.city = city;
        this.client_name = client_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }
}
