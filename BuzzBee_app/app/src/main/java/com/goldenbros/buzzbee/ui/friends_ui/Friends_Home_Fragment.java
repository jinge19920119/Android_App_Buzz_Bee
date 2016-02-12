package com.goldenbros.buzzbee.ui.friends_ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.goldenbros.buzzbee.R;
import com.goldenbros.buzzbee.client.DefaultSocketClient_ClientSide;
import com.goldenbros.buzzbee.model.User;
import com.goldenbros.buzzbee.model.UserList;
import com.goldenbros.buzzbee.ui.login_ui.Login_Activity;
import com.goldenbros.buzzbee.util.CommandConstants;
import com.goldenbros.buzzbee.util.ConfirmMsgConstants;


public class Friends_Home_Fragment extends Fragment {

    private UserList friends_list;
    private boolean isListView;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    private FriendsListAdapter mAdapter;
    private User host_user;

    private static final String LOCAL_HOST = "128.237.167.57";
    private static final int PORT = 4444;
    public static final String EMAIL = "email";
    public static final String USER_ID = "CurrentUser";

    public static Friends_Home_Fragment newInstance() {
        Friends_Home_Fragment fragment = new Friends_Home_Fragment();

        return fragment;
    }

    public Friends_Home_Fragment() {
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
        View view = inflater.inflate(R.layout.friends_home_fragment, container, false);

        if(getFriendList()) {

            mRecyclerView = (RecyclerView) view.findViewById(R.id.friend_list);
            mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(mStaggeredLayoutManager);

            mAdapter = new FriendsListAdapter(getActivity().getBaseContext(), friends_list);
            mRecyclerView.setAdapter(mAdapter);

            mAdapter.setOnItemClickListener(onItemClickListener);
        }
        return view;
    }

    private boolean getFriendList(){

        // Connect to DB to get userlist
        host_user = getUser();
        StringBuilder sb = new StringBuilder();
        DefaultSocketClient_ClientSide dc = new DefaultSocketClient_ClientSide(LOCAL_HOST, PORT,
                CommandConstants.QUERY_FRIEND_LIST, sb, host_user.getID(), -1, friends_list);
        Thread t1 = new Thread(dc);
        t1.start();
        while(t1.isAlive()) {

        }
        Log.d("Query user list:", sb.toString());

        if(sb.toString().equals(ConfirmMsgConstants.QUERY_FRIEND_LIST_SUCCESS)) {
            Toast.makeText(getActivity(), "Query Friend List successfully!",
                    Toast.LENGTH_SHORT).show();
            friends_list = dc.getUserList();
            return true;
        } else {
            Toast.makeText(getActivity(), "Query Friend List fail...!",
                    Toast.LENGTH_SHORT).show();

            return false;
        }

    }


    private User getUser(){
        User user = new User();

        String email = getActivity().getIntent().getStringExtra(Login_Activity.EMAIL);
        user.setEmail(email);

        StringBuilder sb = new StringBuilder();

        DefaultSocketClient_ClientSide d = new DefaultSocketClient_ClientSide(LOCAL_HOST, PORT,
                CommandConstants.QUERY_USER_INFO, user, sb);
        Thread t1 = new Thread(d);
        t1.start();
        while(t1.isAlive()) {

        }

        user = d.getQueryUser();
        Log.d("Get User Info:", sb.toString());

        return user;
    }


    FriendsListAdapter.OnItemClickListener onItemClickListener =
            new FriendsListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    // 1
                    Intent transitionIntent = new Intent(getActivity(), Friends_Info_Activity.class);
                    transitionIntent.putExtra("CurrentUser", friends_list.getUser(position));
                    transitionIntent.putExtra("HostUser", host_user);
                    // 2
                    getActivity().startActivity(transitionIntent);
                }
            };

}
