package com.wolfmobileapps.gofix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AdapterForOrders extends ArrayAdapter<Order> {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Order currentItem = getItem(position);
        if (convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.layout_for_order,parent,false);
        }
        TextView textViewOrderIndustryAndService = convertView.findViewById(R.id.textViewOrderIndustryAndService);
        TextView textViewOrderDescription = convertView.findViewById(R.id.textViewOrderDescription);
        TextView textViewOrder_offer_picked_at = convertView.findViewById(R.id.textViewOrder_offer_picked_at);
        TextView textViewOrder_closed_at = convertView.findViewById(R.id.textViewOrder_closed_at);

        textViewOrderIndustryAndService.setText("Branża: " + currentItem.getIndustryName() + " " + currentItem.getServiceName());
        textViewOrderDescription.setText("Opis: \n" + currentItem.getDescription());
        textViewOrder_offer_picked_at.setText("offer_picked_at: " + currentItem.getOffer_picked_at());
        textViewOrder_closed_at.setText("closed_at: " + currentItem.getClosed_at());
        return convertView;
    }
    public AdapterForOrders(Context context, int resource, List objects) {
        super(context, resource, objects);
    }
}
