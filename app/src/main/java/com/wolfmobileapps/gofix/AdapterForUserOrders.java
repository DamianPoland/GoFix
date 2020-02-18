package com.wolfmobileapps.gofix;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AdapterForUserOrders extends ArrayAdapter<OrderUser> {

    private static final String TAG = "AdapterForUserOrders";

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OrderUser currentItem = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_for_order, parent, false);

            TextView textViewOrderIndustryAndService = convertView.findViewById(R.id.textViewOrderIndustryAndService);
            TextView textViewOrderDescription = convertView.findViewById(R.id.textViewOrderDescription);
            TextView textViewOrderCraftsman_craftsman_name = convertView.findViewById(R.id.textViewOrderCraftsman_craftsman_name);
            TextView textViewOrderCraftsman_craftsman_email = convertView.findViewById(R.id.textViewOrderCraftsman_craftsman_email);
            TextView textViewOrderCraftsman_craftsman_phone = convertView.findViewById(R.id.textViewOrderCraftsman_craftsman_phone);
            TextView buttonOrdersToChoseCraftsman = convertView.findViewById(R.id.buttonOrdersToChoseCraftsman);

            // usunięte przez klienta - opis w ActivityCraftsmanOfferToSend - w layout_for_offer_for_craftsman TextViews zmienione visibility na gone
//        TextView textViewOrderCraftsman_craftsman_offer_price = convertView.findViewById(R.id.textViewOrderCraftsman_craftsman_offer_price);
//        TextView textViewOrderCraftsman_craftsman_offer_details = convertView.findViewById(R.id.textViewOrderCraftsman_craftsman_offer_details);

            textViewOrderIndustryAndService.setText("Branża: " + currentItem.getIndustryName() + " " + currentItem.getServiceName());
            textViewOrderDescription.setText("Opis: \n" + currentItem.getDescription());

            // jeśli jest wybrany wykonawca czyli nazwa wykonawcy jest różna od pustej
            if (!currentItem.getCraftsman_name().equals("")) {

                textViewOrderCraftsman_craftsman_name.setVisibility(View.VISIBLE);
                textViewOrderCraftsman_craftsman_email.setVisibility(View.VISIBLE);
                textViewOrderCraftsman_craftsman_phone.setVisibility(View.VISIBLE);

                // usunięte przez klienta - opis w ActivityCraftsmanOfferToSend
//            textViewOrderCraftsman_craftsman_offer_price.setVisibility(View.VISIBLE);
//            textViewOrderCraftsman_craftsman_offer_details.setVisibility(View.VISIBLE);

                textViewOrderCraftsman_craftsman_name.setText("Nazwa wykonawcy: " + currentItem.getCraftsman_name());
                textViewOrderCraftsman_craftsman_email.setText("Email wykonawcy: " + currentItem.getCraftsman_email());
                textViewOrderCraftsman_craftsman_phone.setText("Nr. telefonu wykonawcy: " + currentItem.getCraftsman_phone());

                // usunięte przez klienta - opis w ActivityCraftsmanOfferToSend
//            textViewOrderCraftsman_craftsman_offer_price.setText("Zadeklarowana przez wykonawcę cena: " + currentItem.getOffer_price() + " zł");
//            textViewOrderCraftsman_craftsman_offer_details.setText("Opis oferty: \n" + currentItem.getOffer_details());

                // ustawienie nazwy na przycisku - jeśli jest zamknięte to przenosi do historii i tam nie może być przyciku
                if (currentItem.getClosed_at().equals("")) {
                    buttonOrdersToChoseCraftsman.setText("  Wystaw ocenę  ");
                } else {
                    buttonOrdersToChoseCraftsman.setVisibility(View.GONE);
                }
            }
        }

        return convertView;
    }

    public AdapterForUserOrders(Context context, int resource, List objects) {
        super(context, resource, objects);
    }
}
