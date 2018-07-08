package com.example.k.kumikomi;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<LatLngState> latLngStates=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        latLngStates=(ArrayList<LatLngState>)getIntent().getSerializableExtra("lat_lng_data");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(latLngStates!=null) {
            for (int i = 0; i < latLngStates.size();i++){
                LatLng position = new LatLng(latLngStates.get(i).getStatus(LatLngState.subject.LAT), latLngStates.get(i).getStatus(LatLngState.subject.LNG));
                if(i==latLngStates.size()-1){
                    mMap.addMarker(new MarkerOptions().position(position).title("Test Maker").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(position));

                }else{
                    mMap.addMarker(new MarkerOptions().position(position).title("Test Maker"));

                }
            }
        }
        // Add a marker in Sydney and move the camera//
         mMap.setMinZoomPreference((float) 10.0);
    }
}
