package com.durianapp.durianapp_stall;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.durianapp.durianapp_stall.Fragment.DurianStallMapFragment;

public class DurianStallMapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_durian_stall_map);

        FragmentManager fm = getSupportFragmentManager();

        Fragment fragment = fm.findFragmentById(R.id.durian_stall_map_fragment_container);

        if(fragment==null){
            fragment = DurianStallMapFragment.newInstance();
            fm.beginTransaction().add(R.id.durian_stall_map_fragment_container,fragment).commit();
        }
    }
}
