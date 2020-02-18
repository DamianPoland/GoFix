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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



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
    private ProgressBar progressBarWeiting;

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
    int tokenNumber; // numer wysłany na maila do wpisania w pkę aby potwierdzić eMail

    //dane do wysyłania na serwer z rejestracją i tokenem
    private String currentEMail;


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
        progressBarWeiting = findViewById(R.id.progressBarWeiting);

        // action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Rejestracja");

        // shar pref
        shar = getSharedPreferences("sharName", MODE_PRIVATE);
        editor = shar.edit();

        // metoda spinnera do wyboru czy specjalista czy zleceniodawca
        spinerTypeOfUser();

        // metoda spinnera do wyboru województwa
        getDataRegionsAndPutOnSpinner();

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
                if (!isCraftsman) {
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
                boolean currentCraftsmanOfUser = isCraftsman; // jeśli jest true to craftsman a jeśli false to user
                int currentRegion = regionID;
                String currentName = editTextName.getText().toString();
                currentEMail = editTextEmail.getText().toString().trim();
                String currentPassword = editTextPassword.getText().toString();
                String currentCity = editTextCity.getText().toString(); // pobranie city potrzebne tylko dla user
                String currentPhoneNumber = editTextPhoneNumber.getText().toString().trim(); // pobranie phone number potrzebne tylko dla craftsman
                ArrayList<Integer> servicesIdList = listOfServicesIdToSend; // pobranie listy services ID potrzebne tylko dla craftsman

                Log.d(TAG, "onClick: All Data: " + "\n craftsmanOfUser:" + currentCraftsmanOfUser + "\n region: " + currentRegion + "\n craftsman_name: " + currentName + "\n eMail: " + currentEMail + "\n password: " + currentPassword + "\n city: " + currentCity + "\n phoneNumber: " + currentPhoneNumber + "\n servicesIdList: " + servicesIdList);

                // Url api w zależności czy zwykły user czy craftsman
                String apiUrl = "";
                Gson gson = new Gson();
                String currentUserOrCraftsmanString = "";
                if (currentCraftsmanOfUser) {
                    apiUrl = C.API + "user/craftsman";
                    Craftsman currentCraftsman = new Craftsman(currentName, currentEMail, currentPassword, currentRegion, servicesIdList, currentPhoneNumber); // utworzenie obiektu do wysłąnia na serwer
                    currentUserOrCraftsmanString = gson.toJson(currentCraftsman); // zmiana obiektu na stringa
                } else {
                    apiUrl = C.API + "user/client";
                    User currentUser = new User(currentName, currentEMail, currentPassword, currentRegion, currentCity); // utworzenie obiektu do wysłąnia na serwer
                    currentUserOrCraftsmanString = gson.toJson(currentUser); // zmiana obiektu na stringa
                }

                // zmiana instancji obiektu na jsona
                try {
                    JSONObject json = new JSONObject(currentUserOrCraftsmanString);
                    sendRegistrationData(apiUrl, json); // metoda do wysyłanie obiektu na serwer

                    //usuniecie przycisku i pokazanie progress bara
                    buttonRegistry.setVisibility(View.GONE);
                    progressBarWeiting.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    Log.d(TAG, "JSONException: " + e);
                }
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

                // tokez wyśle go na server
                String apiUrl = C.API + "user/confirm"; //Url do wysłąnie na server
                Gson gson = new Gson();
                int tokenFromEditText = Integer.parseInt(editTextTokenFromEmail.getText().toString());
                TokenNumber tokenNumberItem = new TokenNumber(tokenFromEditText, currentEMail);
                String tokenString = gson.toJson(tokenNumberItem);
                try {
                    JSONObject jsonObjectToken = new JSONObject(tokenString);
                    sendTokenNumber(apiUrl, jsonObjectToken); // metoda wysyłająca na server
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    // koniec onCreate----------------------------------------------------------------------

    // wysłąnie rejestracji na serwer
    public void sendRegistrationData(String Url, JSONObject json) {

        Log.d(TAG, "sendLogin: JSONObject: " + json);
        Log.d(TAG, "sendLogin: Url: " + Url);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Url; //url
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                //pokazanie przycisku i usunięcie progress bara
                buttonRegistry.setVisibility(View.VISIBLE);
                progressBarWeiting.setVisibility(View.GONE);

                Log.d(TAG, "JSONObject response: " + response.toString());

                try {
                    // zwrot token Number - przy odpowiedzi CODE 200 ma się zapisać TokenNumber (to nie jest Token końcowy) tylkko do informacji w logu
                    tokenNumber = Integer.parseInt(response.getString("emailVerificationToken"));
                    Log.d(TAG, "onResponse: succes token: " + tokenNumber);

                    // ustawienie views do wpisania TokenNumber
                    linLayMain.setVisibility(View.GONE);
                    linLayCity.setVisibility(View.GONE);
                    linLayCraftsMan.setVisibility(View.GONE);
                    linLayPassword.setVisibility(View.GONE);
                    buttonRegistry.setVisibility(View.GONE);
                    linLayTokenNumber.setVisibility(View.VISIBLE);
                    buttonLogin.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //pokazanie przycisku i usunięcie progress bara
                buttonRegistry.setVisibility(View.VISIBLE);
                progressBarWeiting.setVisibility(View.GONE);

                int errorCodeResponse = error.networkResponse.statusCode; // jeśli inny niż 200 to tu się pojawi cod błędu i trzeba go obsłużyć, jeśli 200 to succes i nie włączt wogle metody onErrorResponse
                Log.d(TAG, "onErrorResponse: resp: " + errorCodeResponse);

                try {
                    String errorDataResponse = new String(error.networkResponse.data, "UTF-8"); // rozpakowanie do stringa errorDataResponse który jest JSonem lub czymś innym
                    Log.d(TAG, "onErrorResponse: errorData: " + errorDataResponse);

                    // jeśli kod 422 to zły: email lub hasło
                    if (errorCodeResponse == 422) {

                        JSONObject jsonObject = new JSONObject(errorDataResponse);
                        JSONObject errorsJsonObject = jsonObject.getJSONObject("errors");

                        String eoorMessage = "";

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


                        // pokazanie alert dialogu
                        showAlertDialog(eoorMessage);

                        Log.d(TAG, "onErrorResponse: " + errorDataResponse);
                    }
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
                params.put("Consumer", C.HEDDER_CUSTOMER); //  header dodatkowy z danymi
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

    // wysłąnie tokenNumber na serwer
    public void sendTokenNumber(String Url, JSONObject json) {

        Log.d(TAG, "sendLogin: JSONObject: " + json);
        Log.d(TAG, "sendLogin: Url: " + Url);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Url; //url
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d(TAG, "JSONObject response TOKEN: " + response.toString());

                try {
                    // zapisanie tokenu i is_craftsman do shar pref
                    String token = response.getString("token");
                    editor.putString(C.KEY_FOR_SHAR_TOKEN, token); // zapisanie da shar tokena
                    boolean is_craftsman = response.getBoolean("is_craftsman"); // pobranie info czy to zwykły user czy craftsman
                    editor.putBoolean(C.KEY_FOR_SHAR_IS_CRAFTSMAN, is_craftsman); // zapisanie do shar info czy to zwykły user czy craftsman
                    editor.apply();

                    // wysłnie tokenu do powiadomień na serwer żeby pprzychoddziły powiadomienia na telefon
                    TokenForNotifications tokenForNotifications = new TokenForNotifications();
                    tokenForNotifications.sendTokenToSerwer(ActivityRegistration.this);

                    // alert o poprawnym logowaniu
                    showAlertDialog(C.APPROPRIATE_LOGGING);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(ActivityRegistration.this, "Error", Toast.LENGTH_SHORT).show();
                // do something when error
                int errorCodeResponse = error.networkResponse.statusCode; // jeśli inny niż 200 to tu się pojawi cod błędu i trzeba go obsłużyć, jeśli 200 to succes i nie włączt wogle metody onErrorResponse
                Log.d(TAG, "onErrorResponse: resp: " + errorCodeResponse);
                showAlertDialog("Nieprawidłowy token");
            }
        }) {    //this is the part, that adds the header to the request
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json"); //  header format wysłanej wiadomości - JSON
                params.put("Accept", "application/json"); //  header format otrzymanej wiadomości -JSON
                params.put("Consumer", C.HEDDER_CUSTOMER); //  header dodatkowy z danymi
                return params;
            }
        };
        queue.add(jsonObjectRequest); //wywołanie klasy
    }

    // metoda spinnera do wyboru czy specjalista czy zleceniodawca
    private void spinerTypeOfUser() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ActivityRegistration.this,
                R.array.array_type_of_user, android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeOfUser.setAdapter(adapter);
        spinnerTypeOfUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                switch (position) {         //ifem też można
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
    public void getDataRegionsAndPutOnSpinner() {

        // listy równoległe - jedna ze stringami a druga z ID regions
        listRegionsStrings = new ArrayList<>();
        listRegionsId = new ArrayList<>();

        // pobranie listy województw z shar i zapisanie jej do listRegionsStrings
        String stringJSon = shar.getString(C.KEY_FOR_SHAR_REGIONS, "[]");

        try {
            JSONArray jsonArrayRegions = new JSONArray(stringJSon); // JSONArray wszystkiego pobrana ze stringa
            for (int i = 0; i < jsonArrayRegions.length(); i++) {
                JSONObject jsonObject = jsonArrayRegions.getJSONObject(i);
                String name = jsonObject.getString("name");
                int id = jsonObject.getInt("id");
                listRegionsStrings.add(name); // lista Stringów do wyświetlania w spinnerze
                listRegionsId.add(id); // równoległa lista Id Stringów ze spinnera
            }
        } catch (JSONException e) {
            e.printStackTrace();
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

    // pobranie danych industries i services z shar i dodanie do spinnera
    public void getIndustriesAndSerivesAndPutOnSpinner() {
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
                            JSONArray jsonArrayOfCurrentServices = jsonObjectOfCurrentServices.getJSONArray("services"); // JSONArray servises aktualnie wybraeego ze spinera industry
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
                            ArrayAdapter<String> adapretGridView = new ArrayAdapter<String>(ActivityRegistration.this, R.layout.layout_to_grid_checkbox, listOfCurrentServisesToString);
                            gridViewServices.setAdapter(adapretGridView);
                            gridViewServices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    // zaznaczenie danego checkBoxa i dodanie do listy jego ID
                                    Services currentService = listOfCurrentServises.get(position); // pobranie elementu listOfCurrentServises na podstawie pozycji z listOfCurrentServisesToString
                                    CheckedTextView currentCheckedTextView = view.findViewById(R.id.checkedTextView);
                                    if (currentCheckedTextView.isChecked()) { // jeśli check box jest zaznaczony to odznaczy
                                        currentCheckedTextView.setChecked(false);
                                        listOfServicesIdToSend.remove(Integer.valueOf(currentService.getId())); // usunięcie z listy wybranego check boxa z service

                                    } else {
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
    public void showAlertDialog(final String alertMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityRegistration.this);
        String titule = "Error";
        if (alertMessage.equals(C.APPROPRIATE_LOGGING)){
            titule = C.TITULE_LOGGING;
        }
        builder.setTitle(titule);
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (alertMessage.equals(C.APPROPRIATE_LOGGING)){
                    // otwarcie głównej strony i zamknięcie tej strony
                    Intent currentIntent = new Intent(ActivityRegistration.this, ActivityIndustries.class);
                    startActivity(currentIntent);
                    finish();
                }

            }
        }).create();
        builder.show();
    }

}


class User {

    String name;
    String email;
    String password;
    int regionId;
    String city;

    public User(String name, String email, String password, int regionId, String city) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.regionId = regionId;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}

class Craftsman {

    String name;
    String email;
    String password;
    int regionId;
    ArrayList<Integer> servicesIdList;
    String phoneNumber;

    public Craftsman(String name, String email, String password, int regionId, ArrayList<Integer> servicesIdList, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.regionId = regionId;
        this.servicesIdList = servicesIdList;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public ArrayList<Integer> getServicesIdList() {
        return servicesIdList;
    }

    public void setServicesIdList(ArrayList<Integer> servicesIdList) {
        this.servicesIdList = servicesIdList;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

class TokenNumber {

    int token;
    String email;

    public TokenNumber(int token, String email) {
        this.token = token;
        this.email = email;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
