package com.durianapp.durianapp_stall.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.durianapp.durianapp_stall.DurianInfoActivity;
import com.durianapp.durianapp_stall.Model.Durian;
import com.durianapp.durianapp_stall.Model.Promotion;
import com.durianapp.durianapp_stall.Model.Stall;
import com.durianapp.durianapp_stall.R;
import com.durianapp.durianapp_stall.Util;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Lenovo on 6/19/2017.
 */

public class StallInfoFragment extends Fragment {

    private static final String TAG = StallInfoFragment.class.getSimpleName() ;
    private static final Object GET_STALL_INFO = "http://durianapp.esy.es/getStallInfo.php?stall_id=" ;
    private static final String STALL_ID_ARGS =  "STALL_ID_ARGS";
    private static final String GET_DURIAN_INFO = "http://durianapp.esy.es/getDurian.php?id=";
    private static final String GET_STALL_PROMOTION = "http://durianapp.esy.es/getStallPromotion.php?id=";


    private ImageView mStallImageView;
    private TextView mStallNameTextView;
    private TextView mStallAddressTextView;
    private TextView mStallPhoneTextView;
    private TextView mStallLocalityTextView;
    private RecyclerView mDurianRecyclerView;
    private Button mNavaigationButton;
    private DurianAdapter mDurianAdapter;
    private Stall mStall;



    private int mStallID;
    private List<Durian> mDurianList = new ArrayList<>();
    private List<Promotion> mPromotionList = new ArrayList<>();
    private PromotionAdapter mPromotionAdapter;
    private RecyclerView mPromotionRecyclerView;

    public static StallInfoFragment newInstance(int id) {
        
        Bundle args = new Bundle();
        
        StallInfoFragment fragment = new StallInfoFragment();
        args.putInt(STALL_ID_ARGS,id);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mStallID = args.getInt(STALL_ID_ARGS);

        Util.isNetworkAvailable(getActivity());
        Util.isGpsIsEnabled(getActivity());


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.stall_info_fragment_layout,container,false);

        mStallImageView = (ImageView) v.findViewById(R.id.stall_image_view);
        mStallNameTextView = (TextView) v.findViewById(R.id.stall_name_text_view);
        mStallAddressTextView = (TextView) v.findViewById(R.id.stall_address_text_view);
        mStallLocalityTextView = (TextView) v.findViewById(R.id.stall_locality_text_view);
        mStallPhoneTextView = (TextView) v.findViewById(R.id.stall_phone_text_view);
        mDurianRecyclerView = (RecyclerView) v.findViewById(R.id.durian_list_recycler_view);
        mDurianRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        mNavaigationButton = (Button) v.findViewById(R.id.navigation_button);
        mPromotionRecyclerView = (RecyclerView) v.findViewById(R.id.promotion_recycler_view);

        mPromotionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));

        new GetStallInfoTask().execute();

        FragmentManager fm = getChildFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.stall_map_fragment_container);

        if(fragment==null){
            fragment = StallLocationMapFragment.newInstance(mStallID);

            fm.beginTransaction().add(R.id.stall_map_fragment_container,fragment).commit();
        }

        getDurianKeys();

        mDurianAdapter = new DurianAdapter();
        mDurianRecyclerView.setAdapter(mDurianAdapter);

        mNavaigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToStore();
            }
        });

        new GetPromotionsTask().execute();
        return v;
    }

    private void navigateToStore() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("store_location");

        GeoFire georef = new GeoFire(reference);

        georef.getLocation(mStallID + "", new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+location.latitude+","+location.longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                startActivity(mapIntent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateUI(){

        mStallNameTextView.setText(mStall.getName());
        String addresString = mStall.getAddress()+", "+mStall.getPostcode()+", "+mStall.getCity()+", "+mStall.getState();
        mStallAddressTextView.setText(addresString);
        mStallLocalityTextView.setText(mStall.getLocality());
        mStallPhoneTextView.setText(mStall.getPhone());


        if(!mStall.getPictureUrl().isEmpty()){

            Uri uri = Uri.parse(mStall.getPictureUrl());
            Picasso.with(getActivity()).load(uri).fit().centerInside().into(mStallImageView);
        }



    }

    private void getDurianKeys(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("stall_durian");
        DatabaseReference stallReference = reference.child(mStallID+"");

        stallReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v(TAG,dataSnapshot.toString());

                for(DataSnapshot child: dataSnapshot.getChildren() ){
                    int id = Integer.parseInt(child.getValue().toString());
                    new GetDurianTask().execute(id);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    private class DurianAdapter extends RecyclerView.Adapter<DurianViewHolder>{

        @Override
        public DurianViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.durian_list_item_layout,parent,false);
            return new DurianViewHolder(v);
        }

        @Override
        public void onBindViewHolder(DurianViewHolder holder, int position) {
            holder.bindView(mDurianList.get(position));
        }

        @Override
        public int getItemCount() {
            return mDurianList.size();
        }
    }


    private class DurianViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Durian mDurian;
        private ImageView mDurianImageView;
        private TextView mDurianTextView;

        public DurianViewHolder(View itemView) {
            super(itemView);

            mDurianImageView = (ImageView) itemView.findViewById(R.id.durian_image_view);
            mDurianTextView = (TextView) itemView.findViewById(R.id.durian_name_text_view);
            itemView.setOnClickListener(this);
        }


        public void bindView(Durian durian){
            mDurian = durian;
            if(!mDurian.getImg1().isEmpty()){

                Uri uri = Uri.parse(mDurian.getImg1());
                Picasso.with(getActivity()).load(uri).fit().centerInside().into(mDurianImageView);
            }
            mDurianTextView.setText(mDurian.getName());
        }

        @Override
        public void onClick(View view) {
            Intent i = new Intent(getActivity(),DurianInfoActivity.class);
            i.putExtra("DURIAN_ID",mDurian.getId());
            i.putExtra("DURIAN_TITLE",mDurian.getName());
            startActivity(i);

        }
    }


    private class GetStallInfoTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            getStoreInfo();
            return null;
        }


        private void getStoreInfo(){
            OkHttpClient client = new OkHttpClient();
            String request_url = GET_STALL_INFO+""+mStallID;
            Request request = new Request.Builder().url(request_url).build();
            try {
                Response response = client.newCall(request).execute();
                if(response.isSuccessful())
                {

                    String responseBody = response.body().string();

                    Log.v(TAG,responseBody);
                    JSONObject jsonObject = new JSONObject(responseBody);
                    parseStall(jsonObject);

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        private void parseStall(JSONObject jsonObject)throws JSONException{

            mStall = new Stall();
            mStall.setName(jsonObject.getString("stall_name"));
            mStall.setAddress(jsonObject.getString("stall_address"));
            mStall.setCity(jsonObject.getString("stall_city"));
            mStall.setLocality(jsonObject.getString("stall_locality"));
            mStall.setPhone(jsonObject.getString("stall_phone"));
            mStall.setPostcode(jsonObject.getString("postcode"));
            mStall.setState(jsonObject.getString("stall_state"));
            mStall.setPictureUrl(jsonObject.getString("picture_url"));



        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateUI();
        }
    }

    private class GetDurianTask extends AsyncTask<Integer,Void,Void>{

        private Durian durian;
        @Override
        protected Void doInBackground(Integer... integers) {

            getDurian(integers[0]);
            return null;
        }


        private void getDurian(int id){
            OkHttpClient client = new OkHttpClient();
            String request_url = GET_DURIAN_INFO+""+id;
            Request request = new Request.Builder().url(request_url).build();
            try {
                Response response = client.newCall(request).execute();
                if(response.isSuccessful())
                {

                    String responseBody = response.body().string();

                    Log.v(TAG,responseBody);
                    JSONObject jsonObject = new JSONObject(responseBody);
                    parseDurian(jsonObject);

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void parseDurian(JSONObject durianObject) throws JSONException{


            durian = new Durian();
            durian.setId(Integer.parseInt(durianObject.getString("id")));
            durian.setName(durianObject.getString("name"));
            durian.setShape(durianObject.getString("fruit_shape"));
            durian.setSpine(durianObject.getString("spine_shape"));
            durian.setColor(durianObject.getString("color"));
            durian.setSweetness(durianObject.getString("sweetness"));
            durian.setBitterness(durianObject.getString("bitterness"));
            durian.setImg1(durianObject.getString("img_1"));
            durian.setImg2(durianObject.getString("img_2"));
            durian.setImg3(durianObject.getString("img_3"));
            durian.setImg4(durianObject.getString("img_4"));
            mDurianList.add(durian);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mDurianAdapter.notifyItemInserted(mDurianList.indexOf(durian));

        }
    }


    private class GetPromotionsTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            getStorePromotion();

            return null;
        }

        private void getStorePromotion(){

            OkHttpClient client = new OkHttpClient();
            String request_url = GET_STALL_PROMOTION+""+mStallID;
            Request request = new Request.Builder().url(request_url).build();
            try {
                Response response = client.newCall(request).execute();
                if(response.isSuccessful())
                {

                    String responseBody = response.body().string();

                    Log.v(TAG,responseBody);
                    JSONObject jsonObject = new JSONObject(responseBody);
                    parsePromotion(jsonObject);

                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        private void parsePromotion(JSONObject jsonObject) throws JSONException, ParseException {
            JSONArray jsonArray = jsonObject.getJSONArray("promotions");

            for(int i=0; i<jsonArray.length(); i++)
            {
                JSONObject promoObject = jsonArray.getJSONObject(i);

                Promotion promotion = new Promotion();
                promotion.setPromotionId(Integer.parseInt(promoObject.getString("id")));
                promotion.setPromotionText(promoObject.getString("text"));

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date date = format.parse(promoObject.getString("validUntil"));
                promotion.setEndDate(date);

                mPromotionList.add(promotion);
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mPromotionAdapter = new PromotionAdapter();
            mPromotionRecyclerView.setAdapter(mPromotionAdapter);

        }
    }


    private class PromotionAdapter extends RecyclerView.Adapter<PromotionViewHolder>{

        @Override
        public PromotionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.promotion_item_layout,parent,false);

            return new PromotionViewHolder(v);
        }

        @Override
        public void onBindViewHolder(PromotionViewHolder holder, int position) {

            holder.bindView(mPromotionList.get(position));
        }

        @Override
        public int getItemCount() {
            return mPromotionList.size();
        }
    }

    private class PromotionViewHolder extends RecyclerView.ViewHolder{

        private TextView mPromotionStringTextView;
        private TextView mPromotionDatelineTextView;
        private Promotion mPromotion;

        public PromotionViewHolder(View itemView) {
            super(itemView);
            mPromotionStringTextView = (TextView) itemView.findViewById(R.id.promotion_text);
            mPromotionDatelineTextView = (TextView) itemView.findViewById(R.id.promotion_date_text_view);
        }

        public void bindView(Promotion promotion){
            mPromotion = promotion;
            mPromotionStringTextView.setText(mPromotion.getPromotionText());
            Date date = mPromotion.getEndDate();

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedString = formatter.format(date);

            mPromotionDatelineTextView.setText(formattedString);
        }
    }



}
