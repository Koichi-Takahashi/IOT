package com.example.k.kumikomi;

import java.io.Serializable;

public class LatLngState implements Serializable{
    double lat=0;
    double lng=0;

    enum subject{
        LAT,
        LNG;
    }
    public LatLngState(double lat_rec,double lng_rec){
        lat=lat_rec;
        lng=lng_rec;
    }
    public double getStatus(subject sub){
        switch(sub){
            case LAT:return lat;
            case LNG:return lng;
            default:return -1;
        }
    }

}
