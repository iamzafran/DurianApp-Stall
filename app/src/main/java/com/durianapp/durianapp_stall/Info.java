package com.durianapp.durianapp_stall;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.AsyncTask;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Request;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by Amer S Alkatheri on 14-Jun-17.
 */

public class Info extends Fragment {

    private RecyclerView recyclerView ;
    private GridLayoutManager gridLayoutManager ;
    private CustomAdapter adapter ;
    private List<MyData> data_list;
    ProgressBar mBar;

    public Info() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.info, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mBar = (ProgressBar) rootView.findViewById(R.id.loadingMaterial);
        mBar.setVisibility(rootView.GONE);
        data_list = new ArrayList<>();
        load_data_from_server();

        gridLayoutManager = new GridLayoutManager(this.getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new CustomAdapter(this.getActivity(), data_list);
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    private void load_data_from_server() {
        AsyncTask<Integer, Void, Void> task = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected void onPreExecute() {
                mBar.setVisibility(View.VISIBLE);
            }
            @Override
            protected Void doInBackground(Integer... params) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(Config.DURIAN_FEED_URL).build();
                try {
                    Response response = client.newCall(request).execute();

                    JSONArray array = new JSONArray(response.body().string());

                    for (int i=0; i<array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);

                        MyData data = new MyData(object.getInt("id"), object.getString("name"), object.getString("img_1"));
                        data_list.add(data);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    System.out.println("End of Content");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void avoid){
                adapter.notifyDataSetChanged();
                mBar.setVisibility(View.GONE);
            }
        };
        task.execute();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Durian Lists");
    }
}