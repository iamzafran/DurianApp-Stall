package com.durianapp.durianapp_stall.Fragment;

import android.content.Intent;
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
import android.widget.Toast;

import com.durianapp.durianapp_stall.EditStoreInfoActivity;
import com.durianapp.durianapp_stall.R;
import com.durianapp.durianapp_stall.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.List;

/**
 * Created by Lenovo on 6/17/2017.
 */

public class StoreRegisterFragment extends Fragment {

    private static final String TAG = StoreRegisterFragment.class.getSimpleName() ;
    //User Interface Objects
    @NotEmpty
    @Email
    private EditText mEmailEditText;

    @NotEmpty
    @Password
    private EditText mPasswordEditText;

    @NotEmpty
    @ConfirmPassword
    private EditText mConfirmPasswordEditText;

    private Button mRegisterButton;




    //Objects
    private Validator validator;
    private FirebaseAuth mFirebaseAuth; //Firebase authentication
    private FirebaseUser mFirebaseUser;
    private String email;
    private String password;


    public static StoreRegisterFragment newInstance(){
        return new StoreRegisterFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Verify input before registration
        validator = new Validator(this);
        validator.setValidationListener(new Validator.ValidationListener() {

            @Override
            public void onValidationSucceeded() {
                registerStall();
            }

            @Override
            public void onValidationFailed(List<ValidationError> errors) {

                Toast.makeText(getActivity(),"Invalid Input", Toast.LENGTH_SHORT).show();
            }
        });


        //initialize firebase
        mFirebaseAuth = FirebaseAuth.getInstance();

        Util.isNetworkAvailable(getActivity());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState ){
        View v = inflater.inflate(R.layout.store_register_fragment_layout,container,false);

        mEmailEditText = (EditText) v.findViewById(R.id.register_email_edit_text);
        mPasswordEditText = (EditText) v.findViewById(R.id.register_password_edit_text);
        mConfirmPasswordEditText = (EditText) v.findViewById(R.id.register_confirm_password_edit_text);
        mRegisterButton = (Button) v.findViewById(R.id.register_button);


        //get input for email
        mEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                email = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //get input for password
        mPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                password = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Validates the input before registration
                validator.validate();
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        mFirebaseUser = mFirebaseAuth.getCurrentUser();
    }

    private void registerStall(){


        mFirebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(), "Successfully registered", Toast.LENGTH_SHORT).show();

                    mFirebaseUser = mFirebaseAuth.getCurrentUser();

                    Log.v(TAG,mFirebaseUser.getUid());

                    Intent i = EditStoreInfoActivity.newIntent(getActivity());
                    startActivity(i);
                    getActivity().finish();
                }else {

                    Toast.makeText(getActivity(), "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


}
