package com.durianapp.durianapp_stall;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Amer S Alkatheri on 17-Jun-17.
 */

public class dInfoF2 extends Fragment {
    public int id;
    private NetworkImageView imgN_1, imgN_2, imgN_3, imgN_4;
    private ImageLoader imageLoader_1, imageLoader_2, imageLoader_3, imageLoader_4;

    public dInfoF2() {
    }

    public dInfoF2 (int durian_id){
        id = durian_id;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.d_info_2, container, false);
        imgN_1 = (NetworkImageView) view.findViewById(R.id.img_1);
        imgN_2 = (NetworkImageView) view.findViewById(R.id.img_2);
        imgN_3 = (NetworkImageView) view.findViewById(R.id.img_3);
        imgN_4 = (NetworkImageView) view.findViewById(R.id.img_4);
        getData();
        return view;
    }

    private void getData() {
        String url = Config.DURIAN_INFO_URL + id;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),error.getMessage().toString(),Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void showJSON(String response) {
        String img_1 = "", img_2 = "", img_3 = "", img_4 = "";

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray("result");
            JSONObject collegeData = result.getJSONObject(0);
            img_1 = collegeData.getString("img_1");
            img_2 = collegeData.getString("img_2");
            img_3 = collegeData.getString("img_3");
            img_4 = collegeData.getString("img_4");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Load Image
        imageLoader_1 = CustomVolleyRequest.getInstance(getContext().getApplicationContext())
                .getImageLoader();
        imageLoader_2 = CustomVolleyRequest.getInstance(getContext().getApplicationContext())
                .getImageLoader();
        imageLoader_3 = CustomVolleyRequest.getInstance(getContext().getApplicationContext())
                .getImageLoader();
        imageLoader_4 = CustomVolleyRequest.getInstance(getContext().getApplicationContext())
                .getImageLoader();
        imageLoader_1.get(img_1, ImageLoader.getImageListener(imgN_1,
                R.drawable.image, android.R.drawable
                        .ic_dialog_alert));
        imageLoader_2.get(img_2, ImageLoader.getImageListener(imgN_2,
                R.drawable.image, android.R.drawable
                        .ic_dialog_alert));
        imageLoader_3.get(img_3, ImageLoader.getImageListener(imgN_3,
                R.drawable.image, android.R.drawable
                        .ic_dialog_alert));
        imageLoader_4.get(img_4, ImageLoader.getImageListener(imgN_4,
                R.drawable.image, android.R.drawable
                        .ic_dialog_alert));
        imgN_1.setImageUrl(img_1, imageLoader_1);
        imgN_2.setImageUrl(img_2, imageLoader_2);
        imgN_3.setImageUrl(img_3, imageLoader_3);
        imgN_4.setImageUrl(img_3, imageLoader_4);
    }
}