package com.wolfmobileapps.gofix;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityUserMain extends AppCompatActivity {

    private static final String TAG = "ActivityUserMain";

    //views
    TextView textViewUserNoOrdersMain;
    Button buttonOrdersHistory;
    ListView listViewUserOrdersMain;

    //shared pred
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    // lista zleceń
    private ArrayList<OrderUser> listOfOrders;
    private AdapterForUserOrders adapterForUserOrders;

    // list Serwices
    private ArrayList<ServicesAndIndustryName> listOfIndustriesAndServicesAcoordingToServiceID = new ArrayList<>();

    //do ratingu craftsmana class RatingCraftsman
    private int ratingFromUserToCraftsman = 10; // przypisane 1 w rzie jakby niekliknął nic i żeby się nie wywaliło
    private int orderID;

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
        adapterForUserOrders = new AdapterForUserOrders(this, 0, listOfOrders);
        listViewUserOrdersMain.setAdapter(adapterForUserOrders);

        //podbranie nazwy Industry, industryID, nazwy Service i serviceID i zapisanie do array listOfIndustriesAndServicesAcoordingToServiceID
        ServicesAndIndustryName servicesAndIndustryName = new ServicesAndIndustryName();
        listOfIndustriesAndServicesAcoordingToServiceID.addAll(servicesAndIndustryName.putIndustriesAndServicesWithIDToArray(this));

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

                OrderUser orderUserCurrent = (OrderUser) listViewUserOrdersMain.getItemAtPosition(position);
                orderID = orderUserCurrent.getId(); // pobranie order ID danego itema OrderUser

                // jeśli będzie już wybrany wykonawca to przycisk będzie wystawiał ocenę a nie przenośił do listy wykonawców
                if (!orderUserCurrent.getCraftsman_name().equals("")) {
                    buildAlertDialogWithRating();
                    return;
                }

                //przypisanie offer ID i wysłąnie razem z intent
                Intent listIntent = new Intent(ActivityUserMain.this, ActivityUserOffers.class);
                listIntent.putExtra(C.KEY_FOR_INTENT_TO_ORDER_ID, orderID);
                startActivity(listIntent);

                Log.d(TAG, "onItemClick, ActivityUserMain,  orderID(offer): " + orderID);
            }
        });
    }

    // pobranie listy zleceń i dodanie do listView
    public void getDataFromUrl() {

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

    // alert do oceniania craftsmana
    private void buildAlertDialogWithRating() {

        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final LinearLayout ll = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        RatingBar rating = new RatingBar(this);
        rating.setLayoutParams(lp);
        rating.setNumStars(5);
        rating.setStepSize(0.5f);
        ll.addView(rating);
        rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ///zapisanie ratingu do zmiennej
                ratingFromUserToCraftsman = (int) (rating * 2);
                Log.d(TAG, "onRatingChanged: v: " + ratingFromUserToCraftsman);
            }
        });
        popDialog.setTitle("Ocena wykonawcy");
        popDialog.setView(ll);

        // button later
        popDialog
                //button OK
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // po kliknięciu OK
                                postDataToServerWithRating();

                            }
                        })

                // button LATER
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        popDialog.create();
        popDialog.show();
    }

    // wysłąnie ratingu danego craftsmana i zamknięcie zlecenia - przeniesienie do historii
    public void postDataToServerWithRating() {

        RatingCraftsman ratingCraftsman = new RatingCraftsman(orderID, ratingFromUserToCraftsman);

        Gson gson = new Gson();
        String descriptionString = gson.toJson(ratingCraftsman);
        try {
            JSONObject jsonRatingCraftsman = new JSONObject(descriptionString);


            RequestQueue queue = Volley.newRequestQueue(this); // utworzenie requst - może być inne np o stringa lub JsonArrray
            String url = C.API + "client/review/order"; //url
            Log.d(TAG, "sendLogin: Url: " + url);
            JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, url, jsonRatingCraftsman, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {


                    Log.d(TAG, TAG + ": response: " + response);
                    showAlertDialog();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // do something when don"t getJSONObject
                    Log.d(TAG, TAG + "getDataFromUrl.onErrorResponse: " + error);

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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // utworzenie alert Didalog
    public void showAlertDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ActivityUserMain.this);
        builder.setTitle("Potwierdzenie");
        builder.setMessage("Ocena wykonawcy została dodana");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                startActivity(new Intent(ActivityUserMain.this, ActivityIndustries.class));
                finish();

            }
        }).create();
        builder.show();
    }
}

class RatingCraftsman {
    int orderId;
    int rate;

    public RatingCraftsman(int orderId, int rate) {
        this.orderId = orderId;
        this.rate = rate;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}

