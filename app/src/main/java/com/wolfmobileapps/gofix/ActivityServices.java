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

    // dane do następnego intent do złożenia zamówienia
    private int positionOfIndustryFromListViewFromIntent; // position czyli

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

        // list Serwices
        final ArrayList<Services> listOfServices = new ArrayList<>();

        //podbranie intent i danych z porzedniego activity
        Intent intent = getIntent();
        positionOfIndustryFromListViewFromIntent = intent.getIntExtra("currentIndustry", 0); // pobranie ID danego Industry z intent
        String stringJSon = shar.getString(C.KEY_FOR_SHAR_INDUSTRIES_AND_SERVICES, "[]"); // pobranie stringa z shared pref ze wszystkimi Industries i Services i zamiana na JSonArray
        try {
            JSONArray jsonArrayOfAllIndustries = new JSONArray(stringJSon); // JSONArray wszystkiego pobrana ze stringa

            // dodanie nazwy i jsonObject zgodnej z pobranym ID do text view
            JSONObject jsonObject = null;
            for (int i = 0; i < jsonArrayOfAllIndustries.length(); i++) {
                JSONObject currentJSONObjectForName = jsonArrayOfAllIndustries.getJSONObject(i);
                int currrentObjectID = currentJSONObjectForName.getInt("id");
                if (positionOfIndustryFromListViewFromIntent == currrentObjectID) {
                    textViewService.setText(currentJSONObjectForName.getString("name")); // dodanie nazwy zgodnej z pobranym ID do text view
                    jsonObject = currentJSONObjectForName; // dodanie obiektu zgodnego z pobranym ID żeby potem rozpakować servisy
                }
            }

            // dodanie services do listy
            JSONArray currentArrayofServices = jsonObject.getJSONArray("services");
            for (int i = 0; i < currentArrayofServices.length(); i++) {
                JSONObject currentJSONObject = currentArrayofServices.getJSONObject(i);
                String currentName = currentJSONObject.getString("name");
                int currentId = currentJSONObject.getInt("id");
                int currentIndustry_id = currentJSONObject.getInt("industry_id");
                listOfServices.add(new Services(currentId, currentIndustry_id, currentName));
            }

        } catch (JSONException e) {
            Log.d(TAG, "JSONException: " + e);
        }


        // ustawienie adaptera
        AdapterForServices adapter = new AdapterForServices(this, 0, listOfServices);
        listViewService.setAdapter(adapter);

        // list View on Click
        listViewService.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // jeśli ktoś jest NIE zalogowany czyli nie ma zapisanego tokenu to odeśle go do strony logowania
                if (shar.getString(C.KEY_FOR_SHAR_TOKEN, "").equals("")) {
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

                } else if (shar.getBoolean(C.KEY_FOR_SHAR_IS_CRAFTSMAN, false)){

                    // jeśli jest to craftsman to nie może dodać zlecenia
                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityServices.this);
                    builder.setTitle("Informacja");
                    builder.setMessage("Jesteś wykonawcą i nie możasz dodawać zleceń - przejdż do panelu użytkownika na stronie głównej");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).create();
                    builder.show();

                } else{

                    // jeśli ktoś jest zalogowany czyli ma zapisany token w shar pref to ominie logowanie i przejdzie dalej
                    Intent intentToOrderDescription = new Intent(ActivityServices.this, ActivityOrderDescription.class);
                    intentToOrderDescription.putExtra(C.KEY_FOR_INTENT_INDUSTRY_ID, positionOfIndustryFromListViewFromIntent); // ID wybranej branży czyli industry
                    intentToOrderDescription.putExtra(C.KEY_FOR_INTENT_INDUSTRY_NAME, textViewService.getText()); // nazwa wybranej branży czyli industry
                    Services currentService = (Services) listViewService.getItemAtPosition(position);
                    intentToOrderDescription.putExtra(C.KEY_FOR_INTENT_SERVICE_ID, currentService.getId()); // ID wybranej PODbranży czyli Service
                    intentToOrderDescription.putExtra(C.KEY_FOR_INTENT_SERVICE_NAME, currentService.name); // nazwa wybranej PODbranży czyli Service
                    startActivity(intentToOrderDescription);
                }
            }
        });
    }
}

class Services {
    int id;
    int industry_id;
    String name;

    public Services(int id, int industry_id, String name) {
        this.id = id;
        this.industry_id = industry_id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIndustry_id() {
        return industry_id;
    }

    public void setIndustry_id(int industry_id) {
        this.industry_id = industry_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
