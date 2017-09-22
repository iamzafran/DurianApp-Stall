package com.durianapp.durianapp_stall.Fragment.DialogFragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.durianapp.durianapp_stall.Fragment.StallPromotionFragment;
import com.durianapp.durianapp_stall.Model.Promotion;
import com.durianapp.durianapp_stall.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lenovo on 7/3/2017.
 */

public class AddPromotionDialog extends DialogFragment {

    private static final int REQUEST_DATE = 1;
    private static final String ADD_DATE = "date";
    private static final String TAG = AddPromotionDialog.class.getSimpleName();
    public static final String EXTRA_PROMOTION = "EXTRA_PROMOTION";
    private Button mDateButton;
    private EditText mPromotionEditText;
    private String mPromotionString;
    private Date mPromoDate;

    public static AddPromotionDialog newInstance() {
        
        Bundle args = new Bundle();
        
        AddPromotionDialog fragment = new AddPromotionDialog();
        fragment.setArguments(args);
        return fragment;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.add_promotion_dialog,null);

        mDateButton = (Button) v.findViewById(R.id.date);
        mPromotionEditText = (EditText) v.findViewById(R.id.promotion_edit_text);

        mPromotionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    mPromotionString = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getChildFragmentManager();
                Date date = new Date();

                DatePickerFragment dialog = DatePickerFragment.newInstance(date);
                dialog.setTargetFragment(AddPromotionDialog.this,REQUEST_DATE);
                dialog.show(manager,ADD_DATE);

            }
        });


        return new AlertDialog.Builder(getActivity()).setView(v).setTitle("Tambah Promosi")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Promotion p = new Promotion();
                        p.setPromotionText(mPromotionString);
                        p.setEndDate(mPromoDate);
                        sendResult(Activity.RESULT_OK,p);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_CANCELED,null);
                    }
                })
                .create();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode==REQUEST_DATE){
            mPromoDate = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            Log.v(TAG,mPromoDate.toString());

            SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy");
            String dateString = dateformat.format(mPromoDate);
            mDateButton.setText(dateString);

        }

    }

    private void sendResult(int resultCode, Promotion promo) {
        if (getTargetFragment() == null)
            return;

        Intent i = new Intent();
        i.putExtra(EXTRA_PROMOTION, promo);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, i);
    }
}
