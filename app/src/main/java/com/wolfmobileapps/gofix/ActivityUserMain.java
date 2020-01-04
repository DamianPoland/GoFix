package com.wolfmobileapps.gofix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class ActivityUserMain extends AppCompatActivity {

    private static final String TAG = "ActivityUserMain";

    //views
    TextView textViewUserNoOrdersMain;
    Button buttonOrdersHistory;
    ListView listViewUserOrdersMain;

    // lista zleceń
    private ArrayList<OrderUser> listOfOrders;
    private AdapterForUserOrders adapterForUserOrders;

    //shared pred
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    // list Serwices
    private ArrayList<ServicesAndIndustryName> listOfIndustriesAndServicesAcoordingToServiceID = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        //views
        textViewUserNoOrdersMain = findViewById(R.id.textViewUserNoOrdersMain);
        buttonOrdersHistory = findViewById(R.id.buttonOrdersHistory);
        listViewUserOrdersMain = findViewById(R.id.listViewUserOrdersMain);

        // action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Panel Użytkownika");

        // shar pref
        shar = getSharedPreferences("sharName", MODE_PRIVATE);
        editor = shar.edit();

        // lista + ściągnięcie z neta
        listOfOrders = new ArrayList<>();
        getDataFromUrl();

        // ustawienie adaptera
        adapterForUserOrders = new AdapterForUserOrders(this,0,listOfOrders);
        listViewUserOrdersMain.setAdapter(adapterForUserOrders);

        //podbranie nazwy Industry, industryID, nazwy Service i serviceID i zapisanie do array listOfIndustriesAndServicesAcoordingToServiceID
        OrderCraftsman order = new OrderCraftsman(this);
        listOfIndustriesAndServicesAcoordingToServiceID.addAll(order.putIndustriesAndServicesWithIDToArray ());

        // button do zleceń zamkniętych
        buttonOrdersHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityUserMain.this, ActivityUserHistory.class));
            }
        });

        //list view z ofertami
        listViewUserOrdersMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent listIntent = new Intent(ActivityUserMain.this, ActivityUserOffers.class);

                //przypisanie offer ID i wysłąnie razem z intent
                OrderUser orderUserCurrent = (OrderUser) listViewUserOrdersMain.getItemAtPosition(position);
                int orderID = orderUserCurrent.getId(); // pobranie order ID danego itema OrderUser
                listIntent.putExtra(C.KEY_FOR_INTENT_TO_ORDER_ID, orderID);
                startActivity(listIntent);

                Log.d(TAG, "onItemClick, ActivityUserMain,  orderID(offer): " + orderID);
            }
        });
    }

    // pobranie listy zleceń i dodanie do listView
    public void getDataFromUrl () {

        RequestQueue queue = Volley.newRequestQueue(this); // utworzenie requst - może być inne np o stringa lub JsonArrray
        String url = C.API + "client/orders"; //url
        Log.d(TAG, "sendLogin: Url: " + url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                // jeśli jest pusty JSON to wyłączy
                if (response.toString().equals("[]")) {
                    return;
                }

                // ukrycie textViewNoOrders
                textViewUserNoOrdersMain.setVisibility(View.INVISIBLE);
                Log.d(TAG, "ActivityUserMain: response: " + response);

                // dodanie listy Orders z response
                OrderUser orderUser = new OrderUser(ActivityUserMain.this);
                listOfOrders.addAll(orderUser.putOrdersToArrayList(response, listOfIndustriesAndServicesAcoordingToServiceID));
                adapterForUserOrders.notifyDataSetChanged();
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

