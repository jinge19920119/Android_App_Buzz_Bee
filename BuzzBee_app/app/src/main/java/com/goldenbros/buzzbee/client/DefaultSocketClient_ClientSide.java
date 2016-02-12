package com.goldenbros.buzzbee.client;

import android.util.Log;

import com.goldenbros.buzzbee.model.Event;
import com.goldenbros.buzzbee.model.EventList;
import com.goldenbros.buzzbee.model.User;
import com.goldenbros.buzzbee.model.UserList;
import com.goldenbros.buzzbee.util.CommandConstants;
import com.goldenbros.buzzbee.util.ConfirmMsgConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class DefaultSocketClient_ClientSide extends Thread {
    private String strHost;
    private int iPort;
    private String command;
    private User user;
    private StringBuilder sb;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private User queryUser;
    private Event event;
    private UserList userList;
    private int flag;
    private EventList allEvents = null;
    private int currUserId;
    private int[] eventIdArray = null;

    private int userID, eventID;

    public DefaultSocketClient_ClientSide(String strHost, int iPort, String command, User user, StringBuilder sb) {
        this.strHost = strHost;
        this.iPort = iPort;
        this.command = command;
        this.user = user;
        this.sb = sb;
    }

    public DefaultSocketClient_ClientSide(String strHost, int iPort, String command, Event event, StringBuilder sb) {
        this.strHost = strHost;
        this.iPort = iPort;
        this.command = command;
        this.event = event;
        this.sb = sb;
    }

    public DefaultSocketClient_ClientSide(String strHost, int iPort, String command, int flag, StringBuilder sb) {
        this.strHost = strHost;
        this.iPort = iPort;
        this.command = command;
        this.flag = flag;
        this.sb = sb;
    }

    public DefaultSocketClient_ClientSide(String strHost, int iPort, String command, StringBuilder sb,
                                          int userID, int eventID) {
        this.strHost = strHost;
        this.iPort = iPort;
        this.command = command;
        this.sb = sb;
        this.userID = userID;
        this.eventID = eventID;
    }
    public DefaultSocketClient_ClientSide(String strHost, int iPort, String command, StringBuilder sb,
                                          int userID, int eventID, UserList list) {
        this.strHost = strHost;
        this.iPort = iPort;
        this.command = command;
        this.sb = sb;
        this.userID = userID;
        this.eventID = eventID;
        this.userList = list;
    }

    public void run() {
        if (openConnection()) {
            handleSession();
        }
        Log.i("++++++Client Conenct", "interrupt");
    }

    public boolean openConnection() {
        try {
            socket = new Socket(strHost, iPort);
        } catch (IOException e) {
            System.err.println("Unable to connect to " + strHost);
            return false;
        }

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));
        } catch (Exception e) {
            System.err.println("Unable to obtain stream to/from " + strHost);
            return false;
        }

        return true;

    }

    public void handleSession() {
        try {
            handleCommand(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleCommand(String command) throws IOException {
        switch(command) {
            //Verify if email and password is valid when Login
            case CommandConstants.VERIFY:
                sendOutput(CommandConstants.VERIFY);
                serializeUser(user);
                String confirmInfo = "";
                try {
                    confirmInfo = in.readLine();
                } catch(IOException e) {
                    e.printStackTrace();
                }
                if(confirmInfo.equals(ConfirmMsgConstants.VALID_USER)) {
                    sb.append(ConfirmMsgConstants.VALID_USER);
                } else {
                    sb.append(ConfirmMsgConstants.ILLEGAL_USER);
                }
                break;

            //Create new user in DB when Signup
            case CommandConstants.CREATE_NEW_USER:
                sendOutput(CommandConstants.CREATE_NEW_USER);
                serializeUser(user);
                String confirmInfo2 = "";
                try {
                    confirmInfo2 = in.readLine();
                } catch(IOException e) {
                    e.printStackTrace();
                }
                if(confirmInfo2.equals(ConfirmMsgConstants.NEW_USER_ADDED)) {
                    sb.append(ConfirmMsgConstants.NEW_USER_ADDED);
                } else {
                    sb.append(ConfirmMsgConstants.USER_ADDED_FAIL);
                }
                break;

            //Get user information
            case CommandConstants.QUERY_USER_INFO:
                sendOutput(CommandConstants.QUERY_USER_INFO);
                serializeUser(user);
                queryUser = desiralizeUser();

                break;

            case CommandConstants.UPDATE_USER:
                sendOutput(CommandConstants.UPDATE_USER);
                serializeUser(user);
                String confirmInfo3 = "";
                try {
                    confirmInfo3 = in.readLine();
                } catch(IOException e) {
                    e.printStackTrace();
                }
                if(confirmInfo3.equals(ConfirmMsgConstants.USER_UPDATED)) {
                    sb.append(ConfirmMsgConstants.USER_UPDATED);
                }
                break;


            case CommandConstants.UPDATE_USER_IMAGE:
                sendOutput(CommandConstants.UPDATE_USER_IMAGE);
                serializeUser(user);
                String confirmInfo4 = "";
                try {
                    confirmInfo4 = in.readLine();
                } catch(IOException e) {
                    e.printStackTrace();
                }
                if(confirmInfo4.equals(ConfirmMsgConstants.USER_IMAGE_UPDATED)) {
                    sb.append(ConfirmMsgConstants.USER_IMAGE_UPDATED);
                }
                break;


            //following are for event

            case CommandConstants.CREATE_NEW_EVENT:
                sendOutput(CommandConstants.CREATE_NEW_EVENT);
                serializeEvent(event);
                String confirmEventInfo = "";
                try {
                    confirmEventInfo = in.readLine();
                } catch(IOException e) {
                    e.printStackTrace();
                }
                if(confirmEventInfo.equals(ConfirmMsgConstants.NEW_EVENT_ADDED)) {
                    sb.append(ConfirmMsgConstants.NEW_EVENT_ADDED);
                } else {
                    sb.append(ConfirmMsgConstants.EVENT_ADDED_FAIL);
                }
                break;

            case CommandConstants.QUERY_ALL_EVENTS:
                sendOutput(CommandConstants.QUERY_ALL_EVENTS);

                String confirmEventListInfo = "";
                try {
                    confirmEventListInfo = in.readLine();
                } catch(IOException e) {
                    e.printStackTrace();
                }
                if(confirmEventListInfo.equals(ConfirmMsgConstants.ALL_EVENTS_GET)) {
                    allEvents = deserializeEventList();
                    sb.append(ConfirmMsgConstants.ALL_EVENTS_GET);
                } else {
                    sb.append(ConfirmMsgConstants.NO_EVENT_GET);
                }
                break;

            case CommandConstants.QUERY_ALL_EVENTS_ID:
                currUserId = flag;
                sendOutput(CommandConstants.QUERY_ALL_EVENTS_ID);
                serializeUserId(currUserId);

                String confirmEventId = "";
                try {
                    confirmEventId = in.readLine();
                } catch(IOException e) {
                    e.printStackTrace();
                }
                if(confirmEventId.equals(ConfirmMsgConstants.ALL_EVENTS_ID_GET)) {
                    eventIdArray = deserializeEventIdArray();
                    sb.append(ConfirmMsgConstants.ALL_EVENTS_ID_GET);
                } else {
                    sb.append(ConfirmMsgConstants.NO_EVENT_ID_GET);
                }
                break;

            case CommandConstants.JOIN_EVENT:
                sendOutput(CommandConstants.JOIN_EVENT);
                serializeID(userID);
                serializeID(eventID);

                String confirmMsg = "";
                try {
                    confirmMsg = in.readLine();
                    if(confirmMsg.equals(ConfirmMsgConstants.JOIN_EVENT_SUCCESS))
                        sb.append(ConfirmMsgConstants.JOIN_EVENT_SUCCESS);
                    else
                        sb.append("Join Event Failure");
                } catch(IOException e) {
                    e.printStackTrace();
                }
                break;


            case CommandConstants.DISJOIN_EVENT:
                sendOutput(CommandConstants.DISJOIN_EVENT);
                serializeID(userID);
                serializeID(eventID);

                String confirmMsg1 = "";
                try {
                    confirmMsg1 = in.readLine();
                    if(confirmMsg1.equals(ConfirmMsgConstants.DISJOIN_EVENT_SUCCESS))
                        sb.append(ConfirmMsgConstants.DISJOIN_EVENT_SUCCESS);
                    else
                        sb.append("DisJoin Event Failure");
                } catch(IOException e) {
                    e.printStackTrace();
                }
                break;

            case CommandConstants.QUERY_EVENT_USERS:
                sendOutput(CommandConstants.QUERY_EVENT_USERS);
                serializeID(userID);
                serializeID(eventID);

                try {
                    String confirmMsg3 = in.readLine();
                    if(confirmMsg3.equals(ConfirmMsgConstants.QUERY_EVENT_USERS_SUCCESS)) {
                        sb.append(ConfirmMsgConstants.QUERY_EVENT_USERS_SUCCESS);
                        userList = deserializeUserList();
                    }
                    else
                        sb.append("Query Event Participants Failure");
                } catch(IOException e) {
                    e.printStackTrace();
                }
                break;

            case CommandConstants.QUERY_FRIEND_LIST:
                sendOutput(CommandConstants.QUERY_FRIEND_LIST);
                serializeID(userID);

                try {
                    String confirmMsg3 = in.readLine();
                    if(confirmMsg3.equals(ConfirmMsgConstants.QUERY_FRIEND_LIST_SUCCESS )) {
                        sb.append(ConfirmMsgConstants.QUERY_FRIEND_LIST_SUCCESS );
                        userList = deserializeUserList();
                    }
                    else
                        sb.append("Query Friends List Failure");
                } catch(IOException e) {
                    e.printStackTrace();
                }

                break;

            case CommandConstants.CHECK_FRIEND_STATUS:
                sendOutput(CommandConstants.CHECK_FRIEND_STATUS);
                serializeID(userID);
                serializeID(eventID);

                try {
                    String confirmMsg3 = in.readLine();
                    if(confirmMsg3.equals(ConfirmMsgConstants.IS_FRIEND)) {
                        sb.append(ConfirmMsgConstants.IS_FRIEND);
                    }else if(confirmMsg3.equals(ConfirmMsgConstants.NOT_FRIEND)) {
                        sb.append(ConfirmMsgConstants.NOT_FRIEND);
                    }
                    else
                        sb.append("Query Friend Status Failure");
                } catch(IOException e) {
                    e.printStackTrace();
                }
                break;

            case CommandConstants.ADD_FRIEND:
                sendOutput(CommandConstants.ADD_FRIEND);
                serializeID(userID);
                serializeID(eventID);

                try {
                    String confirmMsg3 = in.readLine();
                    if(confirmMsg3.equals(ConfirmMsgConstants.ADD_FRIEND_SUCCESS)) {
                        sb.append(ConfirmMsgConstants.ADD_FRIEND_SUCCESS);
                    }
                    else
                        sb.append("Add Friend Failure");
                } catch(IOException e) {
                    e.printStackTrace();
                }
                break;

        }
    }

    public void sendOutput(String strOutput) throws IOException {
        out.println(strOutput);
    }

    public void serializeUser(User user) {
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(user);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void serializeID(int id) {
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(id);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User desiralizeUser() {
        User queryUser = null;
        try {
            ois = new ObjectInputStream(
                    socket.getInputStream());
            queryUser = (User) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return queryUser;
    }

    public User getQueryUser() {
        return queryUser;
    }

    public void serializeEvent(Event event) {
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(event);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Event desiralizeEvent() {
        Event queryEvent = null;
        try {
            ois = new ObjectInputStream(
                    socket.getInputStream());
            queryEvent = (Event) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return queryEvent;
    }

    public EventList deserializeEventList() {
        EventList eventList = null;
        try {
            ois = new ObjectInputStream(
                    socket.getInputStream());
            eventList = (EventList) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return eventList;
    }

    public UserList deserializeUserList() {
        UserList userList = null;
        try {
            ois = new ObjectInputStream(
                    socket.getInputStream());
            userList = (UserList) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return userList;
    }

    public EventList getAllEvents() {
        return allEvents;
    }


    public void serializeUserId(int currUserId) {
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(currUserId);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int[] deserializeEventIdArray() {
        int[] eventIdArray = null;
        try {
            ois = new ObjectInputStream(
                    socket.getInputStream());
            eventIdArray = (int[]) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return eventIdArray;
    }

    public int[] getEventIdArray() {
        return eventIdArray;
    }

    public UserList getUserList(){
        return userList;
    }


    public void closeSession() {
        try {
            out = null;
            in = null;
            socket.close();
            oos = null;
//            ois = null;
        } catch(IOException e) {
            System.out.println("Error closing socket to " + strHost);
        }
    }

}
