package com.example.k.kumikomi;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static java.lang.System.out;

public class UploadTask extends AsyncTask<String,Void,String> {
    private Listener listener;
    String json =null;

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (listener != null) {
            listener.onSuccess(result);
        }

    }

    protected String doInBackground(String... params) {
        String urlSt="https://eykk161td0.execute-api.ap-northeast-1.amazonaws.com/stage/resource";
        HttpURLConnection con=null;
        String result=null;
        String word="teacher_name";
        String status=null;
        InputStream inputStream = null;
        StringBuilder sb = new StringBuilder();
        json="{"+
                "\"teacher_name\":\""+params[0]+"\","+
                "\"latitude\":\""+params[1]+"\","+
                "\"longtude\":\""+params[2]+"\""+
                "}";

        try{
            URL url=new URL(urlSt);
            con=(HttpURLConnection)url.openConnection();
            //con.setDoInput(true);
            con.setDoOutput(true);
            con.setReadTimeout(10000);
            con.setConnectTimeout(20000);
            con.setRequestMethod("POST");
            con.setInstanceFollowRedirects(false);
            con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            //con.setRequestProperty("Content-Length", String.valueOf(json.length()));
            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
            out.write(json);
            out.flush();
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
        return json;
    }
    void setListener(Listener listener) {
        this.listener = listener;
    }
    interface Listener{
        void onSuccess(String result);
    }

}
