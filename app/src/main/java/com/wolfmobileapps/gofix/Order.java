package com.wolfmobileapps.gofix;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class Order {

    private static final String TAG = "Order";

    private Context context;

    public Order(Context context) {
        this.context = context;
    }

    // dane do stworzenia obiektu klasy Order
    private int id;
    private int client_id;
    private int region_id;
    private int craftsman_id;
    private int service_id;
    private String description;
    private String offer_picked_at;
    private String closed_at;
    private String serviceName;
    private String industryName;
    private String city;
    private String region_name;

    //shared pred
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    // zmina JSONArray response pobranego z neta na liste Orders
    public ArrayList putOrdersToArrayList(JSONArray response, ArrayList<ServicesAndIndustryName> listOfIndustriesAndServicesAcoordingToServiceID) {

        // shar pref
        shar = context.getSharedPreferences("sharName", MODE_PRIVATE);

        // lista Orders która jest zwracana
        ArrayList<Order> listOfOrders = new ArrayList<>();

        // pobranie JSonArray i zapisanie do listOfOrders
        for (int i = 0; i < response.length(); i++) {
            try {

                // pobranie danych do Order z JSONA
                JSONObject jsonObject = response.getJSONObject(i);
                int id = jsonObject.getInt("id");
                int client_id = -1; // zabezpieczenie przed null
                int craftsman_id = -1; // zabezpieczenie przed null
                String city = "city";

                // TODO wstawić! jak już będzie craftsman_id not null i city sprawdzić
                if (shar.getBoolean(C.KEY_FOR_SHAR_IS_CRAFTSMAN, false)) { // if user is client
                    client_id = jsonObject.getInt("client_id");
                } else { // if user is craftsman
                    craftsman_id = jsonObject.getInt("craftsman_id");
                    city = jsonObject.getString("city");
                }

                int region_id = jsonObject.getInt("region_id");

                // pobranie region name dla danego region_id
                String region_name = "";
                ArrayList<Regions> arrayRegions = getRegionsList();
                for (int j = 0; j < arrayRegions.size(); j++) {
                    Regions currentRegion = arrayRegions.get(j);
                    int currentId = currentRegion.getId();
                    if (currentId == region_id) {
                        region_name = currentRegion.getName();
                    }
                }

                int service_id = jsonObject.getInt("service_id");
                String description = jsonObject.getString("description");
                String offer_picked_at = jsonObject.getString("offer_picked_at");
                String closed_at = jsonObject.getString("closed_at");

                // pobranie industryNsme, serviceName na podstawie ServiceID
                String serviceName = "";
                String industryName = "";
                for (ServicesAndIndustryName servicesAndIndustryName : listOfIndustriesAndServicesAcoordingToServiceID) {
                    if (servicesAndIndustryName.getServiceId() == service_id) {
                        serviceName = servicesAndIndustryName.getServiceName();
                        industryName = servicesAndIndustryName.getIndustryName();
                    }
                }
                // zapisanie do listy listOfOrders pobranych danych
                Order currentOrder = new Order(id, client_id, region_id, craftsman_id, service_id, description, offer_picked_at, closed_at, serviceName, industryName, city, region_name);
                listOfOrders.add(currentOrder);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return listOfOrders;
    }


    // pobranie listy regionó
    public ArrayList getRegionsList() {

        // shar pref
        shar = context.getSharedPreferences("sharName", MODE_PRIVATE);

        ArrayList<Regions> listRegions = new ArrayList<>(); // lista regions

        // pobranie listy województw z shar i zapisanie jej do listRegions
        String stringJSon = shar.getString(C.KEY_FOR_SHAR_REGIONS, "[]");
        try {
            JSONArray jsonArrayRegions = new JSONArray(stringJSon); // JSONArray wszystkiego pobrana ze stringa
            for (int i = 0; i < jsonArrayRegions.length(); i++) {
                JSONObject currentJSONObjectRegions = jsonArrayRegions.getJSONObject(i);
                String name = currentJSONObjectRegions.getString("name");
                int id = currentJSONObjectRegions.getInt("id");
                listRegions.add(new Regions(id, name));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listRegions;
    }

    //podbranie nazwy Industry, industryID, nazwy Service i serviceID i zwrócenie jako array listOfIndustriesAndServicesAcoordingToServiceID
    public ArrayList putIndustriesAndServicesWithIDToArray() {

        // shar pref
        shar = context.getSharedPreferences("sharName", MODE_PRIVATE);

        // list Serwices
        ArrayList<ServicesAndIndustryName> listOfIndustriesAndServicesAcoordingToServiceID = new ArrayList<>();

        String stringJSonWithIndustriesAndServicesFromSharPref = shar.getString(C.KEY_FOR_SHAR_INDUSTRIES_AND_SERVICES, ""); // pobranie stringa z shared pref ze wszystkimi Industries i Services i zamiana na JSonArray
        try {
            JSONArray jsonArrayOfAllIndustries = new JSONArray(stringJSonWithIndustriesAndServicesFromSharPref); // JSONArray wszystkiego pobrana ze stringa
            JSONObject jsonObject = null;
            for (int j = 0; j < jsonArrayOfAllIndustries.length(); j++) {
                JSONObject currentJSONObjectForName = jsonArrayOfAllIndustries.getJSONObject(j);
                String industryName = currentJSONObjectForName.getString("name"); // pobranie nazwy danego industry
                jsonObject = currentJSONObjectForName; // dodanie obiektu zgodnego z pobranym ID żeby potem rozpakować servisy

                // dodanie services do listy
                JSONArray currentArrayofServices = jsonObject.getJSONArray("services");
                for (int i = 0; i < currentArrayofServices.length(); i++) {
                    JSONObject currentJSONObject = currentArrayofServices.getJSONObject(i);
                    String currentName = currentJSONObject.getString("name");
                    int currentId = currentJSONObject.getInt("id");
                    int currentIndustry_id = currentJSONObject.getInt("industry_id");
                    listOfIndustriesAndServicesAcoordingToServiceID.add(new ServicesAndIndustryName(currentId, currentName, currentIndustry_id, industryName));
                }
            }
        } catch (JSONException e) {
            Log.d(TAG, "JSONException: " + e);
        }
        return listOfIndustriesAndServicesAcoordingToServiceID;
    }

    public Order(int id, int client_id, int region_id, int craftsman_id, int service_id, String description, String offer_picked_at, String closed_at, String serviceName, String industryName, String city, String region_name) {
        this.id = id;
        this.client_id = client_id;
        this.region_id = region_id;
        this.craftsman_id = craftsman_id;
        this.service_id = service_id;
        this.description = description;
        this.offer_picked_at = offer_picked_at;
        this.closed_at = closed_at;
        this.serviceName = serviceName;
        this.industryName = industryName;
        this.city = city;
        this.region_name = region_name;
    }

    public static String getTAG() {
        return TAG;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public int getRegion_id() {
        return region_id;
    }

    public void setRegion_id(int region_id) {
        this.region_id = region_id;
    }

    public int getCraftsman_id() {
        return craftsman_id;
    }

    public void setCraftsman_id(int craftsman_id) {
        this.craftsman_id = craftsman_id;
    }

    public int getService_id() {
        return service_id;
    }

    public void setService_id(int service_id) {
        this.service_id = service_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOffer_picked_at() {
        return offer_picked_at;
    }

    public void setOffer_picked_at(String offer_picked_at) {
        this.offer_picked_at = offer_picked_at;
    }

    public String getClosed_at() {
        return closed_at;
    }

    public void setClosed_at(String closed_at) {
        this.closed_at = closed_at;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getIndustryName() {
        return industryName;
    }

    public void setIndustryName(String industryName) {
        this.industryName = industryName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion_name() {
        return region_name;
    }

    public void setRegion_name(String region_name) {
        this.region_name = region_name;
    }
}

class ServicesAndIndustryName {
    private int serviceId;
    private String serviceName;
    private int industryID;
    private String IndustryName;


    public ServicesAndIndustryName(int serviceId, String serviceName, int industryID, String industryName) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.industryID = industryID;
        IndustryName = industryName;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getIndustryID() {
        return industryID;
    }

    public void setIndustryID(int industryID) {
        this.industryID = industryID;
    }

    public String getIndustryName() {
        return IndustryName;
    }

    public void setIndustryName(String industryName) {
        IndustryName = industryName;
    }
}
