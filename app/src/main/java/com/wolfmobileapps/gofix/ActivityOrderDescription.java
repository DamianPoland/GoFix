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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ActivityOrderDescription extends AppCompatActivity {

    private static final String TAG = "ActivityOrderDescriptio";

    //views
    private TextView textViewIndustryInOrder;
    private TextView textViewServiceInOrder;
    private EditText editTextDescription;
    private Button buttonSendInDescription;

    // zmienne do wysłąnia naserwer
    private int industryID;
    private int serviceID;

    //shared pred
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_description);

        // views
        textViewIndustryInOrder = findViewById(R.id.textViewIndustryInOrder);
        textViewServiceInOrder = findViewById(R.id.textViewServiceInOrder);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonSendInDescription = findViewById(R.id.buttonSendInDescription);

        // action bar
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Nowe zlecenie");

        // shar pref
        shar = getSharedPreferences("sharName", MODE_PRIVATE);
        editor = shar.edit();

        // opbranie danych z intent i wstawienie do text Views
        Intent intentFromPreviousActivity = getIntent();
        industryID = intentFromPreviousActivity.getIntExtra(C.KEY_FOR_INTENT_INDUSTRY_ID, 0); // ID wybranej branży czyli industry
        String industryName = intentFromPreviousActivity.getStringExtra(C.KEY_FOR_INTENT_INDUSTRY_NAME); // nazwa wybranej branży czyli industry
        textViewIndustryInOrder.setText(industryName);
        serviceID = intentFromPreviousActivity.getIntExtra(C.KEY_FOR_INTENT_SERVICE_ID, 0); // ID wybranej PODbranży czyli Service
        String serviceName = intentFromPreviousActivity.getStringExtra(C.KEY_FOR_INTENT_SERVICE_NAME); // nazwa wybranej PODbranży czyli Service
        textViewServiceInOrder.setText(serviceName);
        Log.d(TAG, "onCreate Intent: \nindustryName: " + industryName + "\nserviceName: " + serviceName + "\nindustryID: " + industryID + "\nserviceID: " + serviceID);


        //button wysłąnie na server danych
        buttonSendInDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // sprawdzenie czy editTextDescription nie jest null
                if (editTextDescription.getText() == null) {
                    showAlertDialog("Error", "Dodaj opis"); // utworzenie alert Didalog
                    return;
                }
                // sprawdzenie czy editTextDescription nie jest ""
                if (editTextDescription.getText().toString().trim().equals("")) {
                    showAlertDialog("Error", "Dodaj opis"); // utworzenie alert Didalog
                    return;
                }
                // sprawdzenie czy editTextDescription nie jest < 5
                if (editTextDescription.getText().toString().length() < 5) {
                    showAlertDialog("Error", "Opis jest za krótki. Nusi być minimum 50 znaków"); // utworzenie alert Didalog
                    return;
                }

                // pobranie danych do wysłąnia
                int industryIDToSend = industryID; // to nie jest potrzebne do wysyłania do API bo ID service jest jednoznaczne
                int serviceIDToSend = serviceID;
                String orderText = editTextDescription.getText().toString();
                Log.d(TAG, "OrderCraftsman: \nindustryIDToSend: " + industryIDToSend + "\nserviceIDToSend: " + serviceIDToSend + "\norderText: " + orderText);

                // wysłanie danych do API
                String apiUrl = C.API + "client/order";
                Gson gson = new Gson();
                Description descriptionItem = new Description(serviceIDToSend, orderText);
                String descriptionString = gson.toJson(descriptionItem );
                try {
                    JSONObject jsonObjectToken = new JSONObject(descriptionString);
                    sendOrderDescriptiontoSerwer(apiUrl,jsonObjectToken); // metoda wysyłająca na server
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // utworzenie alert Didalog
    public void showAlertDialog(final String titule, String alertMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityOrderDescription.this);
        builder.setTitle(titule);
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // jeśli będzie to alert wywołany przez wysłąne zlecenie to zamknie okno i przejdzie do początku
                if (titule.equals("Potwierdzenie")) {
                    startActivity(new Intent(ActivityOrderDescription.this, ActivityIndustries.class));
                    finish();
                }

            }
        }).create();
        builder.show();
    }

    // wysłanie zlecenia na serwer
    public void sendOrderDescriptiontoSerwer(String Url, JSONObject json) {

        Log.d(TAG, "sendLogin: JSONObject: " + json);
        Log.d(TAG, "sendLogin: Url: " + Url);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Url; //url
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
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
                if (errorCodeResponse == 401 ) {
                    showAlertDialog("Error", "Błąd autoryzacji. Zaloguj się ponownie");
                }
                if (errorCodeResponse == 422 ) {
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
    }
}

class Description {
    int serviceId;
    String description;

    public Description(int serviceId, String description) {
        this.serviceId = serviceId;
        this.description = description;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
