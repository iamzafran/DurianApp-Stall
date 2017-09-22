package com.durianapp.durianapp_stall;

import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.durianapp.durianapp_stall.Fragment.DurianStallMapFragment;
import com.durianapp.durianapp_stall.Model.Stall;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String GET_STALL_INFO = "http://durianapp.esy.es/getManageStallInfo.php?firebaseID=";
    private static final String TAG = MainActivity.class.getSimpleName() ;
    private int trackNav = 0;
    public Fragment fragment;
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    private Stall mStall;

    private void displaySelectedScreen(int itemId) {
        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.navigation_home:
                fragment = new Home();
                break;
            case R.id.navigation_info:
                fragment = new Info();
                break;
            case R.id.navigation_search:
                fragment = new Search();
                break;
            case R.id.navigation_stalls:
                fragment = DurianStallMapFragment.newInstance();
                break;
            case R.id.navigation_news:
                fragment = new News();
                break;
        }
        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (trackNav != item.getItemId()) {
                trackNav = item.getItemId();
                // Handle navigation view item clicks here.
                //int id = item.getItemId();
                displaySelectedScreen(trackNav);
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseAuth = FirebaseAuth.getInstance();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        displaySelectedScreen(R.id.navigation_home);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if(mFirebaseUser!=null){
            new GetStallInfoTask().execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int res_id = item.getItemId();
        if(res_id == R.id.btn_login) {
            Intent i = StallLogInActivity.newIntent(this);
            startActivity(i);
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {

    }

    public void goToAnotherActivity(View view) {
        Intent intent = new Intent(MainActivity.this, testActivity.class);
        startActivity(intent);
    }










    private class GetStallInfoTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            getStoreInfo();
            return null;
        }

        private void getStoreInfo(){

            String firebaseID = mFirebaseUser.getUid();
            OkHttpClient client = new OkHttpClient();
            String request_url = GET_STALL_INFO+""+firebaseID;
            Request request = new Request.Builder().url(request_url).build();
            try {
                Response response = client.newCall(request).execute();
                if(response.isSuccessful())
                {

                    String responseBody = response.body().string();

                    Log.v(TAG,responseBody);
                    JSONObject jsonObject = new JSONObject(responseBody);
                    parseStall(jsonObject);

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        private void parseStall(JSONObject jsonObject)throws JSONException{

            mStall = new Stall();
            mStall.setId(jsonObject.getInt("stall_id"));
            mStall.setName(jsonObject.getString("stall_name"));
            mStall.setAddress(jsonObject.getString("stall_address"));
            mStall.setCity(jsonObject.getString("stall_city"));
            mStall.setLocality(jsonObject.getString("stall_locality"));
            mStall.setPhone(jsonObject.getString("stall_phone"));
            mStall.setPostcode(jsonObject.getString("postcode"));
            mStall.setState(jsonObject.getString("stall_state"));
            mStall.setPictureUrl(jsonObject.getString("picture_url"));


        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Intent i = ManageStallActivity.newIntent(MainActivity.this,mStall);
            startActivity(i);
            finish();
        }
    }

}
