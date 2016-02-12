package com.goldenbros.buzzbee.chats;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.goldenbros.buzzbee.R;
import com.goldenbros.buzzbee.client.DefaultSocketClient_ClientSide;
import com.goldenbros.buzzbee.model.User;
import com.goldenbros.buzzbee.model.UserList;
import com.goldenbros.buzzbee.ui.friends_ui.FriendsListAdapter;
import com.goldenbros.buzzbee.ui.login_ui.Login_Activity;
import com.goldenbros.buzzbee.util.CommandConstants;
import com.goldenbros.buzzbee.util.ConfirmMsgConstants;

import java.sql.SQLException;
import java.util.ArrayList;


public class ChatList extends Fragment implements chatdb_final{

    private static final String LOCAL_HOST = "128.237.167.57";
    private static final int PORT = 4444;
    private ListView listView;
    private DatabaseLocalConnector dc;
    private Cursor cursor;

    public ChatList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_chat_list, container, false);
        listView = (ListView)v.findViewById(R.id.listview);

//        progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setTitle("Loading");
//        progressDialog.setMessage("Please wait...");
//        progressDialog.show();
//        //broadcast receiver to listen for the broadcast
//        //from MessageService
//        BroadcastReceiver receiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                Boolean success = intent.getBooleanExtra("success", false);
//                progressDialog.dismiss();
//                //show a toast message if the Sinch
//                //service failed to start
//                if (!success) {
//                    Toast.makeText(getActivity(), "Messaging service failed to start", Toast.LENGTH_LONG).show();
//                }
//            }
//        };
//        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter("goldenbros.com.chats.ListUsersActivity"));


        dc= new DatabaseLocalConnector(getActivity());
        try {
            dc.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        cursor= dc.getAllChats();
        cursor.moveToFirst();
        ArrayList<String> names= new ArrayList<String>();
//        do{
//            String name = cursor.getString(cursor.getColumnIndex(CHATS_NAME));
//            names.add(name);
//        } while (cursor.moveToNext());

//        while(cursor.moveToNext()){
//            String name = cursor.getString(cursor.getColumnIndex(CHATS_NAME));
//            names.add(name);
//        }
        while(!cursor.isAfterLast()){
            String name = cursor.getString(cursor.getColumnIndex(CHATS_NAME));
            names.add(name);
            cursor.moveToNext();
        }
        cursor.close();
        String[] list= new String[names.size()];
        names.toArray(list);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    dc.open();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                cursor = dc.getOneChat(position+1);
                cursor.moveToFirst();
                String reci_id= "";
                Intent intent = new Intent(getActivity(), MessagingActivity.class);
                if(!cursor.isAfterLast()){
                    reci_id = cursor.getString(cursor.getColumnIndex(CHATS_RECEIVER_ID));
                }
                User user = getUser();

                if((reci_id!=null) && !reci_id.equals("null") ){
                    intent.putExtra("CURRENT_ID",user.getID() +"");
                    intent.putExtra("RECIPIENT_ID", reci_id + "");
                    startActivity(intent);
                } else {
                    String event_id = cursor.getString(cursor.getColumnIndex(CHATS_EVENT_ID));
                    UserList userList= getEventUserList(user.getID(), Integer.parseInt(event_id));
                    String[] user_list = userList.getUserIDArray();
//                        get group_users_id?, send them and current_id
                    String[] name_list = new String[user_list.length];
                    for(int i=0; i<user_list.length;i++){
                        name_list[i]= userList.getUser(i).getName();
                    }
                    intent.putExtra("GROUP_NAME_ID", name_list);
                    intent.putExtra("CURRENT_ID", user.getID() + "");
                    intent.putExtra("EVENT_ID", event_id);
                    intent.putExtra("GROUP_ID", user_list);
                    startActivity(intent);
                }
                cursor.close();
                dc.close();
            }
        });

        return v;
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


    private UserList getEventUserList(int user_id, int event_id){
        Log.d("Set up user list:", "Enter");
        // Connect to DB to get userlist
        UserList uList = null;
        StringBuilder sb = new StringBuilder();
        DefaultSocketClient_ClientSide dc = new DefaultSocketClient_ClientSide(LOCAL_HOST, PORT,
                CommandConstants.QUERY_EVENT_USERS, sb, user_id, event_id, uList);
        Thread t1 = new Thread(dc);
        t1.start();
        while(t1.isAlive()) {

        }
        Log.d("Query user list:", sb.toString());
        uList = dc.getUserList();
        Log.d("uList",(uList==null) +"");

        if(sb.toString().equals(ConfirmMsgConstants.QUERY_EVENT_USERS_SUCCESS)) {
            Toast.makeText(getActivity(), "Query Event Participants successfully!",
                    Toast.LENGTH_SHORT).show();

            return uList;
        } else {
            Toast.makeText(getActivity(), "Query Event Participants fail...!",
                    Toast.LENGTH_SHORT).show();
            return null;
        }
    }

}
