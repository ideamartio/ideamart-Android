package com.tsuresh.ideamart;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
public class RequestWorker extends AsyncTask<String, Void, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        String serverURL = params[0];
        String jsonString = params[1];

        BufferedReader in = null;
        StringBuffer sb = new StringBuffer();
        BufferedReader inPost = null;

        try{

            DefaultHttpClient httpclient = new DefaultHttpClient();

            HttpPost httpost = new HttpPost(serverURL);
            httpost.setHeader("Content-Type", "application/json");
            httpost.setEntity(new StringEntity(jsonString));

            HttpResponse response = httpclient.execute(httpost);
            HttpEntity entity = response.getEntity();

            inPost = new BufferedReader(new InputStreamReader(entity.getContent()));
            String linePost = "";
            String NLPOST = System.getProperty("line.separator");
            while ((linePost = inPost.readLine()) != null) {
                sb.append(linePost + NLPOST);
            }
            inPost.close();
            if (entity != null) {
                entity.consumeContent();
            }

            httpclient.getConnectionManager().shutdown();

        } catch(Exception e){

            return e.toString();

        }

        return sb.toString();

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}