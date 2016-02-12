package com.goldenbros.buzzbee.ui.friends_ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenbros.buzzbee.R;
import com.goldenbros.buzzbee.chats.DatabaseLocalConnector;
import com.goldenbros.buzzbee.chats.MessagingActivity;
import com.goldenbros.buzzbee.chats.chatdb_final;
import com.goldenbros.buzzbee.client.DefaultSocketClient_ClientSide;
import com.goldenbros.buzzbee.model.User;
import com.goldenbros.buzzbee.ui.main_ui.MainActivity;
import com.goldenbros.buzzbee.util.CommandConstants;
import com.goldenbros.buzzbee.util.ConfirmMsgConstants;
import com.goldenbros.buzzbee.util.GetXMLTask;
import com.squareup.picasso.Picasso;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class Friends_Info_Activity extends ActionBarActivity implements View.OnClickListener, chatdb_final {

    private User user, host_user;
    private ImageView mImage;
    private TextView mkTextName;
    private TextView muTextName;
    private TextView mTextTitle;
    private TextView mTextEmail;
    private TextView mTextChat;
    private TextView mTextInvite;
    private TextView mTextViewSchedule;
    private TextView mTextMakeFriend;
    private RelativeLayout mRelLayout;

    private static final String LOCAL_HOST = "128.237.167.57";
    private static final int PORT = 4444;
    public static final String USER = "CurrentUser";
    public static final String HOST_USER = "HostUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        user = (User) getIntent().getSerializableExtra(USER);
        host_user = (User) getIntent().getSerializableExtra(HOST_USER);

        StringBuilder sb = new StringBuilder();
        DefaultSocketClient_ClientSide d = new DefaultSocketClient_ClientSide(LOCAL_HOST, PORT,
                  CommandConstants.CHECK_FRIEND_STATUS, sb, host_user.getID(), user.getID());

        Thread t1 = new Thread(d);
        t1.start();
        while(t1.isAlive()) {

        }
        Log.d("Query user list:", sb.toString());

        mRelLayout = (RelativeLayout) findViewById(R.id.friendInfoViewLayout);

//        mTextTitle = (TextView) findViewById(R.id.friend_title);

        if(sb.toString().equals(ConfirmMsgConstants.IS_FRIEND)) {
            setContentView(R.layout.friends_info_known_activity);
            mkTextName = (TextView) findViewById(R.id.known_friend_name);
            mTextTitle = (TextView) findViewById(R.id.friend_title);
            mTextEmail = (TextView) findViewById(R.id.friend_email);
            mImage = (ImageView) findViewById(R.id.friend_img);
            mTextChat = (TextView) findViewById(R.id.friend_start_chat);
            mTextInvite = (TextView) findViewById(R.id.friend_send_invitation);
            mTextViewSchedule = (TextView) findViewById(R.id.friend_view_schedule);
            mTextChat.setOnClickListener(this);
            mTextInvite.setOnClickListener(this);
            mTextViewSchedule.setOnClickListener(this);
            mkTextName.setText(user.getName());
            mTextTitle.setText("Beginner");
            mTextEmail.setText(user.getEmail());
        }else if(sb.toString().equals(ConfirmMsgConstants.NOT_FRIEND)){
            setContentView(R.layout.friends_info_unknown_activity);
            muTextName = (TextView) findViewById(R.id.unknown_friend_name);
            mTextEmail = (TextView) findViewById(R.id.friend_email);
            mImage = (ImageView) findViewById(R.id.friend_img);
            mTextTitle = (TextView) findViewById(R.id.friend_title);
            mTextMakeFriend = (TextView) findViewById(R.id.make_friend);
            mTextMakeFriend.setOnClickListener(this);
            muTextName.setText(user.getName());
            mTextTitle.setText("Beginner");
            mTextEmail.setText(user.getEmail());
        }else{
            Toast.makeText(this, sb.toString(),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Picasso.with(this).load(user.getPhotoName()).into(mImage);
//        mkTextName.setText(user.getName());
//        muTextName.setText(user.getName());
//        mTextTitle.setText(user.getTitle());
//        mTextEmail.setText(user.getEmail());

    //    setBackColor();
    }

    private void setBackColor(){
        Bitmap photo = null;
        try {
            photo = new GetXMLTask().execute(user.getPhotoName()).get();
            Palette.generateAsync(photo, new Palette.PaletteAsyncListener() {
                public void onGenerated(Palette palette) {
                    float[] hsl = palette.getMutedSwatch().getHsl();
                    mRelLayout.setBackgroundColor(Color.HSVToColor(150, hsl));
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    private void addToDatabase(String recei_id, String event_id, String name){

        Boolean flag= true;
        DatabaseLocalConnector dc= new DatabaseLocalConnector(Friends_Info_Activity.this);
        try {
            dc.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Cursor cursor = dc.getAllChats();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String nameNew = cursor.getString(cursor.getColumnIndex(CHATS_NAME));
            if (name!=null && name.equals(nameNew)) {
                flag = false;
                break;
            }
            cursor.moveToNext();
        }
        if(flag == true){
            dc.insertChats(recei_id, event_id, name);
        }
        cursor.close();
        dc.close();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends__info_, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.friend_start_chat:
//                Intent intent = new Intent(Friends_Info_Activity.this, MainActivity.class);
//                intent.putExtra("Caller","Friends_Info_Chat");
//                intent.putExtra("UserId", user.getID());
//                startActivity(intent);
                Intent intent = new Intent(Friends_Info_Activity.this, MessagingActivity.class);
                intent.putExtra("CURRENT_ID", host_user.getID()+"");
                intent.putExtra("RECIPIENT_ID",user.getID()+"");
                addToDatabase(user.getID() + "", null, user.getName());
                startActivity(intent);
                break;

            case R.id.friend_view_schedule:
                Intent intent2 = new Intent(Friends_Info_Activity.this, MainActivity.class);
                intent2.putExtra("Caller","Friends_Info_Schedule");
                intent2.putExtra("UserId", user.getID());
                startActivity(intent2);
                break;

            case R.id.friend_send_invitation:
                //send email
                Intent i = new Intent(Friends_Info_Activity.this, FriendsInviteActivity.class);
                startActivity(i);
                break;

            case R.id.make_friend:
                // Add Friend in DB
                StringBuilder sb = new StringBuilder();
                DefaultSocketClient_ClientSide d = new DefaultSocketClient_ClientSide(LOCAL_HOST, PORT,
                        CommandConstants.ADD_FRIEND, sb,host_user.getID(), user.getID());

                Thread t1 = new Thread(d);
                t1.start();
                while(t1.isAlive()) {

                }
                Log.d("Make Friend:", sb.toString());

                if(sb.toString().equals(ConfirmMsgConstants.ADD_FRIEND_SUCCESS)) {
                    Toast.makeText(this, "Add Friend Successfully!",
                            Toast.LENGTH_SHORT).show();
                    setContentView(R.layout.friends_info_known_activity);
                    mTextChat = (TextView) findViewById(R.id.friend_start_chat);
                    mTextInvite = (TextView) findViewById(R.id.friend_send_invitation);
                    mTextViewSchedule = (TextView) findViewById(R.id.friend_view_schedule);
                    mTextChat.setOnClickListener(this);

                    mTextInvite.setOnClickListener(this);
                    mTextViewSchedule.setOnClickListener(this);
                }else{
                    Toast.makeText(this, "Add Friend Failure!",
                            Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
}
