package com.durianapp.durianapp_stall.Fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.durianapp.durianapp_stall.R;
import com.durianapp.durianapp_stall.StallInfoActivity;
import com.durianapp.durianapp_stall.Util;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Lenovo on 6/19/2017.
 */

public class DurianStallMapFragment extends SupportMapFragment{


    private static final int LOCATION_PERMISSION = 0;
    private static final String TAG = DurianStallMapFragment.class.getSimpleName() ;
    private static final String GET_STALL_BY_MUKIM = "http://durianapp.esy.es/getStallByMukim.php?stall_locality=";
    private GoogleApiClient mClient;
    private GoogleMap mMap;
    private HashMap<Object, Object> markerHashMap = new HashMap<>();
    private List<LatLng> mLatLngList = new ArrayList<>();
    private List<Integer> mMukimIdList = new ArrayList<>();
    private Location mCurrentLocation;


    public static DurianStallMapFragment newInstance() {
        
        Bundle args = new Bundle();
        
        DurianStallMapFragment fragment = new DurianStallMapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);


        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        //findLocation();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                            if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);


                            } else if(ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                                findLocation();
                            }
                        } else {
                            findLocation();
                        }


                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                }).build();

            getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                       Log.d(TAG,marker.toString());

                        String key = (String) markerHashMap.get(marker);
                        Log.v(TAG,key);

                        Intent i = StallInfoActivity.newIntent(getActivity(),Integer.parseInt(key));
                        startActivity(i);
                        return true;
                    }
                });

            }
        });

        Util.isNetworkAvailable(getActivity());
        Util.isGpsIsEnabled(getActivity());

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.durian_stall_map_menu,menu);

        MenuItem searchItem = menu.findItem(R.id.item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new GetStallByMukim().execute(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)){
                    //Text is cleared, do your thing
                    getStalls();
                }
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                    mMap.clear();
                    getStalls();

                return true;
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode){
            case LOCATION_PERMISSION:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG,"Permission granted");
                    findLocation();
                }
                return;

        }

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

    private void findLocation() {
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try{
            LocationServices.FusedLocationApi.requestLocationUpdates(mClient, request, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d(TAG, "Got a location fix " + location);
                    mCurrentLocation = location;
                    getStalls();
                    //updateUI();

                }

            });
        }catch (SecurityException e){

        }
    }

    private void getStalls(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("store_location");

        GeoFire georef = new GeoFire(reference);

        GeoQuery query = georef.queryAtLocation(new GeoLocation(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude()),10.0);

        query.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.v(TAG,key);

                LatLng latlng = new LatLng(location.latitude,location.longitude);
                MarkerOptions stallMarker = new MarkerOptions().position(latlng);
                addMarker(key,stallMarker);
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private void addMarker(String key, MarkerOptions stallMarker) {


        Marker mMarker = mMap.addMarker(stallMarker);
        markerHashMap.put(key,mMarker);
        markerHashMap.put(mMarker,key);

        mLatLngList.add(mMarker.getPosition());


         LatLngBounds.Builder builder= new LatLngBounds.Builder();

        for(LatLng latlng: mLatLngList){
            builder.include(latlng);
        }

        LatLngBounds latlngbounds = builder.build();
        int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);
        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(latlngbounds, margin);
        mMap.animateCamera(update);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latlngbounds.getCenter())
                .zoom(17)                   // Sets the zoom
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();

        mMap.animateCamera(update);



    }


    private class GetStallByMukim extends AsyncTask<String,Void,List<Integer>>{


        @Override
        protected List<Integer> doInBackground(String... strings) {

            List<Integer> id = getStalls(strings[0]);
            return id;
        }

        @Override
        protected void onPostExecute(List<Integer> integers) {
            super.onPostExecute(integers);
            updateMarkers(integers);
        }

        private List<Integer> getStalls(String mukim){
            OkHttpClient client = new OkHttpClient();
            String request_url = GET_STALL_BY_MUKIM+""+mukim;
            Request request = new Request.Builder().url(request_url).build();
            List<Integer> idList = new ArrayList<>();
            try {
                Response response = client.newCall(request).execute();
                if(response.isSuccessful())
                {

                    String responseBody = response.body().string();

                    Log.v(TAG,responseBody);
                    JSONObject jsonObject = new JSONObject(responseBody);
                    idList = parseStall(jsonObject);
                    mMukimIdList = idList;

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return idList;
        }

        private List<Integer> parseStall(JSONObject jsonObject) throws JSONException {

            JSONArray mStallIDArray = jsonObject.getJSONArray("stalls");

            List<Integer> idList = new ArrayList<>();

            for(int i=0; i<mStallIDArray.length(); i++){

                JSONObject idObject = mStallIDArray.getJSONObject(i);
                idList.add(idObject.getInt("id"));
            }

            return idList ;
        }


    }

    private void updateMarkers(List<Integer> integers) {

        DatabaseReference stallRef = FirebaseDatabase.getInstance().getReference("store_location");

        GeoFire stallLocation = new GeoFire(stallRef);
        markerHashMap.clear();
        mLatLngList.clear();
        mMap.clear();

        for(int i: integers){

            stallLocation.getLocation(i + "", new LocationCallback() {
                @Override
                public void onLocationResult(String key, GeoLocation location) {
                    LatLng latlng = new LatLng(location.latitude,location.longitude);
                    MarkerOptions stallMarker = new MarkerOptions().position(latlng);
                    addMarker(key,stallMarker);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

    }
}
