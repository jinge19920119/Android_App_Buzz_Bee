package com.goldenbros.buzzbee.ui.event_ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.goldenbros.buzzbee.R;
import com.goldenbros.buzzbee.ui.login_ui.Login_Activity;
import com.goldenbros.buzzbee.ui.main_ui.MainActivity;

public class Event_Home_Fragment extends Fragment implements View.OnClickListener {

    TextView mFind;
    TextView mIssue;

    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";

    private String email;
    private String password;

    public static Event_Home_Fragment newInstance() {
        Event_Home_Fragment fragment = new Event_Home_Fragment();;

        return fragment;
    }

    public Event_Home_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.event_home_fragment, container, false);
        mFind = (TextView)v.findViewById(R.id.event_find);
        mIssue = (TextView)v.findViewById(R.id.event_issue);

        mFind.setOnClickListener(this);
        mIssue.setOnClickListener(this);

        Bundle extra = getActivity().getIntent().getExtras();
        email = extra.getString(Login_Activity.EMAIL);
        password = extra.getString(Login_Activity.PASSWORD);

        return v;
    }

    private void toFind(){
        Intent intent = new Intent(getActivity(), Event_Join_Activity.class);
        intent.putExtra(EMAIL, email);
        intent.putExtra(PASSWORD, password);
        startActivity(intent);
    }

    private void toIssue(){
        Intent intent = new Intent(getActivity(), Event_Issue_Activity.class);
        intent.putExtra(EMAIL, email);
        intent.putExtra(PASSWORD, password);
        startActivity(intent);
    }

    public void onClick(View v){
        Log.v("click", "onClick");
        switch(v.getId()){
            case R.id.event_find:
                Log.v("click", "onClick1");
                toFind();
                break;

            case R.id.event_issue:
                Log.v("click", "onClick2");
                toIssue();
                break;
            default: break;
        }
        Log.v("click","onClick3");
    }

}
