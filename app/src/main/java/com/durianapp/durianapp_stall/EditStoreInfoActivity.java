package com.durianapp.durianapp_stall;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.durianapp.durianapp_stall.Fragment.EditStoreInfoFragment;

public class EditStoreInfoActivity extends AppCompatActivity {

    public static Intent newIntent(Context context) {
        Intent i = new Intent(context,EditStoreInfoActivity.class);
        return i;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_store_info);

        FragmentManager fm = getSupportFragmentManager();

        Fragment fragment = fm.findFragmentById(R.id.edit_store_info_fragment_container);

        if(fragment==null){

            fragment = EditStoreInfoFragment.newInstance();
            fm.beginTransaction().add(R.id.edit_store_info_fragment_container,fragment).commit();
        }
    }
}
