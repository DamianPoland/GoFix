package com.wolfmobileapps.gofix;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ActivityCraftsmanOfferToSend extends AppCompatActivity {

    private static final String TAG = "AcCraftsmanOfferToSend";

    //views
    EditText editTextCraftsmanOfferDescription;
    EditText editTextCraftsmanOfferPrice;
    Button buttonSendCraftsmanOfferDescription;

    // dane z poprzedniego intent
    int orderIdFromIntent;
    int craftsmanIdFromIntent;

    //shared pred
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_craftsman_offer_to_send);

        //views
        editTextCraftsmanOfferDescription = findViewById(R.id.editTextCraftsmanOfferDescription);
        editTextCraftsmanOfferPrice = findViewById(R.id.editTextCraftsmanOfferPrice);
        buttonSendCraftsmanOfferDescription = findViewById(R.id.buttonSendCraftsmanOfferDescription);

        // pobranie danych z poprzedniego Intent
        Intent intentFromPrevActivity = getIntent();
        orderIdFromIntent = intentFromPrevActivity.getIntExtra("orderId", -1);

        // shar pref
        shar = getSharedPreferences("sharName", MODE_PRIVATE);
        editor = shar.edit();

        // button to send offer
        buttonSendCraftsmanOfferDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // sprawdzenie czy editTextCraftsmanOfferDescription nie jest null
                if (editTextCraftsmanOfferDescription.getText() == null) {
                    showAlertDialog("Error", "Dodaj opis"); // utworzenie alert Didalog
                    return;
                }
                // sprawdzenie czy editTextCraftsmanOfferDescription nie jest ""
                if (editTextCraftsmanOfferDescription.getText().toString().trim().equals("")) {
                    showAlertDialog("Error", "Dodaj opis"); // utworzenie alert Didalog
                    return;
                }
                // sprawdzenie czy editTextCraftsmanOfferPrice nie jest null
                if (editTextCraftsmanOfferPrice.getText() == null) {
                    showAlertDialog("Error", "Dodaj cenę"); // utworzenie alert Didalog
                    return;
                }
                // sprawdzenie czy editTextCraftsmanOfferPrice nie jest ""
                if (editTextCraftsmanOfferPrice.getText().toString().trim().equals("")) {
                    showAlertDialog("Error", "Dodaj cenę"); // utworzenie alert Didalog
                    return;
                }
                // sprawdzenie czy editTextCraftsmanOfferPrice nie jest za duża
                if (editTextCraftsmanOfferPrice.getText().toString().length() > 5) {
                    showAlertDialog("Error", "Za wysoka cena (max 99.999 zł)"); // utworzenie alert Didalog
                    return;
                }

                // pobranie danych i utworzenie obiektu CraftsmanOfferToSend
                int orderId = orderIdFromIntent;
                String details = editTextCraftsmanOfferDescription.getText().toString();
                int price = Integer.parseInt(editTextCraftsmanOfferPrice.getText().toString());
                Log.d(TAG, "onClick: \norderId: " + orderId + "\ndetails: " + details + "\nprice: " + price);
                CraftsmanOfferToSend craftsmanOfferToSend = new CraftsmanOfferToSend(orderId, details, price);

                // wysłanie obiektu CraftsmanOfferToSend na server
                Gson gson = new Gson();
                String descriptionString = gson.toJson(craftsmanOfferToSend);
                try {
                    JSONObject jsonCraftsmanOffer = new JSONObject(descriptionString);

                    // metoda wysyłająca na server
                    RequestQueue queue = Volley.newRequestQueue(ActivityCraftsmanOfferToSend.this);
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
                    queue.add(jsonObjectRequest); //wywołanie klasy

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    // utworzenie alert Didalog
    public void showAlertDialog(final String titule, String alertMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCraftsmanOfferToSend.this);
        builder.setTitle(titule);
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (titule.equals("Potwierdzenie")){
                    startActivity(new Intent(ActivityCraftsmanOfferToSend.this, ActivityIndustries.class));
                    finish();
                }
            }
        }).create();
        builder.show();
    }
}

class CraftsmanOfferToSend {

    int orderId;
    String details;
    int price;

    public CraftsmanOfferToSend(int orderId, String details, int price) {
        this.orderId = orderId;
        this.details = details;
        this.price = price;
    }
}
