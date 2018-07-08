package com.example.k.kumikomi;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;

import static java.lang.System.out;

public class LocationService extends Service implements LocationListener{
    private LocationManager locationManager;
    private Context context;
    private UploadTask task=null;
    private static final int MinTime = 10000;
    private static final float MinDistance = 50;
    private static final String PREF_FILE_NAME = "com.example.k.myapplication.preferences";

    private InternalFileReadWrite fileReadWrite;
    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        // 内部ストレージにログを保存
        fileReadWrite = new InternalFileReadWrite(context);

        // LocationManager インスタンス生成
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

    }
    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int requestCode = 0;
        String channelId = "default";
        String title = context.getString(R.string.app_name);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, requestCode,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // ForegroundにするためNotificationが必要、Contextを設定
        NotificationManager notificationManager =
                (NotificationManager)context.
                        getSystemService(Context.NOTIFICATION_SERVICE);

        // Notification　Channel 設定
        NotificationChannel channel = new NotificationChannel(
                channelId, title , NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Silent Notification");
        // 通知音を消さないと毎回通知音が出てしまう
        // この辺りの設定はcleanにしてから変更
        channel.setSound(null,null);
        // 通知ランプを消す
        channel.enableLights(false);
        channel.setLightColor(Color.BLUE);
        // 通知バイブレーション無し
        channel.enableVibration(false);

        if(notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(context, channelId)
                    .setContentTitle(title)
                    // 本来なら衛星のアイコンですがandroid標準アイコンを設定
                    .setSmallIcon(android.R.drawable.btn_star)
                    .setContentText("GPS")
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis())
                    .build();

            // startForeground
            startForeground(1, notification);
        }

        startGPS();

        return START_NOT_STICKY;
    }
    protected void startGPS() {
        StringBuilder strBuf = new StringBuilder();
        strBuf.append("startGPS\n");

        Log.d("debug", "startGPS");

        final boolean gpsEnabled
                = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            // GPSを設定するように促す
            enableLocationSettings();
        }

        if (locationManager != null) {
            Log.d("debug", "locationManager.requestLocationUpdates");
            try {
                if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)!=
                        PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MinTime, MinDistance, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            strBuf.append("locationManager=null\n");
        }

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged:","called");
         sendData(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        StringBuilder strBuf = new StringBuilder();

        switch (status) {
            case LocationProvider.AVAILABLE:
                strBuf.append("LocationProvider.AVAILABLE\n");
                break;
            case LocationProvider.OUT_OF_SERVICE:
                strBuf.append("LocationProvider.OUT_OF_SERVICE\n");
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                strBuf.append("LocationProvider.TEMPORARILY_UNAVAILABLE\n");
                break;
        }
        String message=strBuf.toString();
        Log.d("State change:",message);
        //fileReadWrite.writeFile();
    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        task.setListener(null);
        super.onDestroy();
        stopGPS();
    }
    private void stopGPS(){
        if (locationManager != null) {
            // update を止める
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(this);
        }
    }

    private void enableLocationSettings() {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(settingsIntent);
    }
 void sendData(Location location){
     SharedPreferences sharedPref = getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
     String[] params = {sharedPref.getString("teacher_name","error"),String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude())};
     Log.d("debug","called sendData");

     task = new UploadTask();
     task.setListener(createListener());
     task.execute(params);
 }
    private UploadTask.Listener createListener() {
        return new UploadTask.Listener() {
            @Override
            public void onSuccess(String result) {
                Log.d("debug","succeeded");

            }
        };
    }

}
