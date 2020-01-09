package com.wolfmobileapps.gofix;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityUserHistory extends AppCompatActivity {

    private static final String TAG = "ActivityUserHistory";

    //views
    private TextView textViewUserHistoryNoOrders;
    private ListView listViewUserHistory;

    //shared pred
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    // tak samo jak w ActivityUserMain -  lista zleceń
    private ArrayList<OrderUser> listOfUserOrdersHistory;
    private AdapterForUserOrders adapterForUserOrdersHistory;

    //tak samo jak w ActivityUserMain -  list Serwices
    private ArrayList<ServicesAndIndustryName> listOfIndustriesAndServicesAcoordingToServiceID = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_history);

        //views
        textViewUserHistoryNoOrders = findViewById(R.id.textViewUserHistoryNoOrders);
        listViewUserHistory = findViewById(R.id.listViewUserHistory);

        // shar pref
        shar = getSharedPreferences("sharName", MODE_PRIVATE);
        editor = shar.edit();

        // tak samo jak w ActivityUserMain -  lista + ściągnięcie z neta
        listOfUserOrdersHistory = new ArrayList<>();
        getDataFromUrl();

        // tak samo jak w ActivityUserMain -  ustawienie adaptera
        adapterForUserOrdersHistory = new AdapterForUserOrders(this, 0, listOfUserOrdersHistory);
        listViewUserHistory.setAdapter(adapterForUserOrdersHistory);

        //tak samo jak w ActivityUserMain - podbranie nazwy Industry, industryID, nazwy Service i serviceID i zapisanie do array listOfIndustriesAndServicesAcoordingToServiceID
        ServicesAndIndustryName servicesAndIndustryName = new ServicesAndIndustryName();
        listOfIndustriesAndServicesAcoordingToServiceID.addAll(servicesAndIndustryName.putIndustriesAndServicesWithIDToArray(this));
    }

    // pobranie listy zleceń i dodanie do listView
    public void getDataFromUrl () {

        RequestQueue queue = Volley.newRequestQueue(this); // utworzenie requst - może być inne np o stringa lub JsonArrray
        String url = C.API + "client/history"; //url
        Log.d(TAG, "sendLogin: Url: " + url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                // jeśli jest pusty JSON to wyłączy
                if (response.toString().equals("[]")) {
                    return;
                }

                // ukrycie textViewNoOrders
                textViewUserHistoryNoOrders.setVisibility(View.INVISIBLE);
                Log.d(TAG, "ActivityUserMain: response: " + response);


                // tak samo jak w ActivityUserMain -  dodanie listy Orders z response
                OrderUser orderUser = new OrderUser(ActivityUserHistory.this);
                listOfUserOrdersHistory.addAll(orderUser.putOrdersToArrayList(response, listOfIndustriesAndServicesAcoordingToServiceID));
                adapterForUserOrdersHistory.notifyDataSetChanged();




            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
