package com.wolfmobileapps.gofix;

import android.app.Activity;

import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




// klasa do Google Pay z https://developers.google.com/pay/api/android/guides/tutorial?hl=pl
public class PaymentsUtil {

    private static final BigDecimal MICROS = new BigDecimal(1000000d);

    private PaymentsUtil() {}

    // CHYBA MUSI BYĆ API VERSION 3 A NIE 2
    private static JSONObject getBaseRequest() throws JSONException {
        return new JSONObject().put("apiVersion", 2).put("apiVersionMinor", 0);
    }

    // może być ENVIRONMENT_PRODUCTION lub ENVIRONMENT_TEST - zrobione wConstrains
    public static PaymentsClient createPaymentsClient(Activity activity) {
        Wallet.WalletOptions walletOptions =
                new Wallet.WalletOptions.Builder().setEnvironment(Constants.PAYMENTS_ENVIRONMENT).build();
        return Wallet.getPaymentsClient(activity, walletOptions);
    }

    // zwraca dane o płatnościach
    private static JSONObject getGatewayTokenizationSpecification() throws JSONException {
        return new JSONObject(){{
            put("type", "PAYMENT_GATEWAY"); // to v jest OK
            put("parameters", new JSONObject(){{
                put("gateway", Constants.PAYMENT_GATEWAY_TOKENIZATION_NAME); // było "example"
                put("gatewayMerchantId", "236960"); // PODAĆ MERCHAND ID ________________________________________________________________________________________________________________
            }
            });
        }};
    }

    // potrzebne tylko gdy się używa Direct tokenization a ja używam  PAYMENT_GATEWAY wieć nie trzeba
    private static JSONObject getDirectTokenizationSpecification()
            throws JSONException, RuntimeException {
        if (Constants.DIRECT_TOKENIZATION_PARAMETERS.isEmpty()
                || Constants.DIRECT_TOKENIZATION_PUBLIC_KEY.isEmpty()
                || Constants.DIRECT_TOKENIZATION_PUBLIC_KEY == null
                || Constants.DIRECT_TOKENIZATION_PUBLIC_KEY == "REPLACE_ME") {
            throw new RuntimeException("Please edit the Constants.java file to add protocol version & public key.");
        }
        JSONObject tokenizationSpecification = new JSONObject();
        tokenizationSpecification.put("type", "DIRECT");
        JSONObject parameters = new JSONObject(Constants.DIRECT_TOKENIZATION_PARAMETERS);
        tokenizationSpecification.put("parameters", parameters);
        return tokenizationSpecification;
    }

    // obsługiwane karty - zrobione wConstrains
    private static JSONArray getAllowedCardNetworks() {
        return new JSONArray(Constants.SUPPORTED_NETWORKS);
    }

    // rózne metody obsługi - zrobione wConstrains
    private static JSONArray getAllowedCardAuthMethods() {
        return new JSONArray(Constants.SUPPORTED_METHODS);
    }


    // metoda zwraca JSONa z opisem możliwości płacenia
    private static JSONObject getBaseCardPaymentMethod() throws JSONException {
        JSONObject cardPaymentMethod = new JSONObject();
        cardPaymentMethod.put("type", "CARD");

        JSONObject parameters = new JSONObject();
        parameters.put("allowedAuthMethods", getAllowedCardAuthMethods());
        parameters.put("allowedCardNetworks", getAllowedCardNetworks());
        // Optionally, you can add billing address/phone number associated with a CARD payment method.
        parameters.put("billingAddressRequired", true);

        JSONObject billingAddressParameters = new JSONObject();
        billingAddressParameters.put("format", "FULL");

        parameters.put("billingAddressParameters", billingAddressParameters);

        cardPaymentMethod.put("parameters", parameters);

        return cardPaymentMethod;
    }


    // info zwraca dostepne metody płątności chyba karna
    private static JSONObject getCardPaymentMethod() throws JSONException {
        JSONObject cardPaymentMethod = getBaseCardPaymentMethod();
        cardPaymentMethod.put("tokenizationSpecification", getGatewayTokenizationSpecification());
        return cardPaymentMethod;
    }

    // info chyba zwraca dostepne metody płatności przez aplikację
    public static Optional<JSONObject> getIsReadyToPayRequest() {
        try {
            JSONObject isReadyToPayRequest = getBaseRequest();
            isReadyToPayRequest.put(
                    "allowedPaymentMethods", new JSONArray().put(getBaseCardPaymentMethod()));

            return Optional.of(isReadyToPayRequest);
        } catch (JSONException e) {
            return Optional.empty();
        }
    }


    // info o transakcji czyli kwota, kod kraju i waluty z Constrains
    private static JSONObject getTransactionInfo(String price) throws JSONException {
        JSONObject transactionInfo = new JSONObject();
        transactionInfo.put("totalPrice", price);
        transactionInfo.put("totalPriceStatus", "FINAL");
        transactionInfo.put("countryCode", Constants.COUNTRY_CODE);
        transactionInfo.put("currencyCode", Constants.CURRENCY_CODE);

        return transactionInfo;
    }


    // info o nazwie dostawcy czyli w tym przypadku GoFix.pl
    private static JSONObject getMerchantInfo() throws JSONException {
        return new JSONObject().put("merchantName", "GoFix.pl");
    }


    // zwraca dane z Google Pay o płatnościach
    public static Optional<JSONObject> getPaymentDataRequest(String price) {
        try {
            JSONObject paymentDataRequest = PaymentsUtil.getBaseRequest();
            paymentDataRequest.put(
                    "allowedPaymentMethods", new JSONArray().put(PaymentsUtil.getCardPaymentMethod()));
            paymentDataRequest.put("transactionInfo", PaymentsUtil.getTransactionInfo(price));
            paymentDataRequest.put("merchantInfo", PaymentsUtil.getMerchantInfo());  // będzie GoFix.pl

          /* An optional shipping address requirement is a top-level property of the PaymentDataRequest
          JSON object. */
            paymentDataRequest.put("shippingAddressRequired", false); // zmienione z true na false bo nie bedzie porzeba adresu dostawy
            JSONObject shippingAddressParameters = new JSONObject();
            shippingAddressParameters.put("phoneNumberRequired", false);

            JSONArray allowedCountryCodes = new JSONArray(Constants.SHIPPING_SUPPORTED_COUNTRIES);

            shippingAddressParameters.put("allowedCountryCodes", allowedCountryCodes);
            paymentDataRequest.put("shippingAddressParameters", shippingAddressParameters);
            return Optional.of(paymentDataRequest);
        } catch (JSONException e) {
            return Optional.empty();
        }
    }

    // konwertuje long micros to String
    public static String microsToString(long micros) {
        return new BigDecimal(micros).divide(MICROS).setScale(2, RoundingMode.HALF_EVEN).toString();
    }
}




class Constants {

    public static final int PAYMENTS_ENVIRONMENT = WalletConstants.ENVIRONMENT_PRODUCTION; // może być ENVIRONMENT_PRODUCTION lub ENVIRONMENT_TEST _____________________________________________________________________

    public static final List<String> SUPPORTED_NETWORKS = Arrays.asList(  // obsługiwane karty płątnicze przez dostawcę płątności, sieci kart akceptowane w aplikacji
            "MASTERCARD",
            "VISA");

    public static final List<String> SUPPORTED_METHODS = Arrays.asList("PAN_ONLY"); // rózne metody obsługi, było ("PAN_ONLY", "CRYPTOGRAM_3DS"), ma być PAN_ONLY w dotPay wzite z https://www.dotpay.pl/developer/doc/google-pay/en/

    public static final String COUNTRY_CODE = "PL"; // podać COUNTRY_CODE, był US wiec chyba bedzie PL


    public static final String CURRENCY_CODE = "PLN"; // podać CURRENCY_CODE, było USD wiec chyba bedzie PLN


    public static final List<String> SHIPPING_SUPPORTED_COUNTRIES = Arrays.asList("PL"); // potrzebne gdy bedzie adres dostawy jakie klaje są obsługiwane, było ("US", "GB")


    public static final String PAYMENT_GATEWAY_TOKENIZATION_NAME = "dotpay"; // nazwa dostawcy płątności z https://developers.google.com/pay/api/android/reference/request-objects?hl=pl#gateway

    public static final HashMap<String, String> PAYMENT_GATEWAY_TOKENIZATION_PARAMETERS =
            new HashMap<String, String>() {
                {
                    put("gateway", PAYMENT_GATEWAY_TOKENIZATION_NAME);
                    put("gatewayMerchantId", "236960"); // PODAĆ MERCHAND ID __________________________________________________________________________________________________________________
                    // Your processor may require additional parameters. - dotPay nie wymaga https://developers.google.com/pay/api/android/reference/request-objects?hl=pl#gateway
                }
            };

    public static final String DIRECT_TOKENIZATION_PUBLIC_KEY = "REPLACE_ME"; // potrzebne tylko gdy się używa Direct tokenization a ja używam  PAYMENT_GATEWAY wieć nie trzeba

    public static final HashMap<String, String> DIRECT_TOKENIZATION_PARAMETERS = new HashMap<String, String>() { // potrzebne tylko gdy się używa Direct tokenization a ja używam  PAYMENT_GATEWAY wieć nie trzeba
        { put("protocolVersion", "ECv2"); put("publicKey", DIRECT_TOKENIZATION_PUBLIC_KEY);
        }};

    private Constants() {}
}


// informacja o sprzedawanej rzeczy
class ItemInfo {
    private final String name;
    private final int imageResourceId;

    // Micros are used for prices to avoid rounding errors when converting between currencies.
    private final long priceMicros;

    public ItemInfo(String name, long price, int imageResourceId) {
        this.name = name;
        this.priceMicros = price;
        this.imageResourceId = imageResourceId;
    }

    public String getName() {
        return name;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public long getPriceMicros() {
        return priceMicros;
    }
}
