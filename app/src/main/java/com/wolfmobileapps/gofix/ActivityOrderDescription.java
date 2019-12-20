package com.wolfmobileapps.gofix;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
                    showAlertDialog("Error","Dodaj opis"); // utworzenie alert Didalog
                    return;
                }
                // sprawdzenie czy editTextDescription nie jest < 50
                if (editTextDescription.getText().toString().length() < 50) {
                    showAlertDialog("Error","Opis jest za krótki. Nusi być minimum 50 znaków"); // utworzenie alert Didalog
                    return;
                }

                // pobranie danych do wysłąnia
                int industryIDToSend = industryID; // to nie jest potrzebne do wysyłania do API bo ID service jest jednoznaczne
                int serviceIDToSend = serviceID;
                String orderText = editTextDescription.getText().toString();
                Log.d(TAG, "Order: \nindustryIDToSend: " + industryIDToSend + "\nserviceIDToSend: " + serviceIDToSend + "\norderText: " + orderText);






                // TODO
                // wysłanie danych do API

                // po udanym wysłaniu
                showAlertDialog("Potwierdzenie" , "Zlecenie zostało wysłane");



            }
        });


    }

    // utworzenie alert Didalog
    public void showAlertDialog(final String titule, String alertMessage){
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityOrderDescription.this);
        builder.setTitle(titule);
        builder.setMessage(alertMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // jeśli będzie to alert wywołany przez wysłąne zlecenie to zamknie okno i przejdzie do początku
                if (titule.equals("Potwierdzenie")){
                    startActivity(new Intent(ActivityOrderDescription.this, ActivityIndustries.class));
                    finish();
                }

            }
        }).create();
        builder.show();
    }
}
