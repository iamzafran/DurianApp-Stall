package com.durianapp.durianapp_stall;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class DurianInfoActivity extends AppCompatActivity {
    private SectionsPageAdapter mSectionsPageAdapter;
    private ViewPager mViewPager;
    public static int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_durian_info);

        Toolbar toolbar = (Toolbar) findViewById(R.id.infoToolbar);
        id = getIntent().getIntExtra("DURIAN_ID", 0);
        //String aString = Integer.toString(id);
        String title = getIntent().getStringExtra("DURIAN_TITLE");
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        //Set up the ViewPager with the Sections Adapter
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.close_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int res_id = item.getItemId();
        if(res_id == R.id.btn_close) {
            finish();
        }
        return true;
    }

    private void setupViewPager (ViewPager viewPager) {
        int limitNumberOfPages = 3;
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new dInfoF1(id), "Detail");
        adapter.addFragment(new dInfoF2(id), "Pictures");
        adapter.addFragment(StoreListFragment.newInstance(id), "Stalls");
        //Limit re-load all pages
        viewPager.setOffscreenPageLimit(limitNumberOfPages);
        viewPager.setAdapter(adapter);
    }
}