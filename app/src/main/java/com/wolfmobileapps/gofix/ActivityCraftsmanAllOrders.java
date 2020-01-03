package com.wolfmobileapps.gofix;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class ActivityCraftsmanAllOrders extends AppCompatActivity {

    private static final String TAG = "CraftsmanAllOrders";

    //views
    TextView textViewNoOrdersCraftsman;
    ListView listViewOfOrdersCraftsman;

    //shared pred
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    // lista zleceń
    private ArrayList<Order> listOfCraftsmanOrders;
    private AdapterForCraftsmanOrders adapterForCraftsmanOrders;

    // list Serwices
    private ArrayList<ServicesAndIndustryName> listOfIndustriesAndServicesAcoordingToServiceID = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_craftsman_all_orders);

        //views
        textViewNoOrdersCraftsman = findViewById(R.id.textViewNoOrdersCraftsman);
        listViewOfOrdersCraftsman = findViewById(R.id.listViewOfOrdersCraftsman);

        // action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Panel Użytkownika");

        // shar pref
        shar = getSharedPreferences("sharName", MODE_PRIVATE);
        editor = shar.edit();

        // lista Orders + ściągnięcie z neta
        listOfCraftsmanOrders = new ArrayList<>();
        getDataFromUrl();

        // ustawienie adaptera
        adapterForCraftsmanOrders = new AdapterForCraftsmanOrders(this,0, listOfCraftsmanOrders);
        listViewOfOrdersCraftsman.setAdapter(adapterForCraftsmanOrders);

        //podbranie nazwy Industry, industryID, nazwy Service i serviceID i zapisanie do array listOfIndustriesAndServicesAcoordingToServiceID
        Order order = new Order(this);
        listOfIndustriesAndServicesAcoordingToServiceID.addAll(order.putIndustriesAndServicesWithIDToArray ());

        //list view z ofertami, onClick jeśli ma być wysłąna oferta do danego Order
        listViewOfOrdersCraftsman.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCraftsmanAllOrders.this);
                builder.setTitle("Oferta");
                builder.setMessage("Czy chcesz wysłać ofertę do tego zlecenia?");
                builder.setPositiveButton("TAK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent currentIntent = new Intent(ActivityCraftsmanAllOrders.this, ActivityCraftsmanOffer.class);
                        Order currentOrder = (Order) listViewOfOrdersCraftsman.getItemAtPosition(position);
                        int orderId = currentOrder.getId();
                        int craftsmanId = currentOrder.getCraftsman_id();
                        currentIntent.putExtra("orderId", orderId);
                        currentIntent.putExtra("craftsmanId", craftsmanId);
                        startActivity(currentIntent);
                        finish();
                    }
                }).setNegativeButton("NIE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do something when click cancel
                    }
                }).create();
                builder.show();
            }
        });

    }

    // pobranie listy zleceń i dodanie do listView
    public void getDataFromUrl () {

        RequestQueue queue = Volley.newRequestQueue(this); // utworzenie requst - może być inne np o stringa lub JsonArrray
        String url = C.API + "craftsman/orders/open"; //url
        Log.d(TAG, "sendLogin: url: " + url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                // ukrycie textViewNoOrders
                textViewNoOrdersCraftsman.setVisibility(View.INVISIBLE);
                Log.d(TAG, "onResponse: response: " + response);

                // dodanie listy Orders z response
                Order order = new Order(ActivityCraftsmanAllOrders.this);
                listOfCraftsmanOrders.addAll(order.putOrdersToArrayList(response, listOfIndustriesAndServicesAcoordingToServiceID));
                adapterForCraftsmanOrders.notifyDataSetChanged();
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
