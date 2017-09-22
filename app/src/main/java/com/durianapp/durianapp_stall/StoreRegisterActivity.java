package com.durianapp.durianapp_stall;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.durianapp.durianapp_stall.Fragment.StoreRegisterFragment;

public class StoreRegisterActivity extends AppCompatActivity {

    public static final Intent newIntent(Context context){
        Intent i = new Intent(context,StoreRegisterActivity.class);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_register);

        FragmentManager fm = getSupportFragmentManager();

        Fragment fragment = fm.findFragmentById(R.id.store_register_fragment_container);

        if(fragment==null){
            fragment = StoreRegisterFragment.newInstance();

            fm.beginTransaction()
                    .add(R.id.store_register_fragment_container,fragment)
                    .commit();
        }
    }
}
