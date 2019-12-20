package com.wolfmobileapps.gofix;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Spinner;

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


public class ActivityRegistration extends AppCompatActivity {

    private static final String TAG = "ActivityRegistration";

    //views
    private LinearLayout linLayMain;
    private LinearLayout linLayCity;
    private LinearLayout linLayCraftsMan;
    private LinearLayout linLayPassword;
    private LinearLayout linLayTokenNumber;
    private Spinner spinnerTypeOfUser;
    private Spinner spinnerRegions;
    private Spinner spinnerIndustriesToChose;
    private GridView gridViewServices;
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextCity;
    private EditText editTextPhoneNumber;
    private EditText editTextPassword;
    private EditText editTextPasswordChecked;
    private EditText editTextTokenFromEmail;
    private Button buttonRegistry;
    private Button buttonLogin;

    //shared pred
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    // lista regions
    private ArrayList<String> listRegionsStrings; // lista Stringów do wyświetlania w spinnerze
    private ArrayList<Integer> listRegionsId; // równoległa lista Id Stringów ze spinnera
    private ArrayAdapter<String> spinnerArrayAdapterRegions;

    // lista Industries i Services
    private ArrayList<String> listOfIndustriesString; // lista Stringów do wyświetlania w spinnerze
    private ArrayList<Integer> listOfIndustriesId; // równoległa lista Id Stringów ze spinnera
    private ArrayList<Services> listOfCurrentServises;
    private ArrayList<String> listOfCurrentServisesToString; // lista samych stringów wyciągnięta z listOfCurrentServises żeby dodać do checkboxów
    ArrayAdapter<String> spinnerArrayAdapterIndustries;

    //dane do wysłania na server
    boolean isCraftsman;
    int regionID;
    int industriesID;
    private ArrayList<Integer> listOfServicesIdToSend;

    // JSon Array wszystkich Industries i Services pobrana z API
    private JSONArray jsonArrayOfAllIndustries;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //views
        linLayMain = findViewById(R.id.linLayMain);
        linLayCity = findViewById(R.id.linLayCity);
        linLayCraftsMan = findViewById(R.id.linLayCraftsMan);
        linLayPassword = findViewById(R.id.linLayPassword);
        linLayTokenNumber = findViewById(R.id.linLayTokenNumber);
        spinnerTypeOfUser = findViewById(R.id.spinnerTypeOfUser);
        spinnerRegions = findViewById(R.id.spinnerRegions);
        spinnerIndustriesToChose = findViewById(R.id.spinnerIndustriesToChose);
        gridViewServices = findViewById(R.id.gridViewServices);
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextCity = findViewById(R.id.editTextCity);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPasswordChecked = findViewById(R.id.editTextPasswordChecked);
        editTextTokenFromEmail = findViewById(R.id.editTextTokenFromEmail);
        buttonRegistry = findViewById(R.id.buttonRegistry);
        buttonLogin = findViewById(R.id.buttonLogin);

        // action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Rejestracja");

        // shar pref
        shar = getSharedPreferences("sharName", MODE_PRIVATE);
        editor = shar.edit();

        // metoda spinnera do wyboru czy specjalista czy zleceniodawca
        spinerTypeOfUser();

        // metoda spinnera do wyboru województwa
        getDataRegionsAndPutOnSpinner(C.API + "regions");

        // instancja różnych list
        listOfIndustriesString = new ArrayList<>(); // lista Stringów do wyświetlania w spinnerze
        listOfIndustriesId = new ArrayList<>(); // równoległa lista Id Stringów ze spinnera
        listOfCurrentServises = new ArrayList<>();
        listOfCurrentServisesToString = new ArrayList<>();
        listOfServicesIdToSend = new ArrayList<>();

        // pobranie danych industries i services z shar i dodanie do spinnera
        getIndustriesAndSerivesAndPutOnSpinner();



        // button rejestracja
        buttonRegistry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // sprawdzenie czy editTextName nie jest null
                if (editTextName.getText() == null) {
                    showAlertDialog("Wpisz imię"); // utworzenie alert Didalog
                    return;
                }
                // sprawdzenie czy editTextName nie jest ""
                if (editTextName.getText().toString().trim().equals("")) {
                    showAlertDialog("Wpisz imię"); // utworzenie alert Didalog
                    return;
                }
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
                // sprawdzenie czy editTextPassword nie jest null
                if (editTextPassword.getText() == null) {
                    showAlertDialog("Wpisz hasło"); // utworzenie alert Didalog
                    return;
                }
                // sprawdzenie czy editTextPassword nie jest ""
                if (editTextPassword.getText().toString().trim().equals("")) {
                    showAlertDialog("Wpisz hasło"); // utworzenie alert Didalog
                    return;
                }
                // sprawdzenie czy editTextPasswordChecked nie jest null
                if (editTextPasswordChecked.getText() == null) {
                    showAlertDialog("Wpisz ponownie hasło"); // utworzenie alert Didalog
                    return;
                }
                // sprawdzenie czy editTextPasswordChecked nie jest ""
                if (editTextPasswordChecked.getText().toString().trim().equals("")) {
                    showAlertDialog("Wpisz ponwnie hasło"); // utworzenie alert Didalog
                    return;
                }
                // sprawdzenie czy editTextPasswordChecked nie jest ""
                if (!editTextPassword.getText().toString().equals(editTextPasswordChecked.getText().toString())) {
                    showAlertDialog("Hasła są różne"); // utworzenie alert Didalog
                    return;
                }
                // sprawdzenie pola  editTextCity
                if (!isCraftsman){
                    // sprawdzenie czy editTextCity nie jest null
                    if (editTextCity.getText() == null) {
                        showAlertDialog("Wpisz nazwę miasta"); // utworzenie alert Didalog
                        return;
                    }
                    // sprawdzenie czy editTextCity nie jest ""
                    if (editTextCity.getText().toString().trim().equals("")) {
                        showAlertDialog("Wpisz nazwę miasta"); // utworzenie alert Didalog
                        return;
                    }
                }
                //sprawdzenie pola editTextPhoneNumber
                else {
                    // sprawdzenie czy editTextPhoneNumber nie jest null
                    if (editTextPhoneNumber.getText() == null) {
                        showAlertDialog("Wpisz numer telefonu"); // utworzenie alert Didalog
                        return;
                    }
                    // sprawdzenie czy editTextPhoneNumber nie jest ""
                    if (editTextPhoneNumber.getText().toString().trim().equals("")) {
                        showAlertDialog("Wpisz numer telefonu"); // utworzenie alert Didalog
                        return;
                    }
                    // sprawdzenie czy editTextPhoneNumber nie jest dłuższy lub krótszy niż 9 cyfr
                    if (editTextPhoneNumber.getText().toString().trim().length() != 9) {
                        showAlertDialog("Numer telefonu jest niepoprawny"); // utworzenie alert Didalog
                        return;
                    }
                }

                // pobranie wszystkich danych
                boolean craftsmanOfUser = isCraftsman; // jeśli jest true to craftsman a jeśli false to user
                int region = regionID;
                String name = editTextName.getText().toString();
                String eMail = editTextEmail.getText().toString().trim();
                String city = "";
                String phoneNumber = "";

                int industries = -1; //liczba domyślna która nie istnieje
                if (!isCraftsman){
                    city = editTextCity.getText().toString(); // pobranie city jeśli jest user
                } else {
                    phoneNumber = editTextPhoneNumber.getText().toString().trim(); // pobranie phone number jeśli jest craftsman
                    industries = industriesID;
                    //listOfServicesIdToSend - lista wybranych srvisów do informacji o ogłoszeniach już zrobiona jako główna zmienna
                }
                String password = editTextPassword.getText().toString();
                Log.d(TAG, "onClick: All Data: " + "\n craftsmanOfUser:" + craftsmanOfUser + "\n region: " + region + "\n name: " + name + "\n eMail: " + eMail + "\n city: " + city + "\n phoneNumber: " + phoneNumber + "\n password: " + password + "\n industries: " + industries + "\n listOfServicesIdToSend: " + listOfServicesIdToSend);



                //TODO
                // pobranie wszystkich danych i wysłanie na server - API 3
                // przy odpowiedzi CODE 200 ma się gdzieś zapisać TokenNumber (to nie jest Token końcowy) żeby go potem można było porównać z editTextTokenFromEmail i potem to ma się zrobić:
                linLayMain.setVisibility(View.GONE);
                linLayCity.setVisibility(View.GONE);
                linLayCraftsMan.setVisibility(View.GONE);
                linLayPassword.setVisibility(View.GONE);
                buttonRegistry.setVisibility(View.GONE);
                linLayTokenNumber.setVisibility(View.VISIBLE);
                buttonLogin.setVisibility(View.VISIBLE);

            }
        });

        // button zaloguj
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // sprawdzenie czy editTextTokenFromEmail nie jest null
                if (editTextTokenFromEmail.getText() == null) {
                    showAlertDialog("Wpisz Token który otrzymałeś na podany adres e-mail"); // utworzenie alert Didalog
                    return;
                }
                // sprawdzenie czy editTextTokenFromEmail nie jest ""
                if (editTextTokenFromEmail.getText().toString().trim().equals("")) {
                    showAlertDialog("Wpisz Token który otrzymałeś na podany adres e-mail"); // utworzenie alert Didalog
                    return;
                }

                // TODO
                // API 3a -wysłanie tokenNumber i w odpowiedzi pobranie z API tokena - zrobić Volley

                // zapisanie Tokena pobranego z API do shar
                //editor.putString(C.KEY_FOR_SHAR_TOKEN, TokenPobrany);
                //editor.apply();

                // otwarcie głównej strony i zamknięcie tej strony
                //Intent currentIntent = new Intent(ActivityRegistration.this, ActivityIndustries.class);
                //startActivity(currentIntent);
                //finish();

                // toast o poprawnym logowaniu
                //Toast.makeText(ActivityLogin.this, "Zalogowano", Toast.LENGTH_SHORT).show();

            }
        });





    }
    // koniec onCreate----------------------------------------------------------------------




    // metoda spinnera do wyboru czy specjalista czy zleceniodawca
    private void spinerTypeOfUser(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ActivityRegistration.this,
                R.array.array_type_of_user, android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeOfUser.setAdapter(adapter);
        spinnerTypeOfUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                switch (position){		 //ifem też można
                    case 0:
                        //ustawienie visibility jeśli jest USER
                        linLayCity.setVisibility(View.VISIBLE);
                        linLayCraftsMan.setVisibility(View.GONE);
                        //zapisanie do bool zę to user
                        isCraftsman = false;
                        break;
                    case 1:
                        //ustawienie visibility jeśli jest CRAFTSMAN
                        linLayCity.setVisibility(View.GONE);
                        linLayCraftsMan.setVisibility(View.VISIBLE);
                        //zapisanie do bool zę to craftsman
                        isCraftsman = true;
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //nic nie musi być
            }
        });
    }

    // pobranie listy województw, dodanie do listy i ustawienie na spinnerze
    public void getDataRegionsAndPutOnSpinner (String Url) {

        // listy równoległe - jedna ze stringami a druga z ID regions
        listRegionsStrings = new ArrayList<>();
        listRegionsId = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(this); // utworzenie requst - może być inne np o stringa lub JsonArrray
        String url = Url; //url
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                // pobranie listy województw z API i zapisanie jej do listRegionsStrings
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String name = jsonObject.getString("name");
                        int id = jsonObject.getInt("id");
                        listRegionsStrings.add(name); // lista Stringów do wyświetlania w spinnerze
                        listRegionsId.add(id); // równoległa lista Id Stringów ze spinnera

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // ustawienie listRegionsStrings na spinnerRegions
                spinnerArrayAdapterRegions = new ArrayAdapter<>(ActivityRegistration.this, android.R.layout.simple_list_item_1, listRegionsStrings);
                spinnerRegions.setAdapter(spinnerArrayAdapterRegions);
                spinnerRegions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        // zapisanie do regionID żeby potem wysłać na server
                        regionID = listRegionsId.get(position); // pobranie ID z równoległej listy Id Stringów ze spinnera
                        Log.d(TAG, "onItemSelected: regionID: " + regionID);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        //nic nie musi być
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // do something when don"t getJSONObject
                Log.d(TAG, "onErrorResponse: " + error);

            }
        });
        queue.add(jsonArrayRequest); //wywołanie klasy
    }

    // pobranie danych industries i services z shar i dodanie do spinnera
    public void getIndustriesAndSerivesAndPutOnSpinner () {
        final String industriesAndServices = shar.getString(C.KEY_FOR_SHAR_INDUSTRIES_AND_SERVICES, "");
        if (industriesAndServices != "") {
            try {
                jsonArrayOfAllIndustries = new JSONArray(industriesAndServices); // JSONArray wszystkiego pobrana ze stringa
                for (int i = 0; i < jsonArrayOfAllIndustries.length(); i++) {
                    JSONObject jsonObject = jsonArrayOfAllIndustries.getJSONObject(i);
                    String name = jsonObject.getString("name");
                    listOfIndustriesString.add(name); // lista ID stringów ze spinnera
                    int id = jsonObject.getInt("id");
                    listOfIndustriesId.add(id); // równoległą lista Id Stringów ze spinnera
                }

                // ustawienie Industries na spinnerIndustriesToChose
                spinnerArrayAdapterIndustries = new ArrayAdapter<>(ActivityRegistration.this, android.R.layout.simple_list_item_1, listOfIndustriesString);
                spinnerIndustriesToChose.setAdapter(spinnerArrayAdapterIndustries);
                spinnerIndustriesToChose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        //zapisanie industriesID i wyczyszczenie listy of sevices za każdym razem gdy się zmienia industriesID
                        industriesID = listOfIndustriesId.get(position); // dodanie ID z listy  listOfIndustriesId która jest na tej samej posycji co dany string
                        listOfServicesIdToSend.clear(); // listOfServicesIdToSend to lista elementów które mająbyćwysłane jak już user będzie się rejestrował
                        Log.d(TAG, "onItemSelected: industriesID: " + industriesID);


                        // zapisanie do listOfCurrentServises wszystkich services z wybranego na spinnerIndustriesToChose industry
                        listOfCurrentServises.clear(); // wyczyszczenie listy przed dodaniem nowych elementów, listOfCurrentServises to lista wszystkich servisów danego industry wybranego z listy na spinnerze
                        listOfCurrentServisesToString.clear(); // wyczyszczenie listy przed dodaniem nowych elementów, listOfCurrentServisesToString to lista równoległa do listOfCurrentServises tylko z samymi stringami
                        try {

                            // dodanie nazwy i jsonObject zgodnej z pobranym ID do text view
                            JSONObject jsonObjectOfCurrentServices = null;
                            for (int i = 0; i < jsonArrayOfAllIndustries.length(); i++) { // jsonArrayOfAllIndustries to JSONArray wszystkiego pobrana ze stringa z API
                                JSONObject currentJSONObjectForName = jsonArrayOfAllIndustries.getJSONObject(i);
                                int currrentObjectID = currentJSONObjectForName.getInt("id");
                                if (industriesID == currrentObjectID) {
                                    jsonObjectOfCurrentServices = currentJSONObjectForName; // dodanie obiektu zgodnego z pobranym ID żeby potem rozpakować servisy
                                }
                            }

                            // dodanie services do listy
                            JSONArray jsonArrayOfCurrentServices =  jsonObjectOfCurrentServices.getJSONArray("services"); // JSONArray servises aktualnie wybraeego ze spinera industry
                            for (int i = 0; i < jsonArrayOfCurrentServices.length(); i++) {
                                JSONObject currentJSONObject = jsonArrayOfCurrentServices.getJSONObject(i);
                                String currentName = currentJSONObject.getString("name");
                                int currentId = currentJSONObject.getInt("id");
                                int currentIndustry_id = currentJSONObject.getInt("industry_id");
                                listOfCurrentServises.add(new Services(currentId, currentIndustry_id, currentName));
                                listOfCurrentServisesToString.add(currentName); // zrobienie nowej listy tyko ze stringami takiej samej jak listOfCurrentServises
                            }

                            Log.d(TAG, "onItemSelected: listOfCurrentServises size: " + listOfCurrentServises.size());
                            Log.d(TAG, "onItemSelected: listOfCurrentServisesToString size: " + listOfCurrentServisesToString.size());

                            // ustawienie listOfCurrentServises w checkBoxach
                            ArrayAdapter<String> adapretGridView = new ArrayAdapter<String>(ActivityRegistration.this, R.layout.layout_to_grid_checkbox, listOfCurrentServisesToString );
                            gridViewServices.setAdapter(adapretGridView);
                            gridViewServices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    // zaznaczenie danego checkBoxa i dodanie do listy jego ID
                                    Services currentService = listOfCurrentServises.get(position); // pobranie elementu listOfCurrentServises na podstawie pozycji z listOfCurrentServisesToString
                                    CheckedTextView currentCheckedTextView =  view.findViewById(R.id.checkedTextView);
                                    if (currentCheckedTextView.isChecked()){ // jeśli check box jest zaznaczony to odznaczy
                                        currentCheckedTextView.setChecked(false);
                                        listOfServicesIdToSend.remove(Integer.valueOf(currentService.getId())); // usunięcie z listy wybranego check boxa z service

                                    }else {
                                        currentCheckedTextView.setChecked(true); //jeśli check box jest odznaczony to zaznaczy
                                        listOfServicesIdToSend.add(currentService.getId()); // dodanie do listy wybranego check boxa z service
                                    }
                                    Log.d(TAG, "onItemClick: listOfServicesIdToSend: " + listOfServicesIdToSend.toString());

                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        //nic nie musi być
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    // utworzenie alert Didalog
    public void showAlertDialog(String alertMessage){
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegistration.this);
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


class Regions {
    int id;
    String name;

    public Regions(int id, String name) {
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
