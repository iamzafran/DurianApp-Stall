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
 * Created by Lenovo on 6/18/2017.
 */

public class CameraOrGalleryDialog extends DialogFragment {

    public static final String EXTRA_NEW_OR_EXISTING = CameraOrGalleryDialog.class.getSimpleName()+"EXTRA_EXISTING_OR_NEW";


    public static CameraOrGalleryDialog newInstance() {

        CameraOrGalleryDialog fragment = new CameraOrGalleryDialog();
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.camera_or_gallery_dialog_layout,null);

        return new AlertDialog.Builder(getActivity()).setView(v).setTitle(R.string.image_source)
                .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK,true);
                    }
                })
                .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK,false);
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
        intent.putExtra(EXTRA_NEW_OR_EXISTING,isNew);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }
}
