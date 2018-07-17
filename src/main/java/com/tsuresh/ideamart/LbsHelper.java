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

public class LbsHelper {

    //REQUIRED PARAMS
    private String applicationId;
    private String password;
    private String serverURL;

    //OPTIONAL PARAMS
    private String version = "";
    private String responseTime = "";
    private String shorizontalAccuracy = "";
    private String sfreshness = "";

    //RESPONSE VALUES
    private String lalitude = "";
    private String longitude = "";
    private String freshness = "";
    private String horizontalAccuracy = "";
    private String timeStamp = "";

    private Context context;

    public LbsHelper(String serverURL, IdeamartClient ideamartClient, Context context) throws SMSServiceException {

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

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;

    }

    public void getPosition(String subscriberId, String serviceType) throws SMSServiceException, ExecutionException, InterruptedException {

        if(isInternetAvailable()){

            if(subscriberId.equals("") || serviceType.equals("")){

                throw new SMSServiceException("Missing required params", "0001");

            } else {

                String jsonStream = resolveJsonStream(subscriberId,serviceType);
                String jsonResponse =  new RequestWorker().execute(serverURL,jsonStream).get();

                Log.d("lblresponse",jsonResponse);

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

            lalitude = jobject.get("latitude").getAsString();
            longitude = jobject.get("longitude").getAsString();
            freshness = jobject.get("freshness").getAsString();
            horizontalAccuracy = jobject.get("horizontalAccuracy").getAsString();
            timeStamp = jobject.get("timeStamp").getAsString();

        } else {

            throw new SMSServiceException(statusDetail, statusCode);

        }

    }

    public String resolveJsonStream(String subscriberId, String serviceType){

        Map<String, Object> lbsDetails = new HashMap<>();
        Map<String, String> applicationDetails = new HashMap<>();
        Map<String, Object> fullDetails = new HashMap<>();

        Gson gson = new Gson();

        lbsDetails.put("subscriberId",subscriberId);
        lbsDetails.put("serviceType",serviceType);

        if(!version.equals("")){
            lbsDetails.put("version",version);
        }

        if(!responseTime.equals("")){
            lbsDetails.put("responseTime",responseTime);
        }

        if(!shorizontalAccuracy.equals("")){
            lbsDetails.put("horizontalAccuracy",shorizontalAccuracy);
        }

        if(!sfreshness.equals("")){
            lbsDetails.put("freshness",sfreshness);
        }

        applicationDetails.put("applicationId",applicationId);
        applicationDetails.put("password",password);

        fullDetails.putAll(lbsDetails);
        fullDetails.putAll(applicationDetails);

        return gson.toJson(fullDetails);

    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    public void setHorizontalAccuracy(String horizontalAccuracy) {
        this.shorizontalAccuracy = horizontalAccuracy;
    }

    public void setFreshness(String freshness) {
        this.sfreshness = freshness;
    }

    public String getLalitude() {
        return lalitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getFreshness() {
        return freshness;
    }

    public String getHorizontalAccuracy() {
        return horizontalAccuracy;
    }

    public String getTimeStamp() {
        return timeStamp;
    }


}