package com.wolfmobileapps.gofix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

        textIndustries.setText(currentItem.getName());
        return convertView;
    }
    public AdapterForIndustries(Context context, int resource, List objects) {
        super(context, resource, objects);
    }

}
