package com.goldenbros.buzzbee.ui.login_ui;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import com.goldenbros.buzzbee.R;
import com.goldenbros.buzzbee.client.DefaultSocketClient_ClientSide;
import com.goldenbros.buzzbee.model.User;
import com.goldenbros.buzzbee.ui.main_ui.MainActivity;
import com.goldenbros.buzzbee.util.CommandConstants;
import com.goldenbros.buzzbee.util.ConfirmMsgConstants;

public class Login_Activity extends FragmentActivity
        implements LoginDialogFragment.LoginInputListener,
                    SignupDialogFragment.SignupInputListener, OnClickListener {

    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";

    private static final String LOCAL_HOST = "128.237.167.57";
    private static final int PORT = 4444;
    private StringBuilder sb;

    private TextView mLogin;
    private TextView mSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        mLogin = (TextView) findViewById(R.id.login_login);
        mSignup = (TextView) findViewById(R.id.login_signup);

        mLogin.setOnClickListener(this);
        mSignup.setOnClickListener(this);
    }

    public void showLoginDialog()
    {
        LoginDialogFragment dialog = new LoginDialogFragment();
        dialog.show(getSupportFragmentManager(), "loginDialog");
    }

    public void onLoginInputComplete(String email, String password)
    {
        sb = new StringBuilder();
        User user = new User(email, password);
        DefaultSocketClient_ClientSide d = new DefaultSocketClient_ClientSide(LOCAL_HOST, PORT,
                CommandConstants.VERIFY, user, sb);
        Thread t1 = new Thread(d);
        t1.start();

        while(t1.isAlive()) {

        }
        Log.d("----Acc verify info:", sb.toString());

        if(sb.toString().equals(ConfirmMsgConstants.VALID_USER)) {

            Toast.makeText(this, "Welcome back!",
                    Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Email:" + email + "\npassword:" + password,
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Login_Activity.this,
                    MainActivity.class);
            intent.putExtra(EMAIL, email);
            intent.putExtra(PASSWORD, password);
            startActivity(intent);
        } else {
            Toast.makeText(this, "User invalid, please input again" + "\nor sign up a new account",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void showSignupDialog()
    {
        SignupDialogFragment dialog = new SignupDialogFragment();
        dialog.show(getSupportFragmentManager(), "signupDialog");
    }

    public void onSignupInputComplete(String email, String password) {
        sb = new StringBuilder();
        User user = new User(email, password);
        DefaultSocketClient_ClientSide d = new DefaultSocketClient_ClientSide(LOCAL_HOST, PORT,
                CommandConstants.CREATE_NEW_USER, user, sb);
        Thread t1 = new Thread(d);
        t1.start();

        while(t1.isAlive()) {

        }
        Log.d("----Acc NewUser info:", sb.toString());

        if(sb.toString().equals(ConfirmMsgConstants.NEW_USER_ADDED)) {

            Toast.makeText(this, "Welcome new user!",
                    Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Email:" + email + "\npassword:" + password,
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Login_Activity.this,
                    MainActivity.class);
            intent.putExtra(EMAIL, email);
            intent.putExtra(PASSWORD, password);
            startActivity(intent);
        } else {
            Toast.makeText(this, "User already exist...",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void onClick(View v){
        Log.v("click","onClick_Start");
        switch(v.getId()){
            case R.id.login_login:
                Log.v("click","onClick_Login");
                showLoginDialog();
                break;

            case R.id.login_signup:
                Log.v("click","onClick_Signup");
                showSignupDialog();
                break;
            default: break;
        }
        Log.v("click","onClick_End");
    }

}