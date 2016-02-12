package com.goldenbros.buzzbee.ui.user_ui;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.goldenbros.buzzbee.R;

/**
 * Created by wang on 7/26/15.
 */
public class User_ChooseImage_Fragment extends DialogFragment
        implements View.OnClickListener{

    private Button selectPhotoButton;
    private Button takePhotoButton;



    public interface ChooseImageListener
    {
        void onStartCameraComplete();
        void onSelectPhotoComplete();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.user_choose_image_fragment, null);

        selectPhotoButton = (Button) view.findViewById(R.id.select_photo_button);
        takePhotoButton = (Button) view.findViewById(R.id.start_camera_button);

        selectPhotoButton.setOnClickListener(this);
        takePhotoButton.setOnClickListener(this);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view);

        return builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_photo_button:
                //select from photo booklet
                this.dismiss();

                ChooseImageListener listener2 = (ChooseImageListener) getActivity().getSupportFragmentManager().findFragmentById(R.id.container_body);
                listener2.onSelectPhotoComplete();

                break;

            case R.id.start_camera_button:
                this.dismiss();
                ChooseImageListener listener = (ChooseImageListener) getActivity().getSupportFragmentManager().findFragmentById(R.id.container_body);
                listener.onStartCameraComplete();

                break;
        }
    }




}
