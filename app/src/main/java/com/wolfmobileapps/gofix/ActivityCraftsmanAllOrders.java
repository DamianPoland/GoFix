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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
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

public class ActivityCraftsmanAllOrders extends AppCompatActivity {

    private static final String TAG = "CraftsmanAllOrders";

    //views
    TextView textViewNoOrdersCraftsman;
    ListView listViewOfOrdersCraftsman;
    ProgressBar progressBarWaiting;

    //shared pred
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    // lista zleceń
    private ArrayList<OrderCraftsman> listOfCraftsmanOrders;
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
        progressBarWaiting = findViewById(R.id.progressBarWaiting);

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
        ServicesAndIndustryName order = new ServicesAndIndustryName();
        listOfIndustriesAndServicesAcoordingToServiceID.addAll(order.putIndustriesAndServicesWithIDToArray (this));

        //list view z ofertami, onClick jeśli ma być wysłąna oferta do danego OrderCraftsman
        listViewOfOrdersCraftsman.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                // jeśli liczba punktów jest mniejsza od 1 to craftsman nie moze wystawiać ofert
                if (shar.getInt(C.KEY_FOR_BALANCE_SHAR, 0) < 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCraftsmanAllOrders.this);
                    builder.setTitle("Brak punktów");
                    builder.setMessage("Nie masz wystarczającej liczby punków aby wysłać ofertę. Doładuj konto aby móc dodawać oferty.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).create();
                    builder.show();
                } else {

                    // utworzenie alert dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCraftsmanAllOrders.this);
                    builder.setTitle("Oferta");
                    builder.setMessage("Czy chcesz wysłać ofertę do tego zlecenia?");
                    builder.setPositiveButton("TAK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // zrezygnowaklient z tego - opis o co chodzi jest w ActivityCraftsmanOfferToSend
//                            Intent currentIntent = new Intent(ActivityCraftsmanAllOrders.this, ActivityCraftsmanOfferToSend.class);
//                            OrderCraftsman currentOrder = (OrderCraftsman) listViewOfOrdersCraftsman.getItemAtPosition(position);
//                            int orderId = currentOrder.getId();
//                            currentIntent.putExtra("orderId", orderId);
//                            startActivity(currentIntent);
//                            finish();

                            // zamiast tego wyżej odrazu wysyła ofertę craftsmana na serwer

                            // pokazanie czekania po wysłąniu ofery
                            progressBarWaiting.setVisibility(View.VISIBLE);

                            // pobranie danych i utworzenie obiektu CraftsmanOfferToSend
                            OrderCraftsman currentOrder = (OrderCraftsman) listViewOfOrdersCraftsman.getItemAtPosition(position);
                            int orderId = currentOrder.getId();
                            String details = "empty";
                            int price = 0;
                            Log.d(TAG, "onClick: \norderId: " + orderId + "\ndetails: " + details + "\nprice: " + price);
                            CraftsmanOfferToSend craftsmanOfferToSend = new CraftsmanOfferToSend(orderId, details, price);

                            // wysłanie obiektu CraftsmanOfferToSend na server
                            Gson gson = new Gson();
                            String descriptionString = gson.toJson(craftsmanOfferToSend);
                            try {
                                JSONObject jsonCraftsmanOffer = new JSONObject(descriptionString);

                                // metoda wysyłająca na server
                                RequestQueue queue = Volley.newRequestQueue(ActivityCraftsmanAllOrders.this);
                                String url = C.API + "craftsman/offer"; //url
                                Log.d(TAG, "sendLogin: url: " + url);
                                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonCraftsmanOffer, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                        // po udanym wysłaniu - zwraca pusty JSON
                                        showAlertDialog("Potwierdzenie", "Zlecenie zostało wysłane");
                                        Log.d(TAG, "JSONObject response: " + response.toString());
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        // do something when error
                                        int errorCodeResponse = error.networkResponse.statusCode; // jeśli inny niż 200 to tu się pojawi cod błędu i trzeba go obsłużyć, jeśli 200 to succes i nie włączt wogle metody onErrorResponse
                                        Log.d(TAG, "onErrorResponse: resp: " + errorCodeResponse);
                                        if (errorCodeResponse == 401) {
                                            showAlertDialog("Error", "Błąd autoryzacji. Zaloguj się ponownie");
                                        }
                                        if (errorCodeResponse == 422) {
                                            showAlertDialog("Error", "Błąd danych");
                                        }
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

                                // liczba ponownych requestów to zero i czeka 50s
                                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                                        50000,
                                        0,
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                                queue.add(jsonObjectRequest); //wywołanie klasy

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).setNegativeButton("NIE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do something when click cancel
                        }
                    }).create();
                    builder.show();
                }
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

                Log.d(TAG, "onResponse: response: " + response);

                // jeśli jest pusty JSON to wyłączy
                if (response.toString().equals("[]")) {
                    return;
                }

                // ukrycie textViewNoOrders
                textViewNoOrdersCraftsman.setVisibility(View.INVISIBLE);

                // dodanie listy Orders z response
                OrderCraftsman order = new OrderCraftsman(ActivityCraftsmanAllOrders.this);
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

    // utworzenie alert Didalog
    public void showAlertDialog(final String titule, String alertMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCraftsmanAllOrders.this);
        builder.setTitle(titule);
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (titule.equals("Potwierdzenie")){
                    startActivity(new Intent(ActivityCraftsmanAllOrders.this, ActivityCraftsmanData.class));
                    finish();
                }
            }
        }).create();
        builder.show();
    }
}
