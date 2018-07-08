package com.example.k.kumikomi;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Switch;

public class TeacherConfigActivity extends AppCompatActivity {
    private static final String PREF_FILE_NAME = "com.example.k.myapplication.preferences";
    private SharedPreferences sharedPref;
    SeekBar sb;
    Switch sw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_confg);
        final EditText teacher_name=(EditText)findViewById(R.id.editText);
        sb=(SeekBar)findViewById(R.id.seekBar);
        sw=(Switch)findViewById(R.id.switch1);
        Button save_btn=(Button)findViewById(R.id.button2);
        sharedPref = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        boolean set=sharedPref.getBoolean("set",false);
        if(set==true){
            teacher_name.setText(sharedPref.getString("teacher_name","error"));
            sb.setProgress(sharedPref.getInt("sb",0));
            sw.setChecked(sharedPref.getBoolean("sw",false));
        }else{

        }
        save_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("teacher_name",teacher_name.getText().toString());
                editor.putInt("sb",sb.getProgress());
                editor.putBoolean("sw",sw.isChecked());
                editor.putBoolean("set",true);
                editor.commit();
                if(sw.isChecked()){
                    startService();
                }else{
                    stopService();
                }
            }
        });
    }

    private void startService() {
        Intent intent = new Intent(getApplicationContext(), LocationService.class);

        // API 26 以降
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        }

        // Activityを終了させる
        finish();
    }
    private void stopService(){
        Intent intent = new Intent(getApplication(), LocationService.class);
        stopService(intent);

    }
}
