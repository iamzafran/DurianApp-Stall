package com.durianapp.durianapp_stall.Fragment;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.TextView;

import com.durianapp.durianapp_stall.Fragment.DialogFragment.AddPromotionDialog;
import com.durianapp.durianapp_stall.Fragment.DialogFragment.EditOrDeleteDialog;
import com.durianapp.durianapp_stall.Fragment.DialogFragment.EditPromotionDialog;
import com.durianapp.durianapp_stall.Model.Promotion;
import com.durianapp.durianapp_stall.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by Lenovo on 7/2/2017.
 */

public class StallPromotionFragment extends Fragment {

    private static final int REQUEST_ADD_PROMOTION = 1;
    private static final String ADD_PROMO = "ADD_PROMOTION_DIALOG";
    private static final String TAG = StallPromotionFragment.class.getSimpleName();
    private static final String ADD_PROMOTION_URL = "http://durianapp.esy.es/addPromotion.php";
    private static final String STALL_ID =  "STALL_ID";
    private static final String GET_STALL_PROMOTION = "http://durianapp.esy.es/getStallPromotion.php?id=";
    private static final int REQUEST_EDIT_DELETE = 2;
    private static final String EDIT_OR_DELETE = "EDIT_OR_DELETE_PROMO";
    private static final String DELETE_PROMOTION = "http://durianapp.esy.es/deletePromotion.php";
    private static final int REQUEST_EDIT_PROMOTION = 3;
    private static final String EDIT_PROMOTION = "EDIT_PROMOTION" ;
    private static final String UPDATE_PROMOTION = "http://durianapp.esy.es/updatePromotion.php" ;
    private Button mAddPromotionButton;
    private RecyclerView mPromotionRecyclerView;
    private PromotionAdapter mPromotionAdapter;
    private int mStallID;
    private List<Promotion> mPromotionList = new ArrayList<>();

    private Promotion cachePromotion;


    public static StallPromotionFragment newInstance(int stallID) {
        
        Bundle args = new Bundle();
        args.putInt(STALL_ID,stallID);
        StallPromotionFragment fragment = new StallPromotionFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.stall_promotion_fragment_layout,container,false);
        mAddPromotionButton = (Button) v.findViewById(R.id.add_promotion_button);

        mStallID = getArguments().getInt(STALL_ID);

        mAddPromotionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                AddPromotionDialog dialog = AddPromotionDialog.newInstance();
                dialog.setTargetFragment(StallPromotionFragment.this,REQUEST_ADD_PROMOTION);
                dialog.show(manager,ADD_PROMO);
            }
        });

        mPromotionRecyclerView = (RecyclerView) v.findViewById(R.id.promotion_recycler_view);
        mPromotionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        new GetPromotionsTask().execute();
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode==REQUEST_ADD_PROMOTION){
           Promotion promotion = (Promotion) data.getSerializableExtra(AddPromotionDialog.EXTRA_PROMOTION);
            Log.v(TAG,promotion.getPromotionText());
            new AddPromotionTask().execute(promotion);
        }else if(requestCode==REQUEST_EDIT_DELETE){
            boolean isDelete = data.getBooleanExtra(EditOrDeleteDialog.EXTRA_EDIT_OR_DELETE,false);

            if(isDelete){
                Log.v(TAG,isDelete+"");
                new DeletePromotionTask().execute();
                
            }else {
                Log.v(TAG, isDelete+"");

                FragmentManager manager = getFragmentManager();
                EditPromotionDialog editPromotionDialog = EditPromotionDialog.newInstance(cachePromotion);
                editPromotionDialog.setTargetFragment(StallPromotionFragment.this,REQUEST_EDIT_PROMOTION);
                editPromotionDialog.show(manager,EDIT_PROMOTION);

            }
        }else if(requestCode==REQUEST_EDIT_PROMOTION){
            Promotion promotion = (Promotion) data.getSerializableExtra(EditPromotionDialog.EXTRA_UPDATED_PROMOTION);
            new UpdatePromotionTask().execute(promotion);

        }
    }

  
    
    private class DeletePromotionTask extends AsyncTask<Void,Void,Boolean> {


        @Override
        protected Boolean doInBackground(Void... params) {
            return deletePromotion();
        }

        private boolean deletePromotion() {
            OkHttpClient client = new OkHttpClient();


            RequestBody requestBody = new FormBody.Builder().add("promo_id", cachePromotion.getPromotionId() + "")
                    .build();

            Request request = new Request.Builder().url(DELETE_PROMOTION).post(requestBody).build();

            Call call = client.newCall(request);
            try {
                Response response = call.execute();
                if (response.isSuccessful()){
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean){
                int index = mPromotionList.indexOf(cachePromotion);
                mPromotionList.remove(index);
                mPromotionAdapter.notifyItemRemoved(index);
            }
        }
    }


    private class UpdatePromotionTask extends AsyncTask<Promotion,Void,Boolean>{

        private Promotion promotion;

        @Override
        protected Boolean doInBackground(Promotion... params) {
            promotion = params[0];
            return updatePromotion(promotion);
        }

        private Boolean updatePromotion(Promotion promtion){
            OkHttpClient client = new OkHttpClient();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String date = formatter.format(promtion.getEndDate());

            RequestBody requestBody = new FormBody.Builder().add("promo_id", promtion.getPromotionId() + "")
                    .add("promotion_text",promtion.getPromotionText())
                    .add("valid_until",date)
                    .build();

            Request request = new Request.Builder().url(UPDATE_PROMOTION).post(requestBody).build();

            Call call = client.newCall(request);
            try {
                Response response = call.execute();
                if (response.isSuccessful()){
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean){
                int index = mPromotionList.indexOf(cachePromotion);
                mPromotionList.set(index,promotion);
                mPromotionAdapter.notifyItemChanged(index);
            }

        }
    }




    private class AddPromotionTask extends AsyncTask<Promotion,Void,Response>{

        private Promotion promotion;
        @Override
        protected Response doInBackground(Promotion... promotions) {
            promotion = promotions[0];

            return addPromotion();
        }


        private Response addPromotion() {
            OkHttpClient client = new OkHttpClient();

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            String date = format.format(promotion.getEndDate());

            RequestBody requestBody = new FormBody.Builder().add("promo",promotion.getPromotionText())
                    .add("date",date)
                    .add("stallID",mStallID+"")
                    .build();

            Request request = new Request.Builder().url(ADD_PROMOTION_URL).post(requestBody).build();

            Call call = client.newCall(request);
            Response response = null;
            try {
                response = call.execute();


            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }


        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            if(response.isSuccessful()){
                String responseBody = null;
                try {
                    responseBody = response.body().string();
                    Log.v(TAG,responseBody);
                    JSONObject jsonObject = new JSONObject(responseBody);
                    promotion.setPromotionId(jsonObject.getInt("id"));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                mPromotionList.add(promotion);
                mPromotionAdapter.notifyItemInserted(mPromotionList.size()-1);
            }
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
            View v = inflater.inflate(R.layout.promotion_list_item,parent,false);

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

    private class PromotionViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

        private TextView mPromotionStringTextView;
        private TextView mPromotionDatelineTextView;
        private Promotion mPromotion;

        public PromotionViewHolder(View itemView) {
            super(itemView);
            mPromotionStringTextView = (TextView) itemView.findViewById(R.id.promotion_text);
            mPromotionDatelineTextView = (TextView) itemView.findViewById(R.id.promotion_date_text_view);
            itemView.setOnLongClickListener(this);
        }

        public void bindView(Promotion promotion){
            mPromotion = promotion;
            mPromotionStringTextView.setText(mPromotion.getPromotionText());
            Date date = mPromotion.getEndDate();

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedString = formatter.format(date);

            mPromotionDatelineTextView.setText(formattedString);
        }

        @Override
        public boolean onLongClick(View v) {

            Log.v(TAG,mPromotion.getPromotionText());
            FragmentManager manager = getFragmentManager();
           EditOrDeleteDialog editOrDeleteDialog = EditOrDeleteDialog.newInstance();
            editOrDeleteDialog.setTargetFragment(StallPromotionFragment.this,REQUEST_EDIT_DELETE);
            editOrDeleteDialog.show(manager,EDIT_OR_DELETE);
            cachePromotion = mPromotion;

            return false;
        }
    }

}
