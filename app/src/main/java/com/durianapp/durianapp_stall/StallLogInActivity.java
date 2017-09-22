package com.durianapp.durianapp_stall;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.durianapp.durianapp_stall.Fragment.StallLoginFragment;

public class StallLogInActivity extends AppCompatActivity {


    public static Intent newIntent(Context context){
        Intent i = new Intent(context, StallLogInActivity.class);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stall_log_in);


        FragmentManager fm = getSupportFragmentManager();

        Fragment fragment = fm.findFragmentById(R.id.stall_login_fragment_container);

        if(fragment==null){
            fragment = StallLoginFragment.newInstance();

            fm.beginTransaction().add(R.id.stall_login_fragment_container,fragment).commit();
        }

    }
}
