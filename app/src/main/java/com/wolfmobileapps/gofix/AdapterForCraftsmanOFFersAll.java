package com.wolfmobileapps.gofix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AdapterForCraftsmanOFFersAll  extends ArrayAdapter<CraftsmanOffersAll> {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CraftsmanOffersAll currentItem = getItem(position);
        if (convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.layout_forcraftsman_offers_all,parent,false);
        }
        TextView textViewAdapterCraftsmanOffersAllCityAndClientName = convertView.findViewById(R.id.textViewAdapterCraftsmanOffersAllCityAndClientName);
        TextView textViewAdapterCraftsmanOffersAllDetail = convertView.findViewById(R.id.textViewAdapterCraftsmanOffersAllDetail);
        TextView textViewAdapterCraftsmanOffersAllPrice = convertView.findViewById(R.id.textViewAdapterCraftsmanOffersAllPrice);


        textViewAdapterCraftsmanOffersAllCityAndClientName.setText("Miasto: " + currentItem.getCity() +"\nNazwa klienta: " + currentItem.getClient_name());
        textViewAdapterCraftsmanOffersAllDetail.setText("Opis: \n " + currentItem.getDetails());
        textViewAdapterCraftsmanOffersAllPrice.setText("Twoja cena: " + currentItem.getPrice() + " zł");

        return convertView;
    }
    public AdapterForCraftsmanOFFersAll(Context context, int resource, List objects) {
        super(context, resource, objects);
    }
}
