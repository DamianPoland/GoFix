package com.wolfmobileapps.gofix;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AdapterForCraftsmanOFFers extends ArrayAdapter<CraftsmanOffers> {

    private static final String TAG = "AdapterForCraftsmanOFFe";

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CraftsmanOffers currentItem = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_forcraftsman_offers_all, parent, false);
        }
        TextView textViewAdapterCraftsmanOffersAllPicketOrNot = convertView.findViewById(R.id.textViewAdapterCraftsmanOffersAllPicketOrNot);
        TextView textViewAdapterCraftsmanOffersAllCityAndClientName = convertView.findViewById(R.id.textViewAdapterCraftsmanOffersAllCityAndClientName);

        // usunięte przez klienta - opis w ActivityCraftsmanOfferToSend - w layout_forcraftsman_offers_all TextViews zmienione visibility na gone
//        TextView textViewAdapterCraftsmanOffersAllDetail = convertView.findViewById(R.id.textViewAdapterCraftsmanOffersAllDetail);
//        TextView textViewAdapterCraftsmanOffersAllPrice = convertView.findViewById(R.id.textViewAdapterCraftsmanOffersAllPrice);

        //pokaże textView z informacją że zlecenie jest zatwierdzone przez klienta dla tego craftsmana jeśli: 1. oferta została wybrana przez klienta
        if (!currentItem.getOffer_picked_at().equals("")) {         // jeśli oferta została wybrana przez klienta to jest data wybrania tej oferty - nieważne który craftman został wybrany to u każdego to się pokazę
            if (currentItem.getOffer_rejected_at().equals("")) { // jeśli oferta nie zostałą odrzucona to nie ma daty odrzucenia
                textViewAdapterCraftsmanOffersAllPicketOrNot.setText("ZLECENIE ZATWIERDZONE");
                textViewAdapterCraftsmanOffersAllPicketOrNot.setTextColor(Color.GREEN);
            } else {
                textViewAdapterCraftsmanOffersAllPicketOrNot.setText("ZLECENIE ODRZUCONE");
                textViewAdapterCraftsmanOffersAllPicketOrNot.setTextColor(Color.RED);
            }
        } else {
            textViewAdapterCraftsmanOffersAllPicketOrNot.setText("KLIENT JESZCZE NIE WYBRAŁ WYKONAWCY");
            textViewAdapterCraftsmanOffersAllPicketOrNot.setTextColor(Color.GRAY);
        }
        textViewAdapterCraftsmanOffersAllCityAndClientName.setText("Nazwa klienta: " + currentItem.getClient_name() + "\nMiasto: " + currentItem.getCity() + "\nOpis zlecenia: " + currentItem.getDescription());

        // usunięte przez klienta - opis w ActivityCraftsmanOfferToSend
//        textViewAdapterCraftsmanOffersAllDetail.setText("Moja oferta: \n " + currentItem.getOffer_details());
//        textViewAdapterCraftsmanOffersAllPrice.setText("Moja cena: " + currentItem.getOffer_price() + " zł");

        return convertView;
    }

    public AdapterForCraftsmanOFFers(Context context, int resource, List objects) {
        super(context, resource, objects);
    }
}
