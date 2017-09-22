package com.durianapp.durianapp_stall;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.durianapp.durianapp_stall.Fragment.ManageStallFragment;

public class ManageStoreActivity extends AppCompatActivity {


    public static Intent newIntent(Context context){
        Intent i = new Intent(context,ManageStoreActivity.class);

        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_store);

        FragmentManager fm = getSupportFragmentManager();

        Fragment fragment = fm.findFragmentById(R.id.manage_stall_fragment_container);

        if(fragment==null){
            fragment = ManageStallFragment.newInstance();

            fm.beginTransaction().add(R.id.manage_stall_fragment_container,fragment).commit();
        }
    }
}
