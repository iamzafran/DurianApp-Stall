package com.durianapp.durianapp_stall.Fragment.DialogFragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.durianapp.durianapp_stall.Model.Promotion;
import com.durianapp.durianapp_stall.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Zafran on 7/8/2017.
 */

public class EditPromotionDialog extends DialogFragment {
    private static final int REQUEST_DATE = 2;
    private static final String ADD_DATE = "date";
    private static final String TAG = EditPromotionDialog.class.getSimpleName();
    public static final String EXTRA_UPDATED_PROMOTION = "EXTRA_EDIT_PROMOTION";
    private static final String ARGS_PROMOTION = "ARGS_PROMOTION";
    private Button mDateButton;
    private EditText mPromotionEditText;
    private String mPromotionString;
    private Date mPromoDate;
    private Promotion mPromotion;

    public static EditPromotionDialog newInstance(Promotion promotion) {

        Bundle args = new Bundle();

        EditPromotionDialog fragment = new EditPromotionDialog();
        args.putSerializable(ARGS_PROMOTION,promotion);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPromotion = (Promotion) getArguments().getSerializable(ARGS_PROMOTION);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.add_promotion_dialog,null);

        mDateButton = (Button) v.findViewById(R.id.date);
        mPromotionEditText = (EditText) v.findViewById(R.id.promotion_edit_text);
        mPromotionEditText.setText(mPromotion.getPromotionText());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String format = formatter.format(mPromotion.getEndDate());
        mDateButton.setText(format);

        mPromotionEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mPromotionString = charSequence.toString();
                mPromotion.setPromotionText(mPromotionString);
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
                dialog.setTargetFragment(EditPromotionDialog.this,REQUEST_DATE);
                dialog.show(manager,ADD_DATE);

            }
        });


        return new AlertDialog.Builder(getActivity()).setView(v).setTitle("Kemaskini Promosi")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        sendResult(Activity.RESULT_OK,mPromotion);
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
            mPromotion.setEndDate(mPromoDate);
            SimpleDateFormat dateformat = new SimpleDateFormat("dd-MMM-yyyy");
            String dateString = dateformat.format(mPromoDate);
            mDateButton.setText(dateString);

        }

    }

    private void sendResult(int resultCode, Promotion promo) {
        if (getTargetFragment() == null)
            return;

        Intent i = new Intent();
        i.putExtra(EXTRA_UPDATED_PROMOTION, promo);

        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, i);
    }
}
