package com.goldenbros.buzzbee.ui.login_ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.goldenbros.buzzbee.R;

public class LoginDialogFragment extends DialogFragment {

    private EditText mUserEmail;
    private EditText mPassword;

    public interface LoginInputListener
    {
        void onLoginInputComplete(String username, String password);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.login_dialog_fragment, null);
        mUserEmail = (EditText) view.findViewById(R.id.id_txt_username);
        mPassword = (EditText) view.findViewById(R.id.id_txt_password);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Sign in",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                LoginInputListener listener = (LoginInputListener) getActivity();
                                listener.onLoginInputComplete(mUserEmail
                                        .getText().toString(), mPassword
                                        .getText().toString());
                            }
                        }).setNegativeButton("Cancel", null);
        return builder.create();
    }
}
