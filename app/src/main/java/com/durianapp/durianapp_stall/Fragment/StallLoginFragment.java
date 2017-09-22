package com.durianapp.durianapp_stall.Fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.durianapp.durianapp_stall.DurianStallActivity;
import com.durianapp.durianapp_stall.ManageStallActivity;
import com.durianapp.durianapp_stall.ManageStoreActivity;
import com.durianapp.durianapp_stall.Model.DurianAppSharedPreferences;
import com.durianapp.durianapp_stall.Model.Stall;
import com.durianapp.durianapp_stall.R;
import com.durianapp.durianapp_stall.StoreRegisterActivity;
import com.durianapp.durianapp_stall.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Password;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Lenovo on 6/17/2017.
 */

public class StallLoginFragment extends Fragment {


    private Stall mStall;
    private static final String GET_STALL_INFO = "http://durianapp.esy.es/getManageStallInfo.php?firebaseID=";


    public static StallLoginFragment newInstance() {
        
        Bundle args = new Bundle();
        
        StallLoginFragment fragment = new StallLoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private static final String TAG = StallLoginFragment.class.getSimpleName() ;
    @Email
    private EditText mEmailLoginEditText;

    @Password
    private EditText mPasswordLoginEditText;

    private Button mLogInButton;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private Validator mValidator;

    private TextView mRegisterTextView;

    private String mEmail;
    private String mPassword;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mValidator = new Validator(this);

        mValidator.setValidationListener(new Validator.ValidationListener() {
            @Override
            public void onValidationSucceeded() {
                logInUser();

            }

            @Override
            public void onValidationFailed(List<ValidationError> errors) {
                Toast.makeText(getActivity(), "Invalid input", Toast.LENGTH_SHORT).show();

            }
        });

        Util.isNetworkAvailable(getActivity());

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.store_login_fragment_layout,container,false);
        mEmailLoginEditText = (EditText) v.findViewById(R.id.email_login_edit_text);
        mPasswordLoginEditText = (EditText) v.findViewById(R.id.password_login_edit_text);
        mLogInButton = (Button) v.findViewById(R.id.login_button);
        mRegisterTextView = (TextView) v.findViewById(R.id.register_link_text_view);

        mEmailLoginEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mEmail = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        mPasswordLoginEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mPassword = charSequence.toString();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mLogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mValidator.validate();
                //logInUser();
            }
        });

        mRegisterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = StoreRegisterActivity.newIntent(getActivity());
                startActivity(i);
                getActivity().finish();
            }
        });

        return v;
    }




    @Override
    public void onStart() {
        super.onStart();

        mFirebaseUser = mFirebaseAuth.getCurrentUser();
    }


    private void logInUser(){

        Log.v(TAG,mPassword);
        mFirebaseAuth.signInWithEmailAndPassword(mEmail,mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                    mFirebaseUser = mFirebaseAuth.getCurrentUser();

                    if(mFirebaseUser!=null){
                        Log.v(TAG,mFirebaseUser.getUid());
                        new GetStallInfoTask().execute();


                    }else {
                        Toast.makeText(getActivity(), "Log In failed", Toast.LENGTH_SHORT).show();
                    }




            }
        });


    }

    private class GetStallInfoTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            getStoreInfo();
            return null;
        }

        private void getStoreInfo(){

            String firebaseID = mFirebaseUser.getUid();
            OkHttpClient client = new OkHttpClient();
            String request_url = GET_STALL_INFO+""+firebaseID;
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
            mStall.setId(jsonObject.getInt("stall_id"));
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
            Intent i = ManageStallActivity.newIntent(getActivity(),mStall);
            startActivity(i);
            getActivity().finish();
        }
    }





}
