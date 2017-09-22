package com.durianapp.durianapp_stall;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NewsDetailActivity extends AppCompatActivity {

    public static int id;
    private TextView tTitle, tsDate, teDate, tContent;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        id = getIntent().getIntExtra("NEWS_ID", 0);

        tTitle = (TextView) findViewById(R.id.txtTitleNew);
        tsDate = (TextView) findViewById(R.id.txtStrDate);
        teDate = (TextView) findViewById(R.id.txtEndDate);
        tContent = (TextView) findViewById(R.id.txtNewsDetails);

        getNewsDetails();
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

    private void getNewsDetails() {
        loading = ProgressDialog.show(NewsDetailActivity.this,"Please wait...","Fetching...", false, false);

        String url = Config.NEWS_INFO_URL + id;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                showJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(NewsDetailActivity.this,error.getMessage().toString(),Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(NewsDetailActivity.this);
        requestQueue.add(stringRequest);
    }

    private void showJSON(String response) {
        String newsTitle = "", newsSDate = "",
                newsEDate = "", newsContent = "";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray("result");
            JSONObject collegeData = result.getJSONObject(0);
            newsTitle = collegeData.getString("title");
            newsContent = collegeData.getString("content");
            newsSDate = collegeData.getString("starting_date");
            newsEDate = collegeData.getString("ending_date");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        tTitle.setText(newsTitle);
        tsDate.setText(newsSDate);
        teDate.setText(newsEDate);
        tContent.setText(newsContent);
    }
}
