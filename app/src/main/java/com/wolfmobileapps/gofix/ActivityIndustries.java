package com.wolfmobileapps.gofix;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

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

public class ActivityIndustries extends AppCompatActivity {

    private static final String TAG = "ActivityIndustries";

    //views
    private ListView listViewIndustries;
    private ProgressBar progressBarIndustries;
    private Button buttonGoodCraftsMan;

    // lista branż
    private ArrayList<Industries> listOfIndustries;
    private AdapterForIndustries adapterForIndustries;

    //shared pred
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_industries);

        //views
        listViewIndustries = findViewById(R.id.listViewIndustries);
        progressBarIndustries = findViewById(R.id.progressBarIndustries);
        buttonGoodCraftsMan = findViewById(R.id.buttonGoodCraftsMan);

        // shar pref
        shar = getSharedPreferences("sharName", MODE_PRIVATE);
        editor = shar.edit();

        // lista branż + ściągnięcie z neta
        listOfIndustries = new ArrayList<>();
        getDataFromUrl(C.API + "industries");

        // ustawienie adaptera
        adapterForIndustries = new AdapterForIndustries(this,0,listOfIndustries);
        listViewIndustries.setAdapter(adapterForIndustries);

        // tymczasowe czyszczenie tokena w shar pref zeby sie właczało logowanie
//        editor.putString(C.KEY_FOR_SHAR_TOKEN, "");
//        editor.apply();

        // list View on Click
        listViewIndustries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // wysłanie z intent ID danego industry
                Industries currentIndustry = (Industries) listViewIndustries.getItemAtPosition(position);
                Intent intent = new Intent(ActivityIndustries.this, ActivityServices.class);
                intent.putExtra("currentIndustry", currentIndustry.getId());
                startActivity(intent);
            }
        });

        // button po czym poznać dobrego fachowca
        buttonGoodCraftsMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //narazie nic nie robi
            }
        });
    }

    // pobranie listy branż i dodanie do Array Adapter
    public void getDataFromUrl (String Url) {

        RequestQueue queue = Volley.newRequestQueue(this); // utworzenie requst - może być inne np o stringa lub JsonArrray
        String url = Url; //url
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                // ukrycie progress bara
                progressBarIndustries.setVisibility(View.INVISIBLE);

                // dodanie danych z Url do shar pref
                editor.putString(C.KEY_FOR_SHAR_INDUSTRIES_AND_SERVICES, response.toString());
                editor.apply();

                // pobranie JSonArray i zapisanie do listOfIndustries
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        int idOfCurrentIndustry = jsonObject.getInt("id");
                        String nameOfCurrentIndustry = jsonObject.getString("name");
                        listOfIndustries.add(new Industries(idOfCurrentIndustry,nameOfCurrentIndustry));
                        adapterForIndustries.notifyDataSetChanged();
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
        });
        queue.add(jsonArrayRequest); //wywołanie klasy
    }

    // do menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_industries, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_info:
                startActivity(new Intent(ActivityIndustries.this, ActivityInfo.class));
                break;

            case R.id.menu_user:
                // jeśli ktoś jest NIE zalogowany czyli nie ma zapisanego tokenu to odeśle go do strony logowania
                if (shar.getString(C.KEY_FOR_SHAR_TOKEN, "").equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityIndustries.this);
                    builder.setTitle("Logowanie");
                    builder.setMessage("Aby przejść do Panelu Użytkownika musisz być zalogowany");
                    builder.setPositiveButton("Zaloguj", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent currentIntent = new Intent(ActivityIndustries.this, ActivityLogin.class);
                            startActivity(currentIntent);
                            finish();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do something when click cancel
                        }
                    }).create();
                    builder.show();
                }else {
                    // jeśli ktoś jest zalogowany czyli ma zapisany token w shar pref to ominie logowanie i przejdzie dalej
                    startActivity(new Intent(ActivityIndustries.this, ActivityUserMain.class));
                }



                break;
        }
        return super.onOptionsItemSelected(item);//nie usuwać bo up button nie działa
    }

}


class Industries {

    int id;
    String name;

    public Industries(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
