package com.wolfmobileapps.gofix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AdapterForCraftsmanOrders extends ArrayAdapter<OrderCraftsman> {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OrderCraftsman currentItem = getItem(position);
        if (convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.layout_for_offers,parent,false);
        }
        TextView textViewOrderRegionAndCity = convertView.findViewById(R.id.textViewOrderRegionAndCity);
        TextView textViewOrderIndustryAndServiceCraftsman = convertView.findViewById(R.id.textViewOrderIndustryAndServiceCraftsman);
        TextView textViewOrderDescriptionCraftsman = convertView.findViewById(R.id.textViewOrderDescriptionCraftsman);

        textViewOrderRegionAndCity.setText("Województwo: " + currentItem.getRegion_name() + ", Miasto: " + currentItem.getCity());
        textViewOrderIndustryAndServiceCraftsman.setText("Branża: " + currentItem.getIndustryName() + " " + currentItem.getServiceName());
        textViewOrderDescriptionCraftsman.setText("Opis: \n" + currentItem.getDescription());

        return convertView;
    }
    public AdapterForCraftsmanOrders(Context context, int resource, List objects) {
        super(context, resource, objects);
    }
}
