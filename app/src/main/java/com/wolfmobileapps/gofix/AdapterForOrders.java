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
        TextView textServices = convertView.findViewById(R.id.textViewOrderInListView);

        textServices.setText(currentItem.getDescription());
        return convertView;
    }
    public AdapterForOrders(Context context, int resource, List objects) {
        super(context, resource, objects);
    }
}
