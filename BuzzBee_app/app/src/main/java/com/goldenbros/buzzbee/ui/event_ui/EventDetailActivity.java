package com.goldenbros.buzzbee.ui.event_ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.transition.Transition;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.goldenbros.buzzbee.R;
import com.goldenbros.buzzbee.chats.DatabaseLocalConnector;
import com.goldenbros.buzzbee.chats.MessagingActivity;
import com.goldenbros.buzzbee.chats.chatdb_final;
import com.goldenbros.buzzbee.client.DefaultSocketClient_ClientSide;
import com.goldenbros.buzzbee.model.Event;
import com.goldenbros.buzzbee.model.User;
import com.goldenbros.buzzbee.model.UserList;
import com.goldenbros.buzzbee.ui.friends_ui.FriendsListAdapter;
import com.goldenbros.buzzbee.ui.friends_ui.Friends_Info_Activity;
import com.goldenbros.buzzbee.ui.login_ui.Login_Activity;
import com.goldenbros.buzzbee.ui.main_ui.DrawerFragment;
import com.goldenbros.buzzbee.util.*;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class EventDetailActivity extends FragmentActivity implements View.OnClickListener,  LocationListener, chatdb_final {

    //    private ListView mList;
    private ImageView mImageView;
    private TextView mTitle;
    private LinearLayout mTitleHolder;
    private ImageButton mAddButton;
    private boolean isActivityJoint = false;
    private Event mEvent;
    int defaultColor;
    private Bitmap photo;
    private GoogleMap googleMap;
    private TextView mLocation;
    private TextView mEventTime;
    private TextView mEventDesc;
    private double longitude, latitude;
    private LatLng eventLocation;
    private String email = null;

    private RecyclerView mRecyclerView;
    private Button mChatButton;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    private FriendsListAdapter mAdapter;
    private UserList uList = null;
    private User user;

    private static final String LOCAL_HOST = "128.237.167.57";
    private static final int PORT = 4444;
    public static final String EMAIL = "email";
    public static final String USER = "CurrentUser";
    public static final String HOST_USER = "HostUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        mEvent = (Event) getIntent().getSerializableExtra("CurrentEvent");

        String elocation = mEvent.getLocation();
        String[] a = elocation.split("/");
        latitude = Double.parseDouble(a[0]);
        longitude = Double.parseDouble(a[1]);

    //    Log.d("In Detail Create", latitude);
        eventLocation = new LatLng(latitude,longitude);
        //       mList = (ListView) findViewById(R.id.list);
        mImageView = (ImageView) findViewById(R.id.eventImage);
        mTitle = (TextView) findViewById(R.id.textView);
        mTitleHolder = (LinearLayout) findViewById(R.id.eventNameHolder);
        mAddButton = (ImageButton) findViewById(R.id.btn_add);

        mLocation = (TextView)findViewById(R.id.event_location);
        mEventTime = (TextView)findViewById(R.id.event_time);
        mEventDesc = (TextView)findViewById(R.id.event_dst);
        mChatButton = (Button) findViewById(R.id.group_chat_button);
        mChatButton.setOnClickListener(this);

        mAddButton.setOnClickListener(this);
        defaultColor = getResources().getColor(R.color.primary_dark);

        Bundle extra = getIntent().getExtras();
        email = extra.getString(EMAIL);
    //    Log.d("Detail Email", email);

        mEventDesc.setText(mEvent.getDesc());
        mEventTime.setText(mEvent.getDate() + ", " + mEvent.getTime());
        mLocation.setText(mEvent.getLocation());

        getUser();

        Log.d("In Detail Create", "Get User Success!");
        loadEvent();
        Log.d("In Detail Create", "Load Event!");
        windowTransition();
        Log.d("In Detail Create", "Window Transition!");
        getPhoto();
        Log.d("In Detail Create", "Get Photo!");
        try {
            if (googleMap == null) {
                googleMap = ((MapFragment) getFragmentManager().
                        findFragmentById(R.id.event_map)).getMap();
            }
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.setMyLocationEnabled(true);

            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(eventLocation,13);
            googleMap.animateCamera(cu);
            googleMap.addMarker(new MarkerOptions().position(eventLocation).title("Join Us!"));
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestProvider = locationManager.getBestProvider(criteria, true);

            locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setUpUserList();
    }

    private void setUpUserList(){
        Log.d("Set up user list:", "Enter");
        // Connect to DB to get userlist
        StringBuilder sb = new StringBuilder();
        DefaultSocketClient_ClientSide dc = new DefaultSocketClient_ClientSide(LOCAL_HOST, PORT,
                CommandConstants.QUERY_EVENT_USERS, sb, user.getID(), mEvent.getId(), uList);
        Thread t1 = new Thread(dc);
        t1.start();
        while(t1.isAlive()) {

        }
        Log.d("Query user list:", sb.toString());
        uList = dc.getUserList();
        Log.d("uList",(uList==null) +"");

        if(sb.toString().equals(ConfirmMsgConstants.QUERY_EVENT_USERS_SUCCESS)) {
            Toast.makeText(this, "Query Event Participants successfully!",
                    Toast.LENGTH_SHORT).show();
            mRecyclerView = (RecyclerView) findViewById(R.id.user_list);
            mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(mStaggeredLayoutManager);

            mAdapter = new FriendsListAdapter(this, uList);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            mAdapter.setOnItemClickListener(onItemClickListener);
        } else {
            Toast.makeText(this, "Query Event Participants fail...!",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void loadEvent() {
        mTitle.setText(mEvent.getName());
        mImageView.setImageBitmap(photo);
    }

    private void windowTransition() {
        getWindow().getEnterTransition().addListener(new TransitionAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                mAddButton.animate().alpha(1.0f);
                getWindow().getEnterTransition().removeListener(this);
            }
        });
    }

    private void getPhoto() {
        String pic_url = mEvent.getPhotoFilename();
        try {
            photo = new GetXMLTask().execute(pic_url).get();
            mImageView.setImageBitmap(photo);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        colorize(photo);
    }

    private void colorize(Bitmap photo) {
        Palette mPalette = Palette.generate(photo);
        applyPalette(mPalette);
    }

    private void applyPalette(Palette mPalette) {
        getWindow().setBackgroundDrawable(new ColorDrawable(mPalette.getDarkMutedColor(defaultColor)));
        mTitleHolder.setBackgroundColor(mPalette.getMutedColor(defaultColor));
        mLocation.setBackgroundColor(mPalette.getMutedColor(defaultColor));
//        tabLayout.setBackgroundColor(Color.HSVToColor(100, mPalette.getMutedSwatch().getHsl()));

    }

    private void addToDatabase(String recei_id, String event_id, String name){

        Boolean flag= true;
        DatabaseLocalConnector dc= new DatabaseLocalConnector(EventDetailActivity.this);
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
        getMenuInflater().inflate(R.menu.menu_event_detail, menu);
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
        Animatable mAnimatable;
        switch (v.getId()) {
            case R.id.btn_add:
                if (!isActivityJoint) {
                    isActivityJoint = true;
                    joinEventDB();
                    mAddButton.setImageResource(R.drawable.icn_morph);
                    mAnimatable = (Animatable) (mAddButton).getDrawable();
                    mAnimatable.start();
                } else {
                    isActivityJoint = false;
                    disjoinEventDB();
                    mAddButton.setImageResource(R.drawable.icn_morph_reverse);
                    mAnimatable = (Animatable) (mAddButton).getDrawable();
                    mAnimatable.start();
                }
                break;

            case R.id.group_chat_button:
                Intent intent = new Intent(EventDetailActivity.this, MessagingActivity.class);
                intent.putExtra("CURRENT_ID", user.getID() + "");
                intent.putExtra("EVENT_ID", mEvent.getId() + "");
                String[] att_list = uList.getUserIDArray();
                String[] name_list = new String [att_list.length];
                for(int i=0;i<att_list.length;i++){
                    name_list[i]= uList.getUser(i).getName();
                }
                intent.putExtra("GROUP_NAME_ID", name_list);
                intent.putExtra("GROUP_ID", att_list);
                addToDatabase(null, mEvent.getId() + "", mEvent.getName());
                startActivity(intent);
                break;
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(latLng));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }


    public void joinEventDB() {
        StringBuilder sb = new StringBuilder();
   //     User user = getUser();

        Log.d("UserEmail", user.getEmail());
        Log.d("UserID", user.getID()+"");

        DefaultSocketClient_ClientSide d = new DefaultSocketClient_ClientSide(LOCAL_HOST, PORT,
                CommandConstants.JOIN_EVENT, sb, user.getID(), mEvent.getId());
        Thread t1 = new Thread(d);
        t1.start();
        while(t1.isAlive()) {

        }
        Log.d("----Acc event info:", sb.toString());

        if(sb.toString().equals(ConfirmMsgConstants.JOIN_EVENT_SUCCESS)) {
            Toast.makeText(this, "Join Event successfully!",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Join Event fail...!",
                    Toast.LENGTH_SHORT).show();
        }
    }


    public void disjoinEventDB() {
        StringBuilder sb = new StringBuilder();
   //     User user = getUser();

        DefaultSocketClient_ClientSide d = new DefaultSocketClient_ClientSide(LOCAL_HOST, PORT,
                CommandConstants.DISJOIN_EVENT, sb, user.getID(), mEvent.getId());
        Thread t1 = new Thread(d);
        t1.start();
        while(t1.isAlive()) {

        }
        Log.d("----Acc event info:", sb.toString());

        if(sb.toString().equals(ConfirmMsgConstants.DISJOIN_EVENT_SUCCESS)) {
            Toast.makeText(this, "DisJoin Event successfully!",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "DisJoin Event fail...!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private User getUser(){
        Log.d("In Get User","Enter");
        user = new User();

        Log.d("UserEmail",email);
        user.setEmail(email);

        StringBuilder sb = new StringBuilder();

        DefaultSocketClient_ClientSide d = new DefaultSocketClient_ClientSide(LOCAL_HOST, PORT,
                CommandConstants.QUERY_USER_INFO, user, sb);
        Thread t1 = new Thread(d);
        t1.start();
        while(t1.isAlive()) {

        }
        user = d.getQueryUser();
        Log.d("Get User Info:", user.getID()+"");

        return user;
    }

    FriendsListAdapter.OnItemClickListener onItemClickListener =
            new FriendsListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Intent transitionIntent = new Intent(EventDetailActivity.this, Friends_Info_Activity.class);

                    transitionIntent.putExtra(USER, uList.getUser(position));
                    transitionIntent.putExtra(HOST_USER, user);
                    // 2
                    startActivity(transitionIntent);
                }
            };

}
