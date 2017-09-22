package com.durianapp.durianapp_stall.Fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.durianapp.durianapp_stall.R;
import com.durianapp.durianapp_stall.StallInfoActivity;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Lenovo on 6/20/2017.
 */

public class StallLocationMapFragment extends SupportMapFragment {
    private static final String STALL_ID_ARGS = StallLocationMapFragment.class.getSimpleName()+"STALL_ID_ARGS" ;



    private GoogleApiClient mClient;
    private GoogleMap mMap;
    private int id;

    public static StallLocationMapFragment newInstance(int id) {

        Bundle args = new Bundle();

        StallLocationMapFragment fragment = new StallLocationMapFragment();
        args.putInt(STALL_ID_ARGS,id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);


        id = getArguments().getInt(STALL_ID_ARGS);

        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {

                        getLocation();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                }).build();

        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

            }
        });


    }

    @Override
    public void onStart()
    {
        super.onStart();
        mClient.connect();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mClient.disconnect();
    }

    private void getLocation(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("store_location");

        GeoFire georef = new GeoFire(reference);

        georef.getLocation(id + "", new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                LatLng latlng = new LatLng(location.latitude,location.longitude);
                MarkerOptions stallMarker = new MarkerOptions().position(latlng);
                updateMarker(stallMarker);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateMarker(MarkerOptions markerOptions){

        mMap.addMarker(markerOptions);

        LatLngBounds latlngbounds= new LatLngBounds.Builder().include(markerOptions.getPosition()).build();
        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(latlngbounds,0);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(markerOptions.getPosition())      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));



    }
}
