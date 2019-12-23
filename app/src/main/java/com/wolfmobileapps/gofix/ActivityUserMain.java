package com.wolfmobileapps.gofix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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

public class ActivityUserMain extends AppCompatActivity {

    private static final String TAG = "ActivityUserMain";

    //views
    TextView textViewOrders;
    TextView textViewNoOrders;
    Button buttonOrdersHistory;
    ListView listViewOrders;

    // lista zleceń
    private ArrayList<Order> listOfOrders;
    private AdapterForOrders adapterForOrders;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);

        //views
        textViewOrders = findViewById(R.id.textViewOrders);
        textViewNoOrders = findViewById(R.id.textViewNoOrders);
        buttonOrdersHistory = findViewById(R.id.buttonOrdersHistory);
        listViewOrders = findViewById(R.id.listViewOrders);

        // action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Panel Użytkownika");

        // lista branż + ściągnięcie z neta
        listOfOrders = new ArrayList<>();
        getDataFromUrl(C.API + "client/orders");

        // ustawienie adaptera
        adapterForOrders = new AdapterForOrders(this,0,listOfOrders);
        listViewOrders.setAdapter(adapterForOrders);

        // button do zleceń zamkniętych
        buttonOrdersHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityUserMain.this, ActivityUserHistory.class));
            }
        });

        //list view z ofertami
        listViewOrders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent listIntent = new Intent(ActivityUserMain.this, ActivityUserOffers.class);

                //TODO order ID trzeba przypisać
                int orderID = 1; // order ID trzeba przypisać
                listIntent.putExtra(C.KEY_FOR_INTENT_TO_ORDER_ID, 1);
                startActivity(listIntent);
            }
        });
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
                textViewNoOrders.setVisibility(View.INVISIBLE);

                Log.d(TAG, "onResponse: response: " + response);

                //TODO
                // pobranie JSonArray i zapisanie do listOfOrders - zrobić klase Order i adaptera do końca

                // przykłądowe do sprawdzenia czy działą
                listOfOrders.add(new Order("jeden"));
                listOfOrders.add(new Order("dwa"));
                listOfOrders.add(new Order("trzy"));
                adapterForOrders.notifyDataSetChanged();

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
        });
        queue.add(jsonArrayRequest); //wywołanie klasy
    }
}

class Order {

    String description;

    public Order(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
