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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityUserOffers extends AppCompatActivity {

    private static final String TAG = "ActivityUserOffers";

    //views
    private TextView textViewUserNoOffersFromCraftsman;
    private ListView listViewCraftsmanOffersForUser;

    //shared pred
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    private int orderID;

    // offerID do wysłąnia na server jak się wybierze danego craftsmana
    private int offerID;


    // lista zleceń
    private ArrayList<OfferFromCraftsman> listOfOfferFromCraftsman;
    private AdapterForOfferFromCraftsman adapterForOfferFromCraftsman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_offers);

        //views
        textViewUserNoOffersFromCraftsman = findViewById(R.id.textViewUserNoOffersFromCraftsman);
        listViewCraftsmanOffersForUser = findViewById(R.id.listViewCraftsmanOffersForUser);

        // shar pref
        shar = getSharedPreferences("sharName", MODE_PRIVATE);
        editor = shar.edit();

        // pobranie order ID z Intent
        Intent intent = getIntent();
        orderID = intent.getIntExtra(C.KEY_FOR_INTENT_TO_ORDER_ID, 0);

        // lista + ściągnięcie z neta
        listOfOfferFromCraftsman = new ArrayList<>();
        // pobranie danych do listOfOfferFromCraftsman
        getDataFromUrl();

        // ustawienie adaptera
        adapterForOfferFromCraftsman = new AdapterForOfferFromCraftsman(this, 0, listOfOfferFromCraftsman);
        listViewCraftsmanOffersForUser.setAdapter(adapterForOfferFromCraftsman);

        // listViewCraftsmanOffersForUser
        listViewCraftsmanOffersForUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //  pobranie aktualnego offerID
                OfferFromCraftsman offerFromCraftsman = (OfferFromCraftsman) listViewCraftsmanOffersForUser.getItemAtPosition(position);
                offerID = offerFromCraftsman.getId();

                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityUserOffers.this);
                builder.setTitle("Oferta");
                builder.setMessage("Czy chcesz wybrać tego wykonawcę do swojego zlecenia?");
                builder.setPositiveButton("TAK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendChosedCraftsmanToServer();
                    }
                });
                builder.setNegativeButton("NIE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
                builder.show();
            }
        });
    }

    // pobranie listy zleceń i dodanie do listView
    public void getDataFromUrl() {

        RequestQueue queue = Volley.newRequestQueue(this); // utworzenie requst - może być inne np o stringa lub JsonArrray
        String url = C.API + "client/order/" + orderID + "/offers"; //url

        Log.d(TAG, "sendLogin: Url: " + url);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, TAG + ", onResponse: response: " + response);

                // jeśli jest pusty JSON to wyłączy
                if (response.toString().equals("[]")) {
                    return;
                }

                // ukrycie textViewNoOrders
                textViewUserNoOffersFromCraftsman.setVisibility(View.INVISIBLE);

                // pobranie danych i wrzucenie do listy listOfOfferFromCraftsman
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        int order_id = jsonObject.getInt("order_id");
                        float craftsman_rating = jsonObject.getInt("craftsman_rating");
                        String details = jsonObject.getString("details");
                        String price = jsonObject.getString("price");
                        String craftsman_name = jsonObject.getString("craftsman_name");
                        listOfOfferFromCraftsman.add(new OfferFromCraftsman(id, order_id, craftsman_rating, details, price, craftsman_name));
                        adapterForOfferFromCraftsman.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
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

    // metoda wysyła info że oferta jest zaakceptowana
    public void sendChosedCraftsmanToServer() {

        RequestQueue queue = Volley.newRequestQueue(this); // utworzenie requst - może być inne np o stringa lub JsonArrray
        String url = C.API + "client/offer/" + offerID + "/pick"; //url

        Log.d(TAG, "sendLogin: Url: " + url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, TAG + ", onResponse: response: " + response);
                showAlertDialog("Wybrałeś ofertę tego Wykonawcy"); // alert dialog z opisem jak się wszystko uda i przejście do głównego activity


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "getDataFromUrl.onErrorResponse: " + error);
                showAlertDialog("Nie udało się wybrać oferty tego Wykonawcy \n" + error.toString()); // alert dialog z opisem jak się NIE uda
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
        queue.add(jsonObjectRequest); //wywołanie klasy
    }

    // utworzenie alert Didalog
    public void showAlertDialog(final String alertMessage) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityUserOffers.this);
        builder.setTitle("Oferta");
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // jeśli dostanie odpowiedż z Api że wybrano wykonawcę to przejdzie do głównego Activity żeby odświerzyć wszystko
                if(alertMessage.equals("Wybrałeś ofertę tego Wykonawcy")){
                    startActivity(new Intent(ActivityUserOffers.this, ActivityIndustries.class));
                    finish();
                }
            }
        }).create();

        builder.show();
    }
}


class OfferFromCraftsman {
    int id;
    int order_id;
    float craftsman_rating;
    String details;
    String price;
    String craftsman_name;

    public OfferFromCraftsman(int id, int order_id, float craftsman_rating, String details, String price, String craftsman_name) {
        this.id = id;
        this.order_id = order_id;
        this.craftsman_rating = craftsman_rating;
        this.details = details;
        this.price = price;
        this.craftsman_name = craftsman_name;
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

    public float getCraftsman_rating() {
        return craftsman_rating;
    }

    public void setCraftsman_rating(float craftsman_rating) {
        this.craftsman_rating = craftsman_rating;
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

    public String getCraftsman_name() {
        return craftsman_name;
    }

    public void setCraftsman_name(String craftsman_name) {
        this.craftsman_name = craftsman_name;
    }
}
