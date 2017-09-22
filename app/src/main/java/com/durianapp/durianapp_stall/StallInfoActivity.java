package com.durianapp.durianapp_stall;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.durianapp.durianapp_stall.Fragment.StallInfoFragment;

public class StallInfoActivity extends AppCompatActivity {


    private static final String EXTRA_STORE_ID = "EXTRA_STORE_ID";

    public static Intent newIntent(Context context, int stall_id){

        Intent i = new Intent(context,StallInfoActivity.class);
        i.putExtra(EXTRA_STORE_ID,stall_id);

        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stall_info);


        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment fragment = fragmentManager.findFragmentById(R.id.stall_info_fragment_container);

        int stallID = getIntent().getIntExtra(EXTRA_STORE_ID,-1);

        if(fragment==null){

            fragment = StallInfoFragment.newInstance(stallID);

            fragmentManager.beginTransaction().add(R.id.stall_info_fragment_container,fragment).commit();
        }
    }
}
