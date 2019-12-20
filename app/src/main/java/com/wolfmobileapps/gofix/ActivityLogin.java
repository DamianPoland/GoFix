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
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
                Login login = new Login(email, haslo);

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

        // button zapomniałem hasła
        buttonZapomnialemHasla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something
            }
        });
    }

    // wysłąnie loginu i hasła
    public void sendLogin(String Url, JSONObject json) {

        Log.d(TAG, "sendLogin: JSONObject: " + json);
        Log.d(TAG, "sendLogin: Url: " + Url);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Url; //url
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {

                    // zapisanie tokenu do shar pref
                    String token = response.getString("token");
                    editor.putString(C.KEY_FOR_SHAR_TOKEN, token);
                    editor.apply();
                    Log.d(TAG, "onResponse token: " + token);

                    //otwarcie nowego activity i zamknięcie tego
                    Intent intent = new Intent(ActivityLogin.this, ActivityIndustries.class);
                    startActivity(intent);
                    finish();

                    // toast o poprawnym logowaniu
                    Toast.makeText(ActivityLogin.this, "Zalogowano", Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // do something when don"t getJSONObject
                Log.d(TAG, "onErrorResponse: " + error);

            }
        }) {
            //Network response - jeśili jest inny niż 200 to pokaże error
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                int mStatusCode = response.statusCode; //Network response np 200 czyli success
                Log.d(TAG, "parseNetworkResponse: " + mStatusCode);
                if (mStatusCode != 200) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ActivityLogin.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return super.parseNetworkResponse(response);
            }
        };
        queue.add(jsonObjectRequest); //wywołanie klasy
    }

    // utworzenie alert Didalog
    public void showAlertDialog(String alertMessage){
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLogin.this);
        builder.setTitle("Error");
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create();
        builder.show();
    }
}




// klasa do wysłąnia loginu i hasła na serwer ____________________________________________________________________________________
class Login {

    String email;
    String password;

    public Login(String email, String password) {
        this.email = email;
        this.password = password;
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
}
