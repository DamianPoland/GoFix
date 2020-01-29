package com.wolfmobileapps.gofix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

public class AdapterForIndustries extends ArrayAdapter<Industries> {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Industries currentItem = getItem(position);
        if (convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.layout_for_industries,parent,false);
        }
        TextView textIndustries = convertView.findViewById(R.id.textViewIndustries);
        ImageView imageViewIcon = convertView.findViewById(R.id.imageViewIcon);

        // ustawienie teztu
        textIndustries.setText(currentItem.getName());

        // ustawienie ikonki w zależności od tego jaki jest text
        switch (textIndustries.getText().toString()) {
            case "Malarz":
                imageViewIcon.setImageResource(R.drawable.ikonki_malarz);
                break;
            case "Murarz":
                imageViewIcon.setImageResource(R.drawable.ikonki_murarz);
                break;
            case "Hydraulik":
                imageViewIcon.setImageResource(R.drawable.ikonki_hydraulik);
                break;
            case "Elektryk":
                imageViewIcon.setImageResource(R.drawable.ikonki_elektryk);
                break;
            case "Stolarz":
                imageViewIcon.setImageResource(R.drawable.ikonki_stolarz);
                break;
            case "Monter":
                imageViewIcon.setImageResource(R.drawable.ikonki_monter);
                break;
            case "Przeprowadzki":
                imageViewIcon.setImageResource(R.drawable.ikonki_przeprowadzki);
                break;
            case "Sprzątanie":
                imageViewIcon.setImageResource(R.drawable.ikonki_sprzatanie);
                break;
            case "Opiekun(ka)":
                imageViewIcon.setImageResource(R.drawable.ikonki_opiekunka);
                break;
            case "Usługi prawne":
                imageViewIcon.setImageResource(R.drawable.ikonki_uslugi_prawne);
                break;
            case "Projektant":
                imageViewIcon.setImageResource(R.drawable.ikonki_projektant);
                break;
            case "Ogród":
                imageViewIcon.setImageResource(R.drawable.ikonki_ogrod);
                break;
            case "Złota rączka":
                imageViewIcon.setImageResource(R.drawable.ikonki_zlota_raczka);
                break;
        }




        return convertView;
    }
    public AdapterForIndustries(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

}
