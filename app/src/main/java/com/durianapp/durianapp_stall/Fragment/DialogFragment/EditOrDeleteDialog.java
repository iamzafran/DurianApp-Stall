package com.durianapp.durianapp_stall.Fragment.DialogFragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.durianapp.durianapp_stall.R;

/**
 * Created by Zafran on 7/8/2017.
 */

public class EditOrDeleteDialog extends DialogFragment {
    public static final String EXTRA_EDIT_OR_DELETE = EditOrDeleteDialog.class.getSimpleName()+"EXTRA_EDIT_OR_DELETE";


    public static EditOrDeleteDialog newInstance() {

        EditOrDeleteDialog fragment = new EditOrDeleteDialog();
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.edit_or_delete_dialog_layout,null);

        return new AlertDialog.Builder(getActivity()).setView(v).setTitle("Promotion")
                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK,false);
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK,true);
                    }
                })
                .create();

    }


    private void sendResult(int resultCode,Boolean isNew)
    {
        if(getTargetFragment()==null)
        {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_EDIT_OR_DELETE,isNew);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }
}
