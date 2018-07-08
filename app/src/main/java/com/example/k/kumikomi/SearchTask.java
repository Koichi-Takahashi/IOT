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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static java.lang.System.out;

public class SearchTask extends AsyncTask<String,Void,String> {
    private SearchTask.Listener listener;
    String json =null;

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (listener != null) {
            listener.onSuccess(result);
        }

    }

    protected String doInBackground(String... params) {
        String urlSt="https://eykk161td0.execute-api.ap-northeast-1.amazonaws.com/stage/resource"+"?teacher_name="+params[0];

        HttpURLConnection con=null;
        String word="teacher_name";
        String status=null;
        InputStream inputStream = null;
        StringBuilder sb = new StringBuilder();
        json="{"+
                "\"teacher_name\":\""+params[0]+"\""+
                "}";
        try{
            URL url=new URL(urlSt);
            con=(HttpURLConnection)url.openConnection();
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
        }finally {
            if (out != null) {
                out.close();
            }
        }


        con.disconnect();
        out.close();

        return sb.toString();
    }
    void setListener(SearchTask.Listener listener) {
        this.listener = listener;
    }
    interface Listener{
        void onSuccess(String result);
    }

}
