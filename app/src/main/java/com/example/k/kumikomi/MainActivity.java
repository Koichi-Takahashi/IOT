package com.example.k.kumikomi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private UploadTask task;
    TextView textView;
    private final int REQUEST_PERMISSION = 1000;
    String url="https://eykk161td0.execute-api.ap-northeast-1.amazonaws.com/stage/resource";
    private InternalFileReadWrite fileReadWrite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView)findViewById(R.id.result_text);
        Button move_to_teacher=(Button)findViewById(R.id.move_to_teachers);
        Button move_to_student=(Button)findViewById(R.id.move_to_students);
        Button map_btn=(Button)findViewById(R.id.mapbtn);
        Context context = getApplicationContext();
        fileReadWrite = new InternalFileReadWrite(context);

        move_to_teacher.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                checkPermission();

            }
        });
        move_to_student.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent =new Intent(getApplicationContext(),StudentsActivity.class);
                startActivityForResult(intent,100);
            }
        });
        map_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent =new Intent(getApplicationContext(),MapsActivity.class);
                startActivityForResult(intent,100);
            }
        });
        /*move_to_teacher.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String[] params = {"t1","123","234"};
                task = new UploadTask();
                task.setListener(createListener());
                task.execute(params);
            }
        });*/
    }
    public void checkPermission() {
        // 既に許可している
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){

            Intent intent =new Intent(getApplicationContext(),TeacherConfigActivity.class);
            startActivityForResult(intent,100);
        }
        // 拒否していた場合
        else{
            requestLocationPermission();
        }
    }
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION);

        } else {
            Toast toast = Toast.makeText(this,
                    "許可されないとアプリが実行できません", Toast.LENGTH_SHORT);
            toast.show();

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,},
                    REQUEST_PERMISSION);

        }
    }
    @Override
    protected void onDestroy() {
        task.setListener(null);
        super.onDestroy();
    }
    private UploadTask.Listener createListener() {
        return new UploadTask.Listener() {
            @Override
            public void onSuccess(String result) {
                textView.setText(result);
            }
        };
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent =new Intent(getApplicationContext(),TeacherConfigActivity.class);
                startActivityForResult(intent,100);

            } else {
                // それでも拒否された時の対応
                Toast toast = Toast.makeText(this,
                        "これ以上なにもできません", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }
}
