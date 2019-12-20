package com.wolfmobileapps.gofix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class AdapterForServices extends ArrayAdapter<Services> {

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Services currentItem = getItem(position);
        if (convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.layout_for_services,parent,false);
        }
        TextView textServices = convertView.findViewById(R.id.textViewServices);

        textServices.setText(currentItem.getName());
        return convertView;
    }
    public AdapterForServices(Context context, int resource, List objects) {
        super(context, resource, objects);
    }
}