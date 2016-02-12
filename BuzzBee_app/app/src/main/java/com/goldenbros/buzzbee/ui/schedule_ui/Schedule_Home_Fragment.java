package com.goldenbros.buzzbee.ui.schedule_ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenbros.buzzbee.R;
import com.goldenbros.buzzbee.client.DefaultSocketClient_ClientSide;
import com.goldenbros.buzzbee.model.Event;
import com.goldenbros.buzzbee.model.EventList;
import com.goldenbros.buzzbee.model.User;
import com.goldenbros.buzzbee.ui.login_ui.Login_Activity;
import com.goldenbros.buzzbee.util.CommandConstants;
import com.goldenbros.buzzbee.util.ConfirmMsgConstants;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Schedule_Home_Fragment extends Fragment
        implements View.OnClickListener, Schedule_DatePicker_Diaolog_Fragment.EventDatePickerListener {

    private static final String LOCAL_HOST = "128.237.167.57";
    private static final int PORT = 4444;

    private ImageButton dateChooseButton;
    private TextView dateChooseTextView;
    private Button listAllEventsButton;

    private static final int DIALOG_FRAGMENT = 2;

    private ListView eventsListView;

    private StringBuilder sb;

    private EventList eventList;
    private LinkedHashMap<Integer, Event> eventsMap;
    private boolean canQueryAllEvent;

    private List<String> eventInfoList;

    private User currUser;
    private int currUserId = 0;
    private List<Integer> user_eventIdList;
    private boolean canQueryAllEventId;



    public static Schedule_Home_Fragment newInstance() {
        Schedule_Home_Fragment fragment = new Schedule_Home_Fragment();
        return fragment;
    }

    public Schedule_Home_Fragment() {
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
        View view = inflater.inflate(R.layout.schedule_home_fragment, container, false);

        dateChooseButton = (ImageButton) view.findViewById(R.id.event_date_picker);
        dateChooseButton.setOnClickListener(this);

        dateChooseTextView = (TextView) view.findViewById(R.id.selectedDateTextView);

        listAllEventsButton = (Button) view.findViewById(R.id.listAllEventsButton);
        listAllEventsButton.setOnClickListener(this);


        eventsListView = (ListView) view.findViewById(R.id.schedule_list);

        Bundle extra = getActivity().getIntent().getExtras();
        String idStr = extra.getString("UserId");
        if(idStr != null) {
            currUserId = Integer.parseInt(idStr);
            Log.d("^^^^^show uId", currUserId + "");
            getActivity().getIntent().removeExtra("UserId");
        }

        listAllEvents();

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.event_date_picker:
                showDateDialogFragment();
                break;

            case R.id.listAllEventsButton:
                listAllEvents();
                break;
        }
    }

    private void listAllEvents() {
        getAllEvent();
        if(currUserId == 0) {
            getCurrentUser();
        }

        getEventIdsFromSchedule();
        getCurrentUserEvents();

        ArrayAdapter<String> eventsAdapter = new ArrayAdapter<String>(
                getActivity(), R.layout.schedule_event_item, eventInfoList);
        eventsListView.setAdapter(eventsAdapter);
    }

    private void getAllEvent() {
        //get all events from DB
        sb = new StringBuilder();
        DefaultSocketClient_ClientSide d = new DefaultSocketClient_ClientSide(LOCAL_HOST, PORT,
                CommandConstants.QUERY_ALL_EVENTS, 1, sb);
        Thread t1 = new Thread(d);
        t1.start();
        while(t1.isAlive()) {

        }
        Log.d("----Acc qr_allEv info:", sb.toString());

        if(sb.toString().equals(ConfirmMsgConstants.ALL_EVENTS_GET)) {
            Toast.makeText(getActivity(), "Query All Events successfully!",
                    Toast.LENGTH_SHORT).show();

            eventList = d.getAllEvents();
            eventsMap = eventList.getList();
            Log.d("^^^^^^^^e_sz", eventsMap.size() + "");
            canQueryAllEvent = true;
        } else {
            Toast.makeText(getActivity(), "Query Events fail...! No Events Exist",
                    Toast.LENGTH_SHORT).show();
            canQueryAllEvent = false;
        }
    }

    private void getCurrentUser() {
        Bundle extra = getActivity().getIntent().getExtras();
        String email = extra.getString(Login_Activity.EMAIL);

        //Query user info from DB
        sb = new StringBuilder();
        User user = new User();
        user.setEmail(email);

        DefaultSocketClient_ClientSide d = new DefaultSocketClient_ClientSide(LOCAL_HOST, PORT,
                CommandConstants.QUERY_USER_INFO, user, sb);
        Thread t1 = new Thread(d);
        t1.start();
        while(t1.isAlive()) {

        }
        currUser = d.getQueryUser();
        currUserId = currUser.getID();
        Log.d("-----Query user id", currUserId + "");
    }

    private void getEventIdsFromSchedule() {
        //get all events id from scheduleDB
        sb = new StringBuilder();
        DefaultSocketClient_ClientSide d = new DefaultSocketClient_ClientSide(LOCAL_HOST, PORT,
                CommandConstants.QUERY_ALL_EVENTS_ID, currUserId, sb);
        Thread t1 = new Thread(d);
        t1.start();
        while(t1.isAlive()) {

        }
        Log.d("----Acc allEvId info:", sb.toString());

        if(sb.toString().equals(ConfirmMsgConstants.ALL_EVENTS_ID_GET)) {
            Toast.makeText(getActivity(), "Query All Events ID successfully!",
                    Toast.LENGTH_SHORT).show();

            int[] user_eventIdArray = d.getEventIdArray();
            user_eventIdList = new ArrayList<>();
            for(int id: user_eventIdArray) {
                user_eventIdList.add(id);
                Log.d("########e_id:", id + "");
            }
            canQueryAllEventId = true;
        } else {
            Toast.makeText(getActivity(), "Query Events Id fail...! No corresponding Events Exist",
                    Toast.LENGTH_LONG).show();
            canQueryAllEventId = false;
        }

    }

    public void getCurrentUserEvents() {
        eventInfoList = new ArrayList<>();

        if(canQueryAllEvent && canQueryAllEventId) {
            //将每个event 转成 string 的形式 当作ListView 的一个item
                for(Map.Entry<Integer, Event> entry: eventsMap.entrySet()) {
//                    int key = entry.getKey();
                    Event value = entry.getValue();

                    StringBuilder valueSb = new StringBuilder();

                    int e_id = value.getId();
                    if (!user_eventIdList.contains(e_id)) {
                        continue;
                    }

                    int holder_id = value.getHolderId();

                    int status = value.getEventStat();
                    String e_status = null;
                    if (status == 1) {
                        e_status = "Start";
                    } else {
                        e_status = "end";
                    }

                    int population = value.getPopulation();
                    String name = value.getName();
                    String date = value.getDate();
                    String time = value.getTime();

                    String location = value.getLocation();
                    String[] latiLogi = location.split("/");

                    String category = value.getCategory();
                    String description = value.getDesc();
//                    String photo_filename = value.getPhotoFilename();

                    valueSb.append("e_id: ").append(e_id);
                    valueSb.append(", holder_id: ").append(holder_id);
                    valueSb.append(", e_status: ").append(e_status);
                    valueSb.append(", population: ").append(population);

                    valueSb.append("\r\nname: ").append(name);
                    valueSb.append(", category: ").append(category);

                    valueSb.append("\r\ndate: ").append(date);
                    valueSb.append(", time: ").append(time);

                    valueSb.append("\r\nLati: ").append(latiLogi[0]).append(", Longi: ").append(latiLogi[1]);

                    valueSb.append("\r\ndescription: ").append(description);

                    eventInfoList.add(valueSb.toString());
                }
        }
    }



    private void showDateDialogFragment() {
        Schedule_DatePicker_Diaolog_Fragment dialog = new Schedule_DatePicker_Diaolog_Fragment();
        dialog.setTargetFragment(this, DIALOG_FRAGMENT);
        dialog.show(getActivity().getSupportFragmentManager(), "datePickerDialog");
    }

    @Override
    public void onEventDatePickComplete(String selectedDate) {
        dateChooseTextView.setText(selectedDate);
        listEventsOnChooseDay(selectedDate);
    }

    private void listEventsOnChooseDay(String selectedDate) {
        List<String> filterEventInfoList = new ArrayList<>();

        if(canQueryAllEvent && canQueryAllEventId) {
            //将每个event 转成 string 的形式 当作ListView 的一个item
            for(Map.Entry<Integer, Event> entry: eventsMap.entrySet()) {
//                    int key = entry.getKey();
                Event value = entry.getValue();

                StringBuilder valueSb = new StringBuilder();

                String date = value.getDate();
                if(!date.equals(selectedDate)) {
                    continue;
                }

                int e_id = value.getId();
                int holder_id = value.getHolderId();

                int status = value.getEventStat();
                String e_status = null;
                if (status == 1) {
                    e_status = "Start";
                } else {
                    e_status = "end";
                }

                int population = value.getPopulation();
                String name = value.getName();
                String time = value.getTime();

                String location = value.getLocation();
                String[] latiLogi = location.split("/");

                String category = value.getCategory();
                String description = value.getDesc();
//                    String photo_filename = value.getPhotoFilename();

                valueSb.append("e_id: ").append(e_id);
                valueSb.append(", holder_id: ").append(holder_id);
                valueSb.append(", e_status: ").append(e_status);
                valueSb.append(", population: ").append(population);

                valueSb.append("\r\nname: ").append(name);
                valueSb.append(", category: ").append(category);

                valueSb.append("\r\ndate: ").append(date);
                valueSb.append(", time: ").append(time);

                valueSb.append("\r\nLati: ").append(latiLogi[0]).append(", Longi: ").append(latiLogi[1]);

                valueSb.append("\r\ndescription: ").append(description);

                filterEventInfoList.add(valueSb.toString());
            }
        }

        ArrayAdapter<String> eventsAdapter = new ArrayAdapter<String>(
                getActivity(), R.layout.schedule_event_item, filterEventInfoList);
        eventsListView.setAdapter(eventsAdapter);
    }
}
