package com.wolfmobileapps.gofix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AdapterForOfferFromCraftsman  extends ArrayAdapter<OfferFromCraftsman> {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OfferFromCraftsman currentItem = getItem(position);
        if (convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.layout_for_offer_for_craftsman,parent,false);
        }
        TextView textViewForOfferCraftsmanName = convertView.findViewById(R.id.textViewForOfferCraftsmanName);
        TextView textViewForOfferCraftsmanPrice = convertView.findViewById(R.id.textViewForOfferCraftsmanPrice);
        TextView textViewForOfferCraftsmanDescription = convertView.findViewById(R.id.textViewForOfferCraftsmanDescription);
        TextView textViewForOfferCraftsmanStars = convertView.findViewById(R.id.textViewForOfferCraftsmanStars);


        textViewForOfferCraftsmanName.setText("Nazwa: " + currentItem.getCraftsman_name());
        textViewForOfferCraftsmanPrice.setText("Cena: " + currentItem.getPrice() + " zł");
        textViewForOfferCraftsmanDescription.setText("Opis: \n" + currentItem.getDetails());
        // jeśli craftsman nie będzie miał jeszcze ocen to będzie 0
        if (currentItem.getCraftsman_rating() == 0) {
            textViewForOfferCraftsmanStars.setText("Ocena: brak ocen");
        }else {
            textViewForOfferCraftsmanStars.setText("Ocena: " + currentItem.getCraftsman_rating() + "\\10");
        }

        return convertView;
    }
    public AdapterForOfferFromCraftsman(Context context, int resource, List objects) {
        super(context, resource, objects);
    }
}
