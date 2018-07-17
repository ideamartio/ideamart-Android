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

public class SubscriptionHelper {

    private String applicationId;
    private String password;
    private String serverURL;

    private String sversion = "";

    private String version = "";
    private String requestId = "";
    private String subscriptionStatus = "";

    private Context context;

    public SubscriptionHelper(String serverURL, IdeamartClient ideamartClient, Context context) throws SMSServiceException {

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

    public void subscribeUser(String subscriberId, String action) throws SMSServiceException, ExecutionException, InterruptedException {

        if(isInternetAvailable()){

            if(subscriberId.equals("") || action.equals("")){

                throw new SMSServiceException("Missing required params", "0001");

            } else {

                String jsonStream = resolveJsonStream(subscriberId,action);
                String jsonResponse =  new RequestWorker().execute(serverURL,jsonStream).get();

                Log.d("Subscription Response",jsonResponse);

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

            requestId = jobject.get("requestId").getAsString();
            subscriptionStatus = jobject.get("subscriptionStatus").getAsString();
            version = jobject.get("version").getAsString();

        } else {

            throw new SMSServiceException(statusDetail, statusCode);

        }

    }

    public String resolveJsonStream(String subscriberId, String action){

        Map<String, Object> subsDetails = new HashMap<>();
        Map<String, String> applicationDetails = new HashMap<>();
        Map<String, Object> fullDetails = new HashMap<>();

        Gson gson = new Gson();

        subsDetails.put("subscriberId",subscriberId);
        subsDetails.put("action",action);

        if(!version.equals("")){
            subsDetails.put("version",sversion);
        }

        applicationDetails.put("applicationId",applicationId);
        applicationDetails.put("password",password);

        fullDetails.putAll(subsDetails);
        fullDetails.putAll(applicationDetails);

        return gson.toJson(fullDetails);

    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setVersion(String sversion) {
        this.sversion = sversion;
    }

    public String getVersion() {
        return version;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getSubscriptionStatus() {
        return subscriptionStatus;
    }

}