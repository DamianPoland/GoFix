package com.wolfmobileapps.gofix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ActivityCraftsmanData extends AppCompatActivity {

    private static final String TAG = "ActivityCraftsmanData";

    //views
    private TextView textViewCraftsmanDataName;
    private TextView textViewCraftsmanDataEmail;
    private TextView textViewCraftsmanRating;
    private TextView textViewCraftsmanDataBalance;
    private Button buttonChangeCraftsmanData;
    private Button buttonChangeCraftsmanAddPoints;
    private Button  buttonCraftmanAllOrders;
    private Button buttonCraftmanOFFersAll;
    private Button buttonCraftmanOFFersTaken;
    private Button buttonCraftmanOFFersHistory;

    //shared pred
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    // zmienna do dołądowania konta
    int priceToPay = 0;


    // do Google Pay integracja z https://developers.google.com/pay/api/android/guides/tutorial?hl=pl
    private PaymentsClient mPaymentsClient;
    private View mGooglePayButton;
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;
    private TextView mGooglePayStatusText;
    private ItemInfo numberOfPointsToBuy;
    private long mShippingCost = 0; // było dla przykłądu 90 * 1000000




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_craftman_data);

        //views
        textViewCraftsmanDataName = findViewById(R.id.textViewCraftsmanDataName);
        textViewCraftsmanDataEmail = findViewById(R.id.textViewCraftsmanDataEmail);
        textViewCraftsmanDataBalance = findViewById(R.id.textViewCraftsmanDataBalance);
        textViewCraftsmanRating = findViewById(R.id.textViewCraftsmanRating);
        buttonChangeCraftsmanData = findViewById(R.id.buttonChangeCraftsmanData);
        buttonCraftmanAllOrders = findViewById(R.id.buttonCraftmanAllOrders);
        buttonCraftmanOFFersAll = findViewById(R.id.buttonCraftmanOFFersAll);
        buttonCraftmanOFFersTaken = findViewById(R.id.buttonCraftmanOFFersTaken);
        buttonCraftmanOFFersHistory = findViewById(R.id.buttonCraftmanOFFersHistory);

        // action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Panel Użytkownika");

        // shar pref
        shar = getSharedPreferences("sharName", MODE_PRIVATE);
        editor = shar.edit();

        // pobranie danych o craftsmanie
        getDataFromUrl();

        // button do zmiany danych - czyli otwarcia strony GoFix.pl
        buttonChangeCraftsmanData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri webpage = Uri.parse("http://www.gofix.pl");
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        // button do otwarcia Activity z All Orders
        buttonCraftmanAllOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityCraftsmanData.this, ActivityCraftsmanAllOrders.class));
            }
        });

        // button do otwarcia Activity z All OFFers
        buttonCraftmanOFFersAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityCraftsmanData.this, ActivityCraftsmanOFFersAll.class));
            }
        });

        // button do otwarcia Activity z All OFFers
        buttonCraftmanOFFersTaken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityCraftsmanData.this, ActivityCraftsmanOFFersTaken.class));
            }
        });

        // button do otwarcia Activity z All OFFers
        buttonCraftmanOFFersHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityCraftsmanData.this, ActivityCraftsmanOFFersHistory.class));
            }
        });


        // do Google Pay integracja z https://developers.google.com/pay/api/android/guides/tutorial?hl=pl
        //initItemUI(); // dodanie danych o sprzedawanym przedmiocie do views - mi to nie jest potrzebne

        // views do płatności
        mGooglePayButton = findViewById(R.id.googlepay_button);
        mGooglePayStatusText = findViewById(R.id.googlepay_status);

        mPaymentsClient = PaymentsUtil.createPaymentsClient(this); // zwraca object z ENVIRONMENT_PRODUCTION lub ENVIRONMENT_TEST
        possiblyShowGooglePayButton(); // sprawdza czy są dostępne płątności i jak są to pokazuje guzik płątności

        // button do doładowania konta
        mGooglePayButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {

                        // alert dialog do wybrania jaką kwotą chce siędoładować konto
                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCraftsmanData.this);
                        builder.setTitle("Doładowanie konta");
                        // single choice check box
                        final String[] pakiety = {"10 punktów za 10zł", "25 punktów za 20zł", "50 punktów za 40zł", "150 punktów za 100zł" };
                        builder.setSingleChoiceItems(pakiety, 1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // zapisanie do priceToPay kwoty doładowania
                                if (which == 0) {
                                    priceToPay = 10;
                                } else if (which == 1) {
                                    priceToPay = 20;
                                } else if (which == 2) {
                                    priceToPay = 40;
                                } else {
                                    priceToPay = 100;
                                }
                                //Toast.makeText(ActivityCraftsmanData.this, "Kwota doładowania: " + priceToPay, Toast.LENGTH_SHORT).show();
                            }
                        });
                        builder.setPositiveButton("Doładuj", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {



                                // TODO:
                                // wykonuje doładowanie kwotą priceToPay
                                //sendPaintsToSerwer(priceToPay);


                                // dodane tylko po to żeby pobierało małą kwotę - po testach usunąć
                                priceToPay = 1;

                                // dodanie obiektu płatności który będzie obsłużony
                                numberOfPointsToBuy = new ItemInfo("Punkty", priceToPay * 1000000, R.drawable.gofix_icon); // jest * 1000000 żeby potem odpowiednio się zaokrąglało przy przeliczaniu walut - samo to robi chyba w jakiejś metodzie dalej
                                Toast.makeText(getApplicationContext(),"Doładowanie: " + priceToPay + "zł",Toast.LENGTH_SHORT).show();

                                // metoda zaimplementowana z płątnościami Google Pay - rozpoczyna proces płatności po kliknięciu w guzik mGooglePayButton
                                requestPayment(view);





                            }
                        }).setNegativeButton("Zrezygnuj", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create();
                        builder.show();
                    }
                });
    }

    // pobranie danych o craftsmanie z API i wstawienie do textView
    public void getDataFromUrl () {

        RequestQueue queue = Volley.newRequestQueue(this); // utworzenie requst - może być inne np o stringa lub JsonArrray
        String url = C.API + "user/preferences"; //url
        Log.d(TAG, "sendLogin: url: " + url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d(TAG, "onResponse: response: " + response);

                //pobiera danae i wstawia do textViews
                try {
                    String name = response.getString("name"); // pobranie nazwy craftsmana
                    String email= response.getString("email"); // pobranie emaila craftsmana
                    int balance = response.getInt("balance"); // pobranie punktów craftsmana
                    float craftsman_rating = Float.parseFloat("" + response.getDouble("rating")); // pobranie ratingu craftsmana

                    // ustawienie danych w textViews
                    textViewCraftsmanDataName.setText(name);
                    textViewCraftsmanDataEmail.setText(email);
                    if (craftsman_rating == 0) {
                        textViewCraftsmanRating.setText("Brak ocen"); // jeśli craftsman nie będzi miałjeszcze ocen to będzi 0 i wtedy pokazę brak ocen
                    } else {
                        float raitingInt  = Math.round(craftsman_rating*5);
                        textViewCraftsmanRating.setText("" + (raitingInt/10)); // raiting dostaje w skali 1-10 a ma być wyświetlany w skali 0,5-5
                    }
                    textViewCraftsmanDataBalance.setText("" + balance);

                    // zapisanie ilości punktów do shar żeby jak będzie 0 to nie pozwolił na dalsze dodawanie ofert przez craftsmana
                    editor.putInt(C.KEY_FOR_BALANCE_SHAR, balance);
                    editor.apply();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, TAG + "getDataFromUrl.onErrorResponse: " + error);
                // jeśli jest response: com.android.volley.ParseError: org.json.JSONException: Value [] of type org.json.JSONArray cannot be converted to JSONObject - to znaczy że nie ma zleceń w tym województwie(regionie)

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

    // metoda do wysłąnia na serwer dodanych punktów
    public void sendPaintsToSerwer(int priceToPay) {

        String tokenString = "{\"points_to_add\": \"" + priceToPay + "\"}"; // zbudowanie stringa do JSONa
        try {
            JSONObject jsonToken = new JSONObject(tokenString); // zbudowanie JSONa ze stringa

            // wysłanie tokena do notifications na serwer
            RequestQueue queue = Volley.newRequestQueue(ActivityCraftsmanData.this);
            String url = C.API + "user/mobile"; //url
            Log.d(TAG, "sendLogin: url: " + url);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonToken, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    // po udanym wysłaniu - zwraca pusty JSON
                    Log.d(TAG, "JSONObject response points send success: " + response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    // do something when error
                    int errorCodeResponse = error.networkResponse.statusCode; // jeśli inny niż 200 to tu się pojawi cod błędu i trzeba go obsłużyć, jeśli 200 to succes i nie włączt wogle metody onErrorResponse
                    Log.d(TAG, "onErrorResponse points send error: resp: " + errorCodeResponse);
                    Log.d(TAG, "onErrorResponse error: " + error);
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

            // liczba ponownych requestów to zero i czeka 50s
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    50000,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(jsonObjectRequest); //wywołanie klasy

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //______________________________________________________________________________________________________________________________________________________________________
    // do Google Pay


    // sprawdza czy są dostępne płątności i jak są to pokazuje guzik płątności
    private void possiblyShowGooglePayButton() {
        final Optional<JSONObject> isReadyToPayJson = PaymentsUtil.getIsReadyToPayRequest();
        if (!isReadyToPayJson.isPresent()) {
            return;
        }
        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString());
        if (request == null) {
            return;
        }

        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        Task<Boolean> task = mPaymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(this,
                new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            setGooglePayAvailable(task.getResult());
                        } else {
                            Log.w("isReadyToPay failed", task.getException());
                        }
                    }
                });
    }

    // z zależności od statusu pokazeju guzik lub info że brak dostępności Google Pay na tym telefonie
    private void setGooglePayAvailable(boolean available) {
        if (available) {
            mGooglePayStatusText.setVisibility(View.INVISIBLE);
            mGooglePayButton.setVisibility(View.VISIBLE);
        } else {
            mGooglePayStatusText.setText(R.string.googlepay_status_unavailable);
        }
    }

    // odpowiedź z Google Pay czy płatność sięudałą czy nie
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // value passed in AutoResolveHelper
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        handlePaymentSuccess(paymentData);
                        break;
                    case Activity.RESULT_CANCELED:
                        // Nothing to here normally - the user simply cancelled without selecting a
                        // payment method.
                        break;
                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        handleError(status.getStatusCode());
                        break;
                    default:
                        // Do nothing.
                }

                // Re-enables the Google Pay payment button.
                mGooglePayButton.setClickable(true);
                break;
        }
    }

     // Gdy zwrotka z Google Pay będzie SUCCESS to odpali tą metodę ____________________________________________________________________________________
    @SuppressLint("StringFormatInvalid")
    private void handlePaymentSuccess(PaymentData paymentData) {
        String paymentInformation = paymentData.toJson();

        // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
        if (paymentInformation == null) {
            return;
        }
        JSONObject paymentMethodData;

        try {
            paymentMethodData = new JSONObject(paymentInformation).getJSONObject("paymentMethodData");
            // If the gateway is set to "example", no payment information is returned - instead, the
            // token will only consist of "examplePaymentMethodToken".
            if (paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("type")
                    .equals("PAYMENT_GATEWAY")
                    && paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token")
                    .equals("examplePaymentMethodToken")) {
                AlertDialog alertDialog =
                        new AlertDialog.Builder(this)
                                .setTitle("Warning")
                                .setMessage(
                                        "Gateway name set to \"example\" - please modify "
                                                + "Constants.java and replace it with your own gateway.")
                                .setPositiveButton("OK", null)
                                .create();
                alertDialog.show();
            }

            String billingName =
                    paymentMethodData.getJSONObject("info").getJSONObject("billingAddress").getString("name");
            Log.d(TAG, "BillingName: " + billingName);
            Toast.makeText(this, getString(R.string.payments_show_name, billingName), Toast.LENGTH_LONG)
                    .show();

            // Logging token string.
            Log.d("GooglePaymentToken", paymentMethodData.getJSONObject("tokenizationData").getString("token"));
        } catch (JSONException e) {
            Log.e("handlePaymentSuccess", "Error: " + e.toString());
            return;
        }
    }


    // pokaże gdy wypadnie error
    private void handleError(int statusCode) {
        Log.w("loadPaymentData failed", String.format("Error code: %d", statusCode));
    }

    // metoda rozpoczyna proces płatności po kliknięciu w guzik mGooglePayButton
    public void requestPayment(View view) {
        // Disables the button to prevent multiple clicks.
        mGooglePayButton.setClickable(false);

        // The price provided to the API should include taxes and shipping.
        // This price is not displayed to the user.
        String price = PaymentsUtil.microsToString(numberOfPointsToBuy.getPriceMicros() + mShippingCost);

        // TransactionInfo transaction = PaymentsUtil.createTransaction(price);
        Optional<JSONObject> paymentDataRequestJson = PaymentsUtil.getPaymentDataRequest(price);
        if (!paymentDataRequestJson.isPresent()) {
            return;
        }
        PaymentDataRequest request =
                PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());

        // Since loadPaymentData may show the UI asking the user to select a payment method, we use
        // AutoResolveHelper to wait for the user interacting with it. Once completed,
        // onActivityResult will be called with the result.
        if (request != null) {
            AutoResolveHelper.resolveTask(
                    mPaymentsClient.loadPaymentData(request), this, LOAD_PAYMENT_DATA_REQUEST_CODE);
        }
    }

    // dodanie danych o sprzedawanym przedmiocie do views - mi to nie jest potrzebne
//    private void initItemUI() {
//        TextView itemName = findViewById(R.id.text_item_name);
//        ImageView itemImage = findViewById(R.id.image_item_image);
//        TextView itemPrice = findViewById(R.id.text_item_price);
//
//        itemName.setText(numberOfPointsToBuy.getName());
//        itemImage.setImageResource(numberOfPointsToBuy.getImageResourceId());
//        itemPrice.setText(PaymentsUtil.microsToString(numberOfPointsToBuy.getPriceMicros()));
//    }
}
