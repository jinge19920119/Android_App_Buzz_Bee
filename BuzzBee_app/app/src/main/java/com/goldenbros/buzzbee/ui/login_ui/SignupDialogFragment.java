package com.goldenbros.buzzbee.ui.login_ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.goldenbros.buzzbee.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupDialogFragment extends DialogFragment {

    private EditText mEmail;
    private EditText mPassword;

    public interface SignupInputListener
    {
        void onSignupInputComplete(String username, String password);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.login_signup_fragment, null);
        mEmail = (EditText) view.findViewById(R.id.signup_email);
        mPassword = (EditText) view.findViewById(R.id.signup_password);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Sign Up",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                SignupInputListener listener = (SignupInputListener) getActivity();
                                String email = mEmail.getText().toString();
                                if (isValidEmail(email)) {
                                    listener.onSignupInputComplete(email, mPassword
                                            .getText().toString());
                                } else {
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "email is invalid, plz input correct email",
                                            Toast.LENGTH_LONG).show();
                                }

                            }
                        }).setNegativeButton("Cancel", null);
        return builder.create();
    }

    private boolean isValidEmail(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern p = Pattern.compile(ePattern);
        Matcher m = p.matcher(email);
        Log.d("check email....", "++++++++++++");
        return m.matches();
    }

}
