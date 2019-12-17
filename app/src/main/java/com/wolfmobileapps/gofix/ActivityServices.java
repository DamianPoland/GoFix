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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityServices extends AppCompatActivity {

    private static final String TAG = "ActivityServices";

    //views
    private TextView textViewService;
    private ListView listViewService;

    //shared pred
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        //views
        textViewService = findViewById(R.id.textViewService);
        listViewService = findViewById(R.id.listViewService);

        // action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Kategorie");

        // shar pref
        shar = getSharedPreferences("sharName", MODE_PRIVATE);
        editor = shar.edit();

        // list podBranż
        ArrayList<String> listOfServices = new ArrayList<>();

        //podbranie danych do wyświetlenia z intent i z shar pref
        final Intent intent = getIntent();
        if (intent.hasExtra("currentIndustry")){

            // opbranie position z intent
            int position = intent.getIntExtra("currentIndustry", 0);

            // pobranie stringa z shared pref i zamiana na JSonArray
            String stringJSon = shar.getString(C.KEY_FOR_SHAR_INDUSTRIES_AND_SERVICES, "");
            try {
                JSONArray jsonArrayOfAllIndustries = new JSONArray(stringJSon); // JSONArray wszystkiego pobrana ze stringa

                // pobranie nazwy głównej branży i dodanie do texView
                JSONObject jsonObject = jsonArrayOfAllIndustries.getJSONObject(position);
                String name = jsonObject.getString("name");
                textViewService.setText(name);

                // dodanie podbranż do listy
                JSONArray currentArrayofServices = jsonObject.getJSONArray("services");
                for (int i = 0; i < currentArrayofServices.length(); i++) {
                    JSONObject currentJSONObject = currentArrayofServices.getJSONObject(i);
                    String currentName  = currentJSONObject.getString("name");
                    listOfServices.add(currentName);
                }

            } catch (JSONException e) {
                Log.d(TAG, "JSONException: " + e);
            }
        }

        // ustawienie adaptera
        AdapterForServices adapter = new AdapterForServices(this,0,listOfServices);
        listViewService.setAdapter(adapter);

        // list View on Click
        listViewService.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // jeśli ktoś jest NIE zalogowany czyli nie ma zapisanego tokenu to odeśle go do strony logowania
                if (shar.getString(C.KEY_FOR_SHAR_TOKEN, "").equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityServices.this);
                    builder.setTitle("Logowanie");
                    builder.setMessage("Aby przejść dalej musisz być zalogowany");
                    builder.setPositiveButton("Zaloguj", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent currentIntent = new Intent(ActivityServices.this, ActivityLogin.class);
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
                    Intent intent1 = new Intent(ActivityServices.this, ActivityOrderDescription.class);
                    startActivity(intent1);
                }

            }
        });
    }
}
