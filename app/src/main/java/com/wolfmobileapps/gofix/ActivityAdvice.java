package com.wolfmobileapps.gofix;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class ActivityAdvice extends AppCompatActivity {

    private static final String TAG = "ActivityAdvice";

    //views
    TextView textViewAdvice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advice);

        // views
        textViewAdvice = findViewById(R.id.textViewAdvice);


        // action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Nasza rada");

        String textAdvice = "Polska to duży kraj mający wielu wspaniałych, doświadczonych i wykwalifikowanych fachowców – polskich i zagranicznych.\n" +
                "\nJak w tym gąszczu rozpoznać prawdziwego fachowca? Oto nasza rada:" +
                "\n\n1. Fachowiec jest zawsze na czas." +
                "\n\n2. Fachowiec dotrzymuje słowa." +
                "\n\n3. Fachowiec zawsze doradzi." +
                "\n\n4. Fachowiec nie używa słów „nie wiem”, „nie da się”, „nie umiem”." +
                "\n\n5. Fachowiec zawsze ma niezbędne narzędzia do pracy." +
                "\n\n6. Fachowiec nigdy nie pyta klienta „czy ma Pan/Pani może drabinę, młotek itp.”." +
                "\n\n7. Fachowiec zawsze powinien być schludnie ubrany i dobrze się prezentować." +
                "\n\n8. Fachowiec zawsze kończy swoją pracę." +
                "\n\n9. Fachowiec powinien oszacować koszt i czas powierzonego mu zadania." +
                "\n\n10. Fachowiec po skończonej pracy zawsze odbiera telefon od klienta i służy pomocą. ";

        textViewAdvice.setText(textAdvice);
    }
}
