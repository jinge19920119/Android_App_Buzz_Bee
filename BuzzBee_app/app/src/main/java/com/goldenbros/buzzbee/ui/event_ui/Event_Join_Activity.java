package com.goldenbros.buzzbee.ui.event_ui;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Toast;
import android.widget.Toolbar;

import com.goldenbros.buzzbee.R;
import com.goldenbros.buzzbee.client.DefaultSocketClient_ClientSide;
import com.goldenbros.buzzbee.model.Event;
import com.goldenbros.buzzbee.model.EventList;
import com.goldenbros.buzzbee.ui.login_ui.Login_Activity;
import com.goldenbros.buzzbee.ui.main_ui.MainActivity;
import com.goldenbros.buzzbee.util.CommandConstants;
import com.goldenbros.buzzbee.util.ConfirmMsgConstants;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Event_Join_Activity extends ActionBarActivity {

    private Menu menu;
    private boolean isListView;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mStaggeredLayoutManager;
    private EventListAdapter mAdapter;
    private Toolbar toolbar;
    private List<Event> eList;
    private boolean canQueryAllEvent;
    private String email = null;

    private static final String LOCAL_HOST = "128.237.167.57";
    private static final int PORT = 4444;
    public static final String EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_join_activity);

        isListView = true;

        eList = getEventList();

        Bundle extra = getIntent().getExtras();
        email = extra.getString(Event_Home_Fragment.EMAIL);
        Log.d("Join Email",email);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mStaggeredLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mStaggeredLayoutManager);

        mAdapter = new EventListAdapter(this, eList);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(onItemClickListener);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Event List");
        setUpActionBar();
    }

    EventListAdapter.OnItemClickListener onItemClickListener =
            new EventListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    // 1
                    Intent transitionIntent = new Intent(Event_Join_Activity.this, EventDetailActivity.class);
                    transitionIntent.putExtra("CurrentEvent", eList.get(position));
                    Log.d("ONClick Email",email);
                    transitionIntent.putExtra(EMAIL, email);
                    ImageView placeImage = (ImageView) v.findViewById(R.id.eventImage);

                    LinearLayout placeNameHolder = (LinearLayout) v.findViewById(R.id.eventNameHolder);
                    // 2
                    View navigationBar = findViewById(android.R.id.navigationBarBackground);
                    View statusBar = findViewById(android.R.id.statusBarBackground);

                    Pair<View, String> imagePair = Pair.create((View) placeImage, "tImage");
                    Pair<View, String> holderPair = Pair.create((View) placeNameHolder, "tNameHolder");
                    // 3
                    Pair<View, String> navPair = Pair.create(navigationBar,
                            Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME);
                    Pair<View, String> statusPair = Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME);
                    Pair<View, String> toolbarPair = Pair.create((View) toolbar, "tActionBar");
                    // 4
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(Event_Join_Activity.this,
                            imagePair, holderPair, navPair, statusPair, toolbarPair);
                    ActivityCompat.startActivity(Event_Join_Activity.this, transitionIntent, options.toBundle());
                }
            };

    private void setUpActionBar() {
        if (toolbar != null) {
            setActionBar(toolbar);
            getActionBar().setDisplayHomeAsUpEnabled(false);
            getActionBar().setDisplayShowTitleEnabled(true);
            getActionBar().setElevation(7);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event__join_, menu);
        this.menu = menu;
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
        }if (id == R.id.action_toggle) {
            toggle();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toggle() {
        MenuItem item = menu.findItem(R.id.action_toggle);
        if (isListView) {
            mStaggeredLayoutManager.setSpanCount(2);
            item.setIcon(R.drawable.ic_action_list);
            item.setTitle("Show as list");
            isListView = false;
        } else {
            mStaggeredLayoutManager.setSpanCount(1);
            item.setIcon(R.drawable.ic_action_grid);
            item.setTitle("Show as grid");
            isListView = true;
        }
    }

    private List<Event> getEventList() {
        //get all events from DB
        StringBuilder sb = new StringBuilder();
        List<Event> rList = null;
        DefaultSocketClient_ClientSide d = new DefaultSocketClient_ClientSide(LOCAL_HOST, PORT,
                CommandConstants.QUERY_ALL_EVENTS, 1, sb);
        Thread t1 = new Thread(d);
        t1.start();
        while(t1.isAlive()) {

        }
        Log.d("----Acc qr_allEv info:", sb.toString());

        if(sb.toString().equals(ConfirmMsgConstants.ALL_EVENTS_GET)) {
            Toast.makeText(this, "Query All Events successfully!",
                    Toast.LENGTH_SHORT).show();

            rList = new ArrayList<Event>(d.getAllEvents().getList().values());
        } else {
            Toast.makeText(this, "Query Events fail...! No Events Exist",
                    Toast.LENGTH_SHORT).show();
            canQueryAllEvent = false;
        }

        return rList;
    }
}
