package com.wolfmobileapps.gofix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AdapterForCraftsmanOFFers extends ArrayAdapter<CraftsmanOffers> {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CraftsmanOffers currentItem = getItem(position);
        if (convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.layout_forcraftsman_offers_all,parent,false);
        }
        TextView textViewAdapterCraftsmanOffersAllPicketOrNot = convertView.findViewById(R.id.textViewAdapterCraftsmanOffersAllPicketOrNot);
        TextView textViewAdapterCraftsmanOffersAllCityAndClientName = convertView.findViewById(R.id.textViewAdapterCraftsmanOffersAllCityAndClientName);
        TextView textViewAdapterCraftsmanOffersAllDetail = convertView.findViewById(R.id.textViewAdapterCraftsmanOffersAllDetail);
        TextView textViewAdapterCraftsmanOffersAllPrice = convertView.findViewById(R.id.textViewAdapterCraftsmanOffersAllPrice);


        // jeśli oferta została wybrana przez klienta to pokaże textView z tą informacją
        if (!currentItem.getOffer_picked_at().equals("")) {
            textViewAdapterCraftsmanOffersAllPicketOrNot.setVisibility(View.VISIBLE);
        }


        textViewAdapterCraftsmanOffersAllCityAndClientName.setText("Nazwa klienta: " + currentItem.getClient_name() + "\nMiasto: " + currentItem.getCity() + "\nOpis zlecenia: " + currentItem.getDescription());
        textViewAdapterCraftsmanOffersAllDetail.setText("Moja oferta: \n " + currentItem.getOffer_details());
        textViewAdapterCraftsmanOffersAllPrice.setText("Moja cena: " + currentItem.getOffer_price() + " zł");

        return convertView;
    }
    public AdapterForCraftsmanOFFers(Context context, int resource, List objects) {
        super(context, resource, objects);
    }
}
