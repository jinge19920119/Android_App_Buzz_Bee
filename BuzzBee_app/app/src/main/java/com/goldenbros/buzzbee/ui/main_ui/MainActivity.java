package com.goldenbros.buzzbee.ui.main_ui;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.goldenbros.buzzbee.R;
import com.goldenbros.buzzbee.chats.ChatList;
import com.goldenbros.buzzbee.chats.DatabaseLocalConnector;
import com.goldenbros.buzzbee.client.DefaultSocketClient_ClientSide;
import com.goldenbros.buzzbee.model.User;
import com.goldenbros.buzzbee.service.MessageService;
import com.goldenbros.buzzbee.ui.event_ui.Event_Home_Fragment;
import com.goldenbros.buzzbee.ui.friends_ui.Friends_Home_Fragment;
import com.goldenbros.buzzbee.ui.login_ui.Login_Activity;
import com.goldenbros.buzzbee.ui.schedule_ui.Schedule_Home_Fragment;
import com.goldenbros.buzzbee.ui.user_ui.User_Home_Fragment;
import com.goldenbros.buzzbee.util.CommandConstants;


public class MainActivity extends ActionBarActivity implements DrawerFragment.FragmentDrawerListener {

    private Toolbar mToolbar;
    private DrawerFragment df;

    private static final String LOCAL_HOST = "128.237.167.57";
    private static final int PORT = 4444;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        startChatService();
        DatabaseLocalConnector dc= new DatabaseLocalConnector(MainActivity.this);
        dc.deleteTable();

        df = (DrawerFragment)getSupportFragmentManager().
                findFragmentById(R.id.fragment_navigation_drawer);
        df.setUp(R.id.fragment_navigation_drawer,
                (DrawerLayout)findViewById(R.id.drawer_layout), mToolbar);
        df.setDrawerListener(this);
        displayView(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        if(id == R.id.action_search){
            Toast.makeText(getApplicationContext(),
                    "Search action is selected!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new Event_Home_Fragment();
                title = getString(R.string.title_events);
                break;
            case 1:
                fragment = new Schedule_Home_Fragment();
                title = getString(R.string.title_schedule);
                break;
            case 2:
                fragment = new Friends_Home_Fragment();
                title = getString(R.string.title_friends);
                break;
            case 3:
                fragment = new ChatList();
                title = "Chat History";

//                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
//                startActivity(intent);
                break;
            case 4:
                fragment = new User_Home_Fragment();
                title = getString(R.string.title_user);
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the main_toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String action = intent.getStringExtra("Caller");

        if(action != null) {
            if (action.equals("Friends_Info_Chat")) {
                displayView(3);
            } else if (action.equals("Friends_Info_Schedule")) {
                displayView(1);
            }
        }
    }

    private void startChatService(){
        final Intent intent2 = new Intent(MainActivity.this, MessageService.class);
   //     Intent intent2 = new Intent();
        User user = getUser();
        Log.d("Main Start Ser: uid:", user.getID()+"");
        intent2.putExtra("CURRENT_ID", user.getID() + "");
 //       intent2.setAction(MessageService.class.getName());
        startService(intent2);
        Toast.makeText(getApplicationContext(),
                "Chat Service Start Successfully", Toast.LENGTH_SHORT).show();
    }

    private User getUser(){
        User user = new User();

        String email = getIntent().getStringExtra(Login_Activity.EMAIL);
        user.setEmail(email);

        StringBuilder sb = new StringBuilder();

        DefaultSocketClient_ClientSide d = new DefaultSocketClient_ClientSide(LOCAL_HOST, PORT,
                CommandConstants.QUERY_USER_INFO, user, sb);
        Thread t1 = new Thread(d);
        t1.start();
        while(t1.isAlive()) {

        }

        user = d.getQueryUser();
        Log.d("Main GetUser Info:", sb.toString());

        return user;
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(MainActivity.this, MessageService.class));
        super.onDestroy();
    }
}
