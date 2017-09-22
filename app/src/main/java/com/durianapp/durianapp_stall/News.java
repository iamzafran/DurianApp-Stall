package com.durianapp.durianapp_stall;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Amer S Alkatheri on 14-Jun-17.
 */

public class News extends Fragment {

    private View vNews;
    private RecyclerView rvNews;
    private NewsAdapter nAdapter;
    private GridLayoutManager gridNews ;
    private List<NewsData> newsList;
    private int trackItem;
    ProgressBar mBarNews;

    public News() {
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("News");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vNews = inflater.inflate(R.layout.news, container, false);
        rvNews = (RecyclerView) vNews.findViewById(R.id.RVnewsList);
        mBarNews = (ProgressBar) vNews.findViewById(R.id.loadingNews);
        mBarNews.setVisibility(vNews.GONE);
        newsList = new ArrayList<>();
        loadNews();
        gridNews = new GridLayoutManager(this.getActivity(), 1);
        rvNews.setLayoutManager(gridNews);

        nAdapter = new NewsAdapter(this.getActivity(), newsList);

            rvNews.setAdapter(nAdapter);

        return vNews;
    }

    /*
    Starting From Here
     */

    private void loadNews() {
        AsyncTask<Integer, Void, Void> task = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected void onPreExecute() {
                mBarNews.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(Integer... params) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(Config.DURIAN_NEWS_URL).build();
                try {
                    Response response = client.newCall(request).execute();
                    JSONArray array = new JSONArray(response.body().string());
                    for (int i=0; i<array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);

                        NewsData data = new NewsData(object.getInt("id"),
                                object.getString("title"));
                        newsList.add(data);
                        trackItem = array.length();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    System.out.println("End of News");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void avoid){
                if (trackItem == 0) {
                    Toast.makeText(getContext(), "NO NEWS", Toast.LENGTH_LONG).show();
                }
                nAdapter.notifyDataSetChanged();
                mBarNews.setVisibility(View.GONE);
            }
        };
        task.execute();
    }

}
