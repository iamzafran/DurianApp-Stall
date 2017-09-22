package com.durianapp.durianapp_stall;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

public class dInfoF1 extends Fragment {
    private TextView txtName, txtShapeF, txtShapeS, txtColor, txtSweetness, txtBitterness;
    private ProgressDialog loading;
    public int id;
    private NetworkImageView imageView;
    private ImageLoader imageLoader;

    public dInfoF1() {
    }

    public dInfoF1 (int durian_id){
        id = durian_id;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.d_info_1, container, false);
        txtName = (TextView) view.findViewById(R.id.showDurianName);
        txtShapeF = (TextView) view.findViewById(R.id.showDurianShape);
        txtShapeS = (TextView) view.findViewById(R.id.showDurianSpine);
        txtColor = (TextView) view.findViewById(R.id.showDurianColor);
        txtSweetness = (TextView) view.findViewById(R.id.showDurianSweetness);
        txtBitterness = (TextView) view.findViewById(R.id.showDurianBitterness);

        imageView = (NetworkImageView) view.findViewById(R.id.imageView);
        getData();
        return view;
    }

    private void getData() {
        loading = ProgressDialog.show(getContext(),"Please wait...","Fetching...", false, false);

        String url = Config.DURIAN_INFO_URL + id;

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
                        Toast.makeText(getContext(),error.getMessage().toString(),Toast.LENGTH_LONG).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void showJSON(String response) {
        String durianName = "", durianShapeF = "", durianShapeS = "", durianColor = "", durianSweetness = "", durianBitterness = "";

        String img_1 = "";
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray("result");
            JSONObject collegeData = result.getJSONObject(0);
            durianName = collegeData.getString("name");
            durianShapeF = collegeData.getString("fruit_shape");
            durianShapeS = collegeData.getString("spine_shape");
            durianColor = collegeData.getString("color");
            durianSweetness = collegeData.getString("sweetness");
            durianBitterness = collegeData.getString("bitterness");
            img_1 = collegeData.getString("img_1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        txtName.setText(durianName);
        txtShapeF.setText(durianShapeF);
        txtShapeS.setText(durianShapeS);
        txtColor.setText(durianColor);
        txtSweetness.setText(durianSweetness);
        txtBitterness.setText(durianBitterness);

        //Load Image
        imageLoader = CustomVolleyRequest.getInstance(getContext().getApplicationContext())
                .getImageLoader();
        imageLoader.get(img_1, ImageLoader.getImageListener(imageView,
                R.drawable.image, android.R.drawable
                        .ic_dialog_alert));
        imageView.setImageUrl(img_1, imageLoader);
    }
}
