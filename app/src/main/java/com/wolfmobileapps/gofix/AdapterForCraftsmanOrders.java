package com.wolfmobileapps.gofix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AdapterForCraftsmanOrders extends ArrayAdapter<Order> {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Order currentItem = getItem(position);
        if (convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.layout_for_offers,parent,false);
        }
        TextView textViewOrderRegionAndCity = convertView.findViewById(R.id.textViewOrderRegionAndCity);
        TextView textViewOrderIndustryAndServiceCraftsman = convertView.findViewById(R.id.textViewOrderIndustryAndServiceCraftsman);
        TextView textViewOrderDescriptionCraftsman = convertView.findViewById(R.id.textViewOrderDescriptionCraftsman);
        TextView textViewOrder_offer_picked_atCraftsman = convertView.findViewById(R.id.textViewOrder_offer_picked_atCraftsman);
        TextView textViewOrder_closed_atCraftsman = convertView.findViewById(R.id.textViewOrder_closed_atCraftsman);

        textViewOrderRegionAndCity.setText("Województwo: " + currentItem.getRegion_name() + ", Miasto: " + currentItem.getCity());
        textViewOrderIndustryAndServiceCraftsman.setText("Branża: " + currentItem.getIndustryName() + " " + currentItem.getServiceName());
        textViewOrderDescriptionCraftsman.setText("Opis: \n" + currentItem.getDescription());
        textViewOrder_offer_picked_atCraftsman.setText("offer_picked_at: " + currentItem.getOffer_picked_at());
        textViewOrder_closed_atCraftsman.setText("closed_at: " + currentItem.getClosed_at());
        return convertView;
    }
    public AdapterForCraftsmanOrders(Context context, int resource, List objects) {
        super(context, resource, objects);
    }
}
