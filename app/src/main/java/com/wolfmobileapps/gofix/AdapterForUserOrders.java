package com.wolfmobileapps.gofix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AdapterForUserOrders extends ArrayAdapter<OrderUser> {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OrderUser currentItem = getItem(position);
        if (convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.layout_for_order,parent,false);
        }
        TextView textViewOrderIndustryAndService = convertView.findViewById(R.id.textViewOrderIndustryAndService);
        TextView textViewOrderDescription = convertView.findViewById(R.id.textViewOrderDescription);


        textViewOrderIndustryAndService.setText("Branża: " + currentItem.getIndustryName() + " " + currentItem.getServiceName());
        textViewOrderDescription.setText("Opis: \n" + currentItem.getDescription());
        return convertView;
    }
    public AdapterForUserOrders(Context context, int resource, List objects) {
        super(context, resource, objects);
    }
}
