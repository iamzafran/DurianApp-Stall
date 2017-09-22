package com.durianapp.durianapp_stall;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.durianapp.durianapp_stall.Model.Stall;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Amer S Alkatheri on 17-Jun-17.
 */

public class StoreListFragment extends Fragment {


    private static final String DURIAN_ID =  "DURIAN_ID";
    private static final String GET_STALL_URL = "http://durianapp.esy.es/getStallForDurian.php?durian_id=";
    private static final String TAG = StoreListFragment.class.getSimpleName();
    private List<Stall> mStallList = new ArrayList<>();
    private RecyclerView mStallRecyclerView;
    private StallAdapter mStallAdapter;

    private int mDurianId;

    public static StoreListFragment newInstance(int durianId) {

        Bundle args = new Bundle();

        StoreListFragment fragment = new StoreListFragment();
        args.putInt(DURIAN_ID,durianId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDurianId = getArguments().getInt(DURIAN_ID);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.store_list_fragment_layout, container, false);
        mStallRecyclerView = (RecyclerView) view.findViewById(R.id.store_list_recycler_view);
        mStallRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));


        new GetStallsForDurianTask().execute();
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Stalls");
    }


    private class GetStallsForDurianTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            fetchStalls();
            return null;
        }

        private void fetchStalls() {
            OkHttpClient client = new OkHttpClient();
            String requestString = GET_STALL_URL+mDurianId;
            Request request = new Request.Builder().url(requestString).build();
            Call call = client.newCall(request);
            try {
                Response response = call.execute();
                if(response.isSuccessful()){
                    String responseBody = response.body().string();

                    Log.v(TAG,responseBody);

                    parseStalls(responseBody);

                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void parseStalls(String responseBody) throws JSONException {
            JSONObject jsonObject = new JSONObject(responseBody);

            JSONArray jsonArray = jsonObject.getJSONArray("stall");

            for(int i=0; i<jsonArray.length(); i++){
                JSONObject stallObject = jsonArray.getJSONObject(i);

                Stall stall = new Stall();
                stall.setId(stallObject.getInt("id"));
                stall.setName(stallObject.getString("name"));
                stall.setPictureUrl(stallObject.getString("pictureURL"));

                mStallList.add(stall);
            }
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            StallAdapter adapter = new StallAdapter();
            mStallRecyclerView.setAdapter(adapter);
        }
    }


    private class StallAdapter extends RecyclerView.Adapter<StallViewHolder>{

        @Override
        public StallViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflator = LayoutInflater.from(getActivity());
            View v = inflator.inflate(R.layout.store_list_item,parent,false);
            return new StallViewHolder(v);
        }

        @Override
        public void onBindViewHolder(StallViewHolder holder, int position) {
            holder.bindStall(mStallList.get(position));
        }

        @Override
        public int getItemCount() {
            return mStallList.size();
        }
    }

    private class StallViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Stall mStall;
        private ImageView mStallImageView;
        private TextView mStallNameTextView;

        StallViewHolder(View itemView) {
            super(itemView);

            mStallImageView = (ImageView) itemView.findViewById(R.id.stall_image_view);
            mStallNameTextView = (TextView) itemView.findViewById(R.id.stall_name_text_view);
            itemView.setOnClickListener(this);
        }

        public void bindStall(Stall stall){

            mStall = stall;
            mStallNameTextView.setText(stall.getName());

            if(!mStall.getPictureUrl().isEmpty()&&mStall.getPictureUrl()!=null){
                Uri uri = Uri.parse(mStall.getPictureUrl());

                Picasso.with(getActivity()).load(uri).fit().centerInside().into(mStallImageView);
            }

        }

        @Override
        public void onClick(View view) {
            Intent i = StallInfoActivity.newIntent(getActivity(),mStall.getId());
            startActivity(i);
        }
    }

}
