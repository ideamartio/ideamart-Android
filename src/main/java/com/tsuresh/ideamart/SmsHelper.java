package com.tsuresh.ideamart;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class SmsHelper {

    private String applicationId;
    private String password;
    private String serverURL;

    private String charging_amount = "";
    private String encoding = "";
    private String version = "";
    private String deliveryStatusRequest = "";
    private String binaryHeader = "";
    private String sourceAddress = "";

    private Context context;

    public SmsHelper(String serverURL, IdeamartClient ideamartClient, Context context) throws SMSServiceException {

        this.context = context;

        if(serverURL.equals("") || ideamartClient.getApplicationID().equals("") || ideamartClient.getPassword().equals("")){
            throw new SMSServiceException("Request Invalid.", "E1312");
        } else {
            this.serverURL = serverURL;
            this.applicationId = ideamartClient.getApplicationID();
            this.password = ideamartClient.getPassword();
        }

    }

    public boolean isInternetAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public void sendSMS(String message, String addresses[]) throws SMSServiceException, ExecutionException, InterruptedException {

        if(isInternetAvailable()){

            if(addresses.length == 0){
                throw new SMSServiceException("Format of the address is invalid.", "E1325");
            } else {
                String jsonStream = resolveJsonStream(message,addresses);
                String jsonResponse =  new RequestWorker().execute(serverURL,jsonStream).get();

                Log.d("ideamartresponse",jsonResponse);

                handleResponse(jsonResponse);
            }

        } else {

            throw new SMSServiceException("No network access.", "0000");

        }

    }

    public void handleResponse(String jsonResponse) throws SMSServiceException {

        JsonElement jelement = new JsonParser().parse(jsonResponse);
        JsonObject jobject = jelement.getAsJsonObject();

        String statusCode = jobject.get("statusCode").getAsString();
        String statusDetail = jobject.get("statusDetail").getAsString();

        if(jsonResponse.isEmpty()){

            throw new SMSServiceException("Invalid server URL", "500");

        } else if(statusCode.equals("S1000")){

            //RESPONSE SUCCESS: DO NOTHING HERE

        } else {

            throw new SMSServiceException(statusDetail, statusCode);

        }

    }

    public String resolveJsonStream(String message, String addresses[]){

        Map<String, Object> messageDetails = new HashMap<>();
        Map<String, String> applicationDetails = new HashMap<>();
        Map<String, Object> fullDetails = new HashMap<>();

        Gson gson = new Gson();

        messageDetails.put("message",message);
        messageDetails.put("destinationAddresses",addresses);

        if(!sourceAddress.equals("")){
            messageDetails.put("sourceAddress",sourceAddress);
        }

        if(!deliveryStatusRequest.equals("")){
            messageDetails.put("deliveryStatusRequest",deliveryStatusRequest);
        }

        if(!binaryHeader.equals("")){
            messageDetails.put("binaryHeader",binaryHeader);
        }

        if(!version.equals("")){
            messageDetails.put("version",version);
        }

        if(!encoding.equals("")){
            messageDetails.put("encoding",encoding);
        }

        if(!charging_amount.equals("")){
            messageDetails.put("chargingAmount",charging_amount);
        }

        applicationDetails.put("applicationId",applicationId);
        applicationDetails.put("password",password);

        fullDetails.putAll(messageDetails);
        fullDetails.putAll(applicationDetails);

        return gson.toJson(fullDetails);

    }

    public void setChargingAmount(String charging_amount) {
        this.charging_amount = charging_amount;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDeliveryStatusRequest(String deliveryStatusRequest) {
        this.deliveryStatusRequest = deliveryStatusRequest;
    }

    public void setBinaryHeader(String binaryHeader) {
        this.binaryHeader = binaryHeader;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public void setContext(Context context) {
        this.context = context;
    }

}
