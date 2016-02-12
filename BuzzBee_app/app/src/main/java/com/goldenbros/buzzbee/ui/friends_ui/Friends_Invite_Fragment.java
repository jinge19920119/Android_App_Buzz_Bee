package com.goldenbros.buzzbee.ui.friends_ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenbros.buzzbee.R;

/**
 * Created by wang on 7/30/15.
 */
public class Friends_Invite_Fragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friends_invite_fragment, container, false);

        Button sendButton = (Button) view.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMail();
            }
        });

        return view;
    }

    /**
     * Send email to vocal artist
     */
    private void sendMail() {
        TextView toEmailTextView = (TextView) getActivity().findViewById(R.id.toEmailTextView);
        TextView subjectTextView = (TextView) getActivity().findViewById(R.id.subjectContentTextView);
        EditText contentEditText = (EditText) getActivity().findViewById(R.id.requestContentEditText);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:"));

        String[] emailList = new String[]{toEmailTextView.getText().toString()};
        intent.putExtra(Intent.EXTRA_EMAIL, emailList);
        intent.putExtra(Intent.EXTRA_SUBJECT, subjectTextView.getText().toString());
        intent.putExtra(Intent.EXTRA_TEXT, contentEditText.getText().toString());
        intent.setType("message/rfc822");

        Intent chooser = Intent.createChooser(intent, "Choose email-box:");
        startActivity(chooser);
    }
}
