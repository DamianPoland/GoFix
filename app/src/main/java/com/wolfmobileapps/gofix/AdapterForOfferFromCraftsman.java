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
        TextView textViewForOfferCraftsmanStars = convertView.findViewById(R.id.textViewForOfferCraftsmanStars);

        // usunięte przez klienta - opis w ActivityCraftsmanOfferToSend - w layout_for_order NIC nie zmienione
//        TextView textViewForOfferCraftsmanPrice = convertView.findViewById(R.id.textViewForOfferCraftsmanPrice);
//        TextView textViewForOfferCraftsmanDescription = convertView.findViewById(R.id.textViewForOfferCraftsmanDescription);


        textViewForOfferCraftsmanName.setText("Nazwa: " + currentItem.getCraftsman_name());
        if (currentItem.getCraftsman_rating() == 0) {
            textViewForOfferCraftsmanStars.setText("Ocena: brak ocen"); // jeśli craftsman nie będzi miałjeszcze ocen to będzi 0 i wtedy pokazę brak ocen
        }else {
            float raitingInt  = Math.round(currentItem.getCraftsman_rating()*5);
            textViewForOfferCraftsmanStars.setText("Ocena: " + (raitingInt/10)); // raiting dostaje w skali 1-10 a ma być wyświetlany w skali 0,5-5
        }

        // usunięte przez klienta - opis w ActivityCraftsmanOfferToSend
//        textViewForOfferCraftsmanPrice.setText("Cena: " + currentItem.getPrice() + " zł");
//        textViewForOfferCraftsmanDescription.setText("Opis: \n" + currentItem.getDetails());

        return convertView;
    }
    public AdapterForOfferFromCraftsman(Context context, int resource, List objects) {
        super(context, resource, objects);
    }
}
