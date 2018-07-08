package com.example.k.kumikomi;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class StudentsActivity extends AppCompatActivity {
    SearchTask task;
    TextView textView;
    EditText editText;
    GeoCodeTask geo_task;
    ArrayList<LatLngState> latlngs=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);
        Button search_btn=findViewById(R.id.searchbtn);
        Button map_btn=findViewById(R.id.move_map);
        textView=findViewById(R.id.st_act_txv);
        editText=findViewById(R.id.search_edit);

        search_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                task = new SearchTask();
                task.setListener(createListener());
                task.execute(editText.getText().toString());

            }
        });
        map_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                if(latlngs!=null){
                    Intent intent =new Intent(getApplicationContext(),MapsActivity.class);
                    intent.putExtra("lat_lng_data",  latlngs);
                    startActivityForResult(intent,100);
                }

            }
        });
    }
    private SearchTask.Listener createListener() {
        return new SearchTask.Listener() {
            @Override
            public void onSuccess(String result) {
                Log.d("debug in Activity",result);
                textView.setText(createAddressList(result));
                latlngs=createLatLngList(result);            }
        };
    }

    private GeoCodeTask.Listener createGeoListener() {
        return new GeoCodeTask.Listener() {
            @Override
            public void onSuccess(String result) {
                Log.d("debug in Activity",result);
                String string=textView.getText().toString();
                string+=result+="\n";
                textView.setText(string);
                            }
        };
    }
    public String createAddressList(String json_string) {
        try {
            JSONArray json = new JSONArray(json_string);
            String result = "";
            for (int i = 0; i < json.length(); i++) {
                JSONObject object = json.getJSONObject(i);
                String params[]=new String[3];
                params[0]=object.getString("position_latitude");
                params[1]= object.getString("position_longtude");
                params[2]=object.getString("time_stamp");
                geo_task = new GeoCodeTask();
                geo_task.setListener(createGeoListener());
                geo_task.execute(params);
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public ArrayList<LatLngState> createLatLngList(String json_string){
        try {
            JSONArray json = new JSONArray(json_string);
            ArrayList<LatLngState> latlngs=new ArrayList<LatLngState>();
            for (int i = 0; i < json.length(); i++) {
                JSONObject object = json.getJSONObject(i);
                double lat=Double.parseDouble(object.getString("position_latitude"));
                double lng=Double.parseDouble(object.getString("position_longtude"));
                LatLngState lls=new LatLngState(lat,lng);
                latlngs.add(lls);
            }
            return latlngs;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
