package com.example.k.kumikomi;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static java.lang.System.out;

public class GeoCodeTask extends AsyncTask<String,Void,String> {
    private GeoCodeTask.Listener listener;
    String query =null;

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (listener != null) {
            listener.onSuccess(result);
        }

    }

    protected String doInBackground(String... params) {
        String urlSt="https://maps.google.com/maps/api/geocode/json?";
        HttpURLConnection con=null;
        String result=null;
        String word="teacher_name";
        String status=null;
        InputStream inputStream = null;
        StringBuilder sb = new StringBuilder();
        query="latlng="+params[0]+","+params[1]+"&sensor=false&language=ja";
        String api_key_query="&key=AIzaSyDkNprpszHhPq64_sB8hMjr4TxW4o7hB0M";
        String reqURL=urlSt+query+api_key_query;
        try{
            URL url=new URL(reqURL);
            con=(HttpURLConnection)url.openConnection();
            //con.setDoInput(true);
            con.setDoOutput(true);
            con.setReadTimeout(10000);
            con.setConnectTimeout(20000);
            con.setRequestMethod("GET");
            con.setInstanceFollowRedirects(false);
            con.connect();
            status = String.valueOf(con.getResponseCode());
            inputStream = con.getInputStream();
            if(inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            result="POST送信エラー";
        }finally {
            if (out != null) {
                out.close();
            }
        }


        con.disconnect();
        out.close();
        try {
            JSONObject json=new JSONObject(sb.toString());
            return createReturnText(getAddressFromJson(json),params[2]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    void setListener(GeoCodeTask.Listener listener) {
        this.listener = listener;
    }
    interface Listener{
        void onSuccess(String result);
    }

    public String getAddressFromJson(JSONObject geocode_response) throws JSONException {
        JSONArray array=geocode_response.getJSONArray("results");
        return array.getJSONObject(0).get("formatted_address").toString().split("、")[1];
    }
    public String createReturnText(String address,String timestamp){
        return timestamp+":"+address;
    }
}
