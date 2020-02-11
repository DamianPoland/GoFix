package com.wolfmobileapps.gofix;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ActivityLogin extends AppCompatActivity {

    private static final String TAG = "ActivityLogin";

    //views
    private EditText editTextEmail;
    private EditText editTextHaslo;
    private Button buttonZaloguj;
    private Button buttonZarejestruj;
    private Button buttonZapomnialemHasla;

    //shared pred
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //views
        editTextEmail = findViewById(R.id.editEmail);
        editTextHaslo = findViewById(R.id.editTextHaslo);
        buttonZaloguj = findViewById(R.id.buttonZaloguj);
        buttonZarejestruj = findViewById(R.id.buttonZarejestruj);
        buttonZapomnialemHasla = findViewById(R.id.buttonZapomnialemHasla);

        // action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Logowanie");

        // shar pref
        shar = getSharedPreferences("sharName", MODE_PRIVATE);
        editor = shar.edit();

        // button Zaloguj
        buttonZaloguj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // sprawdzenie czy editTextEmail nie jest null
                if (editTextEmail.getText() == null) {
                    showAlertDialog("Wpisz adres e-mail"); // utworzenie alert Didalog
                    return;
                }
                // sprawdzenie czy editTextEmail nie jest ""
                if (editTextEmail.getText().toString().trim().equals("")) {
                    showAlertDialog("Wpisz adres e-mail"); // utworzenie alert Didalog
                    return;
                }
                // sprawdzenie czy editTextHaslo nie jest null
                if (editTextHaslo.getText() == null) {
                    showAlertDialog("Wpisz hasło"); // utworzenie alert Didalog
                    return;
                }
                // sprawdzenie czy editTextHaslo nie jest ""
                if (editTextHaslo.getText().toString().trim().equals("")) {
                    showAlertDialog("Wpisz hasło"); // utworzenie alert Didalog
                    return;
                }

                // jeśli nie ma nulli i pustych stringów to wyśle login i hasło
                String email = editTextEmail.getText().toString();
                String haslo = editTextHaslo.getText().toString();
                String token_notifications = shar.getString(C.KEY_FOR_NOTIFICATIONS_SHAR, "");
                Login login = new Login(email, haslo, token_notifications);

                // zmiana instancji obiektu na jsona(trzeba zaimplementować GSON bibliotekę)
                Gson gson = new Gson();
                String jsonString = gson.toJson(login);
                try {
                    JSONObject json = new JSONObject(jsonString);

                    // wysłanie loginu i hasła
                    String url = C.API + "user/auth";
                    sendLogin(url, json);

                } catch (JSONException e) {
                    Log.d(TAG, "JSONException: " + e);
                }
            }
        });

        // button zarejestruj
        buttonZarejestruj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityLogin.this, ActivityRegistration.class);
                startActivity(intent);
            }
        });

        // button zapomniałem hasła otwiera http://www.gofix.pl
        buttonZapomnialemHasla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri webpage = Uri.parse("http://www.gofix.pl");
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }


    // wysłąnie loginu i hasła i tokena
    public void sendLogin(String Url, JSONObject json) {

        Log.d(TAG, "sendLogin: JSONObject: " + json);
        Log.d(TAG, "sendLogin: Url: " + Url);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Url; //url
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d(TAG, "JSONObject response: " + response.toString());

                try {

                    // zapisanie tokenu i is_craftsman do shar pref
                    String token = response.getString("token");
                    editor.putString(C.KEY_FOR_SHAR_TOKEN, token); // zapisanie da shar tokena
                    boolean is_craftsman = response.getBoolean("is_craftsman"); // pobranie info czy to zwykły user czy craftsman
                    editor.putBoolean(C.KEY_FOR_SHAR_IS_CRAFTSMAN, is_craftsman); // zapisanie do shar info czy to zwykły user czy craftsman
                    editor.apply();
                    Log.d(TAG, "onResponse token: " + token);
                    Log.d(TAG, "onResponse is_craftsman: " + is_craftsman);


                    // alert o poprawnym logowaniu
                    showAlertDialog(C.APPROPRIATE_LOGGING);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ActivityLogin.this, "Error", Toast.LENGTH_SHORT).show();
                // do something when error
                int errorCodeResponse = error.networkResponse.statusCode; // jeśli inny niż 200 to tu się pojawi cod błędu i trzeba go obsłużyć, jeśli 200 to succes i nie włączt wogle metody onErrorResponse
                Log.d(TAG, "onErrorResponse: resp: " + errorCodeResponse);

                try {
                    String errorDataResponse = new String(error.networkResponse.data, "UTF-8"); // rozpakowanie do stringa errorDataResponse który jest JSonem lub czymś innym
                    Log.d(TAG, "onErrorResponse: errorData: " + errorDataResponse);

                    // pobranie info z servera JSONA z errorem
                    JSONObject jsonObject = new JSONObject(errorDataResponse);
                    String eoorMessage = "";
                    // jeśli kod 422 to zły email lub hasło
                    if (errorCodeResponse == 422) {
                        JSONObject errorsJsonObject = jsonObject.getJSONObject("errors");

                        // pobranie info z servera o złym password i wyświetlenie w alert dialog
                        try {
                            JSONArray jsonArray = errorsJsonObject.getJSONArray("password");
                            eoorMessage = (String) jsonArray.get(0);
                        }
                        catch (JSONException e){}

                        // pobranie info z servera o złym emailu i wyświetlenie w alert dialog
                        try {
                            JSONArray jsonArray = errorsJsonObject.getJSONArray("email");
                            eoorMessage = (String) jsonArray.get(0);
                        }
                        catch (JSONException e){}

                        Log.d(TAG, "onErrorResponse: " + errorDataResponse);
                    }

                    if (errorCodeResponse == 401) {
                        eoorMessage = "Niepoprawny login lub hasło";
                        Log.d(TAG, "onErrorResponse: eoorMessage: " + eoorMessage); // wypada błąd invalid credetials
                    }

                    // wyświetlenie errora w alert dialog
                    showAlertDialog(eoorMessage);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }) {    //this is the part, that adds the header to the request
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json"); //  header format wysłanej wiadomości - JSON
                params.put("Accept", "application/json"); //  header format otrzymanej wiadomości -JSON
                params.put("Consumer", C.HEDDER_CUSTOMER); //  header Consumer
                return params;
            }
        };

        // liczba ponownych requestów to zero i czeka 50s
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsonObjectRequest); //wywołanie klasy
    }

    // utworzenie alert Didalog
    public void showAlertDialog(final String alertMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLogin.this);
        String titule = "Error";
        if (alertMessage.equals(C.APPROPRIATE_LOGGING)) {
            titule = C.TITULE_LOGGING;
        }
        builder.setTitle(titule);
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (alertMessage.equals(C.APPROPRIATE_LOGGING)) {
                    //otwarcie nowego activity i zamknięcie tego
                    Intent intent = new Intent(ActivityLogin.this, ActivityIndustries.class);
                    startActivity(intent);
                    finish();
                }
            }
        }).create();
        builder.show();
    }
}


// klasa do wysłąnia loginu i hasła na serwer ____________________________________________________________________________________
class Login {


    String email;
    String password;
    String token_notifications;

    public Login(String email, String password, String token_notifications) {
        this.email = email;
        this.password = password;
        this.token_notifications = token_notifications;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken_notifications() {
        return token_notifications;
    }

    public void setToken_notifications(String token_notifications) {
        this.token_notifications = token_notifications;
    }
}
