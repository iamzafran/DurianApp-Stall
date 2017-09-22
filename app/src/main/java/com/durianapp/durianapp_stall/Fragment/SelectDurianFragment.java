package com.durianapp.durianapp_stall.Fragment;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.durianapp.durianapp_stall.Model.Durian;
import com.durianapp.durianapp_stall.Model.DurianAppSharedPreferences;
import com.durianapp.durianapp_stall.R;
import com.google.firebase.database.ChildEventListener;
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
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Lenovo on 6/19/2017.
 */

public class SelectDurianFragment extends Fragment {

    private static final String GET_DURIAN_URL = "http://durianapp.esy.es/getDurianStall.php";
    private static final String TAG = SelectDurianFragment.class.getSimpleName();
    private static final String STALL_ADD_DURIAN_URL = "http://durianapp.esy.es/addDurianAtStall.php";
    private static final String STALL_REMOVE_DURIAN_URL = "http://durianapp.esy.es/removeDurianAtStall.php";
    private static final String STALL_ID = "STALL_ID";
    private List<Durian> mDurianList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private DurianAdapter mDurianAdapter;
    private int mStallID;


    private Button mDoneButton;


    public static SelectDurianFragment newInstance() {
        
        Bundle args = new Bundle();
        
        SelectDurianFragment fragment = new SelectDurianFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static SelectDurianFragment newInstance(int id) {

        Bundle args = new Bundle();

        SelectDurianFragment fragment = new SelectDurianFragment();
        args.putInt(STALL_ID,id);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStallID = getArguments().getInt(STALL_ID);

        new GetDurianTask().execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.select_durian_fragment_layout,container,false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.durian_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        return v;
    }





    private void updateUI(){
        mDurianAdapter = new DurianAdapter();
        mRecyclerView.setAdapter(mDurianAdapter);
    }



    private class GetDurianTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            getDurians();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            updateUI();

        }

        private void getDurians(){
            OkHttpClient client = new OkHttpClient();
            String requestString = GET_DURIAN_URL;
            Request request = new Request.Builder().url(requestString).build();
            Call call = client.newCall(request);
            try {
                Response response = call.execute();
                if(response.isSuccessful()){
                    String responseBody = response.body().string();

                    Log.v(TAG,responseBody);

                    parseDurians(responseBody);

                }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        private void parseDurians(String response) throws JSONException{

            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("durians");

            for(int i=0; i<jsonArray.length(); i++)
            {
                JSONObject durianObject = jsonArray.getJSONObject(i);
                Durian durian = new Durian();
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
        }
    }



    private class DurianAdapter extends RecyclerView.Adapter<DurianViewHolder>{

        @Override
        public DurianViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.select_durian_list_item,parent,false);

            return new DurianViewHolder(v);
        }

        @Override
        public void onBindViewHolder(DurianViewHolder holder, int position) {
            Durian durian = mDurianList.get(position);
            holder.bindDurian(durian);
        }

        @Override
        public int getItemCount() {

            return mDurianList.size();
        }
    }


    private class DurianViewHolder extends RecyclerView.ViewHolder{

        private ImageView mDurianImageView;
        private TextView mDurianNameTextView;
        private TextView mDurianColorTextView;
        private TextView mDurianBitternessTextView;
        private TextView mDurianSweetnessTextView;
        private CheckBox mDurianSelectedCheckbox;
        private Durian mDurian;
        private boolean isAvailable;

        public DurianViewHolder(View itemView) {
            super(itemView);

            mDurianImageView = (ImageView) itemView.findViewById(R.id.durian_image_view);
            mDurianNameTextView = (TextView) itemView.findViewById(R.id.durian_name_text_view);
            mDurianColorTextView = (TextView) itemView.findViewById(R.id.color_text_view);
            mDurianBitternessTextView = (TextView) itemView.findViewById(R.id.bitterness_text_view);
            mDurianSweetnessTextView = (TextView) itemView.findViewById(R.id.sweetness_text_view);
            mDurianSelectedCheckbox = (CheckBox) itemView.findViewById(R.id.durian_selected_checkbox);

            this.setIsRecyclable(false);


        }





        public void bindDurian(Durian durian){
            mDurian = durian;
            Uri uri = Uri.parse(durian.getImg1());
            Picasso.with(getActivity()).load(uri).fit().centerInside().into(mDurianImageView);
            mDurianNameTextView.setText(mDurian.getName());
            mDurianColorTextView.setText(mDurian.getColor());
            mDurianBitternessTextView.setText(mDurian.getBitterness());
            mDurianSweetnessTextView.setText(mDurian.getSweetness());
            mDurianSelectedCheckbox.setOnCheckedChangeListener(null);
            checkIfDurianExists();

        }

        private void addDurian(){

            int durianID = mDurian.getId();



            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new FormBody.Builder().add("durian_id",mDurian.getId()+"")
                    .add("stall_id",mStallID+"").build();

            Request request = new Request.Builder().url(STALL_ADD_DURIAN_URL).post(requestBody).build();

            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    DatabaseReference storeReference = FirebaseDatabase.getInstance().getReference("stall_durian");
                    String key = storeReference.child(mStallID+"").push().getKey();

                    mDurian.setKey(key);
                    storeReference.child(mStallID+"").child(key).setValue(mDurian.getId());
                }
            });




        }

        private void removeDurian() {


            OkHttpClient client = new OkHttpClient();

            RequestBody requestBody = new FormBody.Builder().add("durian_id",mDurian.getId()+"")
                    .add("stall_id",mStallID+"").build();

            Request request = new Request.Builder().url(STALL_REMOVE_DURIAN_URL).post(requestBody).build();

            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    DatabaseReference storeReference = FirebaseDatabase.getInstance().getReference("stall_durian");
                    String key = mDurian.getKey();
                    Log.v(TAG,key);
                    storeReference.child(mStallID+"").child(key).setValue(null);
                }
            });




        }

        private void checkIfDurianExists(){


            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("stall_durian");
            DatabaseReference stallReference = reference.child(mStallID+"");

            stallReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.v(TAG,dataSnapshot.toString());

                    for(DataSnapshot child: dataSnapshot.getChildren() ){
                        int id = Integer.parseInt(child.getValue().toString());
                        isAvailable = false;

                        if(id==mDurian.getId()){
                            Log.v(TAG,id+"="+mDurian.getId()+"checked");
                            mDurian.setKey(child.getKey());
                            Log.v(TAG,mDurian.getKey());
                            isAvailable=true;
                            break;
                        }

                    }

                    mDurianSelectedCheckbox.setChecked(isAvailable);

                    mDurianSelectedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            if(b){
                                addDurian();
                            }else{
                                removeDurian();
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }





    }


}
