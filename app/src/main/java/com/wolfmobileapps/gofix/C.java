package com.wolfmobileapps.gofix;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

// Constans
public class C {

    //api gółówne co się nie zmienia
    public static final String API = "http://gofix.cichowski.me/api/";

    // key do shar pref gdzie jest zapisane pierwsze API czyli wszystkie branże i podbranże
    public static final String KEY_FOR_SHAR_INDUSTRIES_AND_SERVICES = "key for shar industries and services";

    // key do shar pref gdzie jest zapisane regions
    public static final String KEY_FOR_SHAR_REGIONS = "key for shar REGIONS";

    // key do shar gdzie jest zapisany login
    public static final String KEY_FOR_SHAR_TOKEN = "key for shar TOKEN";
    public static final String KEY_FOR_SHAR_IS_CRAFTSMAN = "key for shar IS CRAFTSMAN";
    // do hedders - API
    public static final String HEDDER_CUSTOMER = "rdbtT3Ode5D9b53q"; // dodawane w każdym headerze
    public static final String HEDDER_BEARER = "Bearer "; // dodawane przy każdym Tokenie jako: "HEDDER_BEARER Token"

    // key do intent z current industry i current service do wysłąniea zlecenia
    public static final String KEY_FOR_INTENT_INDUSTRY_NAME = "key for intent industry NAME";
    public static final String KEY_FOR_INTENT_SERVICE_NAME = "key for intent service NAME";
    public static final String KEY_FOR_INTENT_INDUSTRY_ID = "key for intent industry ID";
    public static final String KEY_FOR_INTENT_SERVICE_ID = "key for intent service ID";

    public static final String KEY_FOR_INTENT_TO_ORDER_ID = "key for intent to ORDER ID";

    public static final String APPROPRIATE_LOGGING = "Zostałeś poprawnie zalogowany";
    public static final String TITULE_LOGGING = "Logowanie";

}

//_______________________________________________________________________________________________________________________________________________________________________________________


class OrderCraftsman {

    private static final String TAG = "OrderCraftsman";

    private Context context;

    public OrderCraftsman(Context context) {
        this.context = context;
    }

    // dane do stworzenia obiektu klasy OrderCraftsman
    private int id;
    private int region_id;
    private int service_id;
    private String description;
    private String city;
    private String serviceName;
    private String industryName;
    private String region_name;

    //shared pred
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    // zmina JSONObject response pobranego z neta na liste Orders
    public ArrayList putOrdersToArrayList(JSONArray response, ArrayList<ServicesAndIndustryName> listOfIndustriesAndServicesAcoordingToServiceID) {

        // shar pref
        shar = context.getSharedPreferences("sharName", MODE_PRIVATE);

        // lista Orders która jest zwracana
        ArrayList<OrderCraftsman> listOfOrders = new ArrayList<>();

        // pobranie JSonObject i zapisanie do listOfOrders
        for (int i = 0; i < response.length(); i++) {
            try {

                // pobranie danych do OrderCraftsman z JSONA
                JSONObject jsonObject = response.getJSONObject(i);
                int id = jsonObject.getInt("id");
                int region_id = jsonObject.getInt("region_id");
                int service_id = jsonObject.getInt("service_id");
                String description = jsonObject.getString("description");
                String city = jsonObject.getString("city");

                // pobranie region craftsman_name dla danego region_id
                String region_name = "";
                ArrayList<Regions> arrayRegions = new Regions().getRegionsList(context);
                for (int j = 0; j < arrayRegions.size(); j++) {
                    Regions currentRegion = arrayRegions.get(j);
                    int currentId = currentRegion.getId();
                    if (currentId == region_id) {
                        region_name = currentRegion.getName();
                    }
                }

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
                OrderCraftsman currentOrder = new OrderCraftsman(id, region_id, service_id, description, serviceName, industryName, city, region_name);
                listOfOrders.add(currentOrder);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return listOfOrders;
    }


    public OrderCraftsman(int id, int region_id, int service_id, String description, String serviceName, String industryName, String city, String region_name) {
        this.id = id;
        this.region_id = region_id;
        this.service_id = service_id;
        this.description = description;
        this.serviceName = serviceName;
        this.industryName = industryName;
        this.city = city;
        this.region_name = region_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRegion_id() {
        return region_id;
    }

    public void setRegion_id(int region_id) {
        this.region_id = region_id;
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

//_______________________________________________________________________________________________________________________________________________________________________________________


class OrderUser {

    private static final String TAG = "OrderUser";

    private Context context;

    public OrderUser(Context context) {
        this.context = context;
    }

    //shared pred
    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    // zmina JSONArray response pobranego z neta na liste Orders
    public ArrayList putOrdersToArrayList(JSONArray response, ArrayList<ServicesAndIndustryName> listOfIndustriesAndServicesAcoordingToServiceID) {

        // shar pref
        shar = context.getSharedPreferences("sharName", MODE_PRIVATE);

        // lista Orders która jest zwracana
        ArrayList<OrderUser> listOfOrders = new ArrayList<>();

        // pobranie JSonObject i zapisanie do listOfOrders
        for (int i = 0; i < response.length(); i++) {
            try {
                // pobranie danych do OrderCraftsman z JSONA
                JSONObject jsonObject = response.getJSONObject(i);
                int id = jsonObject.getInt("id");
                int service_id = jsonObject.getInt("service_id");
                String description = jsonObject.getString("description");

                // pobranie industryNsme, serviceName na podstawie ServiceID
                String serviceName = "";
                String industryName = "";
                for (ServicesAndIndustryName servicesAndIndustryName : listOfIndustriesAndServicesAcoordingToServiceID) {
                    if (servicesAndIndustryName.getServiceId() == service_id) {
                        serviceName = servicesAndIndustryName.getServiceName();
                        industryName = servicesAndIndustryName.getIndustryName();
                    }
                }

                // pobranie danych ocraftsmanie gdy nie jest wybrana oferta to są ""
                int craftsman_id = jsonObject.getInt("craftsman_id");
                String craftsman_name = jsonObject.getString("craftsman_name");
                String craftsman_email = jsonObject.getString("craftsman_email");
                String craftsman_phone = jsonObject.getString("craftsman_phone");
                String offer_price = jsonObject.getString("offer_price");
                String offer_details = jsonObject.getString("offer_details");

                // dane w ofertach - jeśli closed_at == "" to znaczy że otwarte zlecenie a jak closed_at == "jakaś data" to zamknięte i przeniesione od historii
                String offer_picked_at = jsonObject.getString("offer_picked_at");
                String closed_at = jsonObject.getString("closed_at");


                // zapisanie do listy listOfOrders pobranych danych
                OrderUser orderUser = new OrderUser(id, service_id, description, serviceName, industryName, craftsman_id, craftsman_name, craftsman_email, craftsman_phone, offer_price, offer_details, offer_picked_at, closed_at);
                listOfOrders.add(orderUser);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return listOfOrders;
    }

    // dane do stworzenia obiektu klasy OrderUser
    private int id;
    private int service_id;
    private String description;
    private String serviceName;
    private String industryName;

    private int craftsman_id;
    private String craftsman_name;
    private String craftsman_email;
    private String craftsman_phone;
    private String offer_price;
    private String offer_details;

    private String offer_picked_at;
    private String closed_at;


    public OrderUser(int id, int service_id, String description, String serviceName, String industryName, int craftsman_id, String craftsman_name, String craftsman_email, String craftsman_phone, String offer_price, String offer_details, String offer_picked_at, String closed_at) {
        this.id = id;
        this.service_id = service_id;
        this.description = description;
        this.serviceName = serviceName;
        this.industryName = industryName;
        this.craftsman_id = craftsman_id;
        this.craftsman_name = craftsman_name;
        this.craftsman_email = craftsman_email;
        this.craftsman_phone = craftsman_phone;
        this.offer_price = offer_price;
        this.offer_details = offer_details;
        this.offer_picked_at = offer_picked_at;
        this.closed_at = closed_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getCraftsman_id() {
        return craftsman_id;
    }

    public void setCraftsman_id(int craftsman_id) {
        this.craftsman_id = craftsman_id;
    }

    public String getCraftsman_name() {
        return craftsman_name;
    }

    public void setCraftsman_name(String craftsman_name) {
        this.craftsman_name = craftsman_name;
    }

    public String getCraftsman_email() {
        return craftsman_email;
    }

    public void setCraftsman_email(String craftsman_email) {
        this.craftsman_email = craftsman_email;
    }

    public String getCraftsman_phone() {
        return craftsman_phone;
    }

    public void setCraftsman_phone(String craftsman_phone) {
        this.craftsman_phone = craftsman_phone;
    }

    public String getOffer_price() {
        return offer_price;
    }

    public void setOffer_price(String offer_price) {
        this.offer_price = offer_price;
    }

    public String getOffer_details() {
        return offer_details;
    }

    public void setOffer_details(String offer_details) {
        this.offer_details = offer_details;
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
}


//_______________________________________________________________________________________________________________________________________________________________________________________


class ServicesAndIndustryName {

    private static final String TAG = "ServicesAndIndustryName";


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

    // pusty konstruktor
    public ServicesAndIndustryName() {
    }

    //podbranie nazwy Industry, industryID, nazwy Service i serviceID i zwrócenie jako array listOfIndustriesAndServicesAcoordingToServiceID
    public ArrayList putIndustriesAndServicesWithIDToArray(Context context) {

        // shar pref
        SharedPreferences shar = context.getSharedPreferences("sharName", MODE_PRIVATE);

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

//_______________________________________________________________________________________________________________________________________________________________________________________


class Regions {
    int id;
    String name;

    public Regions(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // pusty konstruktor
    public Regions() {
    }

    // pobranie listy regionó
    public ArrayList getRegionsList(Context context) {

        // shar pref
        SharedPreferences shar = context.getSharedPreferences("sharName", MODE_PRIVATE);

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

//_______________________________________________________________________________________________________________________________________________________________________________________


class CraftsmanOffers {

    private static final String TAG = "CraftsmanOffers";

    // dane do klasy zwracanej w "craftsman/orders/applied", "craftsman/orders/picked", "craftsman/orders/history"
    private int id; // numer zlecenia utworzonego przez klienta czyli to to samo co order ID
    private String service_id; // id danego servisu
    private String description; // klient opisuje zlecenie czyli order
    private String created_at; // data utworzenia oferty przez craftsmana, jak nie to pusty string czyli ""
    private String closed_at; // data zamknięcia oferty, jak nie zamknięta to pusty string czyli "",
    private String city;  // nazwa miasta clienta który dodał zlecenie czyli order
    private String client_name;  // nzawa klienta
    private String offer_price; // cena w ofercoe od craftsmana
    private String offer_details; // opis oferty craftsmana w odpowiedzi na order
    private String offer_picked_at; // data wybrania ofertycraftsmana przez klienta, jak nie to pusty string czyli "", pokaze się tu data jak został wybrany TEN craftsman, jak INNY to nadal "" i wpadnie do historii

    // pobranie JSonArray i zapisanie do listOfCraftsmanOFFersAll
    public ArrayList<CraftsmanOffers> getDataFromUrlResponse(JSONArray response) {

        ArrayList<CraftsmanOffers> listOfCraftsmanOFFersAll = new ArrayList<>();

        for (int i = 0; i < response.length(); i++) {
            try {

                // pobranie danych z JSONA i zapisanie do listy listOfCraftsmanOFFersAll
                JSONObject jsonObject = response.getJSONObject(i);
                int id = jsonObject.getInt("id"); // numer zlecenia utworzonego przez klienta czyli to to samo co order ID
                String service_id = jsonObject.getString("service_id"); // id danego servisu
                String description = jsonObject.getString("description"); // klient opisuje zlecenie czyli order
                String created_at = jsonObject.getString("created_at"); // data utworzenia oferty przez craftsmana, jak nie to pusty string czyli ""
                String closed_at = jsonObject.getString("closed_at"); // data zamknięcia oferty, jak nie zamknięta to pusty string czyli "",
                String city = jsonObject.getString("city");  // nazwa miasta clienta który dodał zlecenie czyli order
                String client_name = jsonObject.getString("client_name");  // nzawa klienta
                String offer_price = jsonObject.getString("offer_price"); // cena w ofercoe od craftsmana
                String offer_details = jsonObject.getString("offer_details"); // opis oferty craftsmana w odpowiedzi na order
                String offer_picked_at = jsonObject.getString("offer_picked_at"); // data wybrania ofertycraftsmana przez klienta, jak nie to pusty string czyli "",

                CraftsmanOffers craftsmanOffers = new CraftsmanOffers(id, service_id,description, created_at, closed_at, city, client_name, offer_price, offer_details, offer_picked_at);
                listOfCraftsmanOFFersAll.add(craftsmanOffers);
                Log.d(TAG, "getDataFromUrlResponse:craftsmanOffers: " + craftsmanOffers.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return listOfCraftsmanOFFersAll;
    }

    // pusty konstruktor
    public CraftsmanOffers() {
    }

    public CraftsmanOffers(int id, String service_id, String description, String created_at, String closed_at, String city, String client_name, String offer_price, String offer_details, String offer_picked_at) {
        this.id = id;
        this.service_id = service_id;
        this.description = description;
        this.created_at = created_at;
        this.closed_at = closed_at;
        this.city = city;
        this.client_name = client_name;
        this.offer_price = offer_price;
        this.offer_details = offer_details;
        this.offer_picked_at = offer_picked_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getClosed_at() {
        return closed_at;
    }

    public void setClosed_at(String closed_at) {
        this.closed_at = closed_at;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public String getOffer_price() {
        return offer_price;
    }

    public void setOffer_price(String offer_price) {
        this.offer_price = offer_price;
    }

    public String getOffer_details() {
        return offer_details;
    }

    public void setOffer_details(String offer_details) {
        this.offer_details = offer_details;
    }

    public String getOffer_picked_at() {
        return offer_picked_at;
    }

    public void setOffer_picked_at(String offer_picked_at) {
        this.offer_picked_at = offer_picked_at;
    }
}

