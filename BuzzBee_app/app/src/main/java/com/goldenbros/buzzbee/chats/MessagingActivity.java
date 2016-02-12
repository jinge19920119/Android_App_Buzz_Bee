package com.goldenbros.buzzbee.chats;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.goldenbros.buzzbee.R;
import com.goldenbros.buzzbee.service.MessageService;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.messaging.Message;
import com.sinch.android.rtc.messaging.MessageClient;
import com.sinch.android.rtc.messaging.MessageClientListener;
import com.sinch.android.rtc.messaging.MessageDeliveryInfo;
import com.sinch.android.rtc.messaging.MessageFailureInfo;
import com.sinch.android.rtc.messaging.WritableMessage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MessagingActivity  extends Activity implements chatdb_final {

    private String recipient_Id;
    private ArrayList<String> groupUsersId;
    private EditText messageBodyField;
    private String messageBody;
    private MessageService.MessageServiceInterface messageService;
    private String currentUserId;
    private String chatting_id;
    private MessageAdapter messageAdapter;
    private ListView messagesList;
    private ServiceConnection serviceConnection = new MyServiceConnection();
    private MyMessageClientListener messageClientListener = new MyMessageClientListener();
    private ProgressDialog progressDialog;
    private String event_id;
    private String[] group;
    private String[] groupName;

    private Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        bindService(new Intent(this, MessageService.class), serviceConnection, BIND_AUTO_CREATE);

        //get recipientId from the intent
        Intent intent = getIntent();
        recipient_Id = intent.getStringExtra("RECIPIENT_ID");//接收INTENT
        currentUserId = intent.getStringExtra("CURRENT_ID");
        group= intent.getStringArrayExtra("GROUP_ID");//先的到EVENT_Id再得到GROUP(去掉CURRENT_ID)
        event_id = intent.getStringExtra("EVENT_ID");
        groupName = intent.getStringArrayExtra("GROUP_NAME_ID");


        if(recipient_Id==null){
            groupUsersId = new ArrayList<String>(Arrays.asList(group));

        }
        messageBodyField = (EditText) findViewById(R.id.messageBodyField);

        /*
        set listView
         */
        messagesList = (ListView) findViewById(R.id.listMessages);
        messageAdapter = new MessageAdapter(this);
        messagesList.setAdapter(messageAdapter);
        populateMessageHistory();


        //listen for a click on the send button
        findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send the message!
                sendMessage();
            }
        });
    }

    private void sendMessage(){
        messageBody = messageBodyField.getText().toString();
        if (messageBody.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter a message", Toast.LENGTH_LONG).show();
            return;
        }
        if(recipient_Id==null){
            messageService.sendMessage(groupUsersId, messageBody);
        } else  {
            messageService.sendMessage(recipient_Id, messageBody);
        }

        messageBodyField.setText("");
    }

    private void populateMessageHistory(){
        DatabaseLocalConnector dc= new DatabaseLocalConnector(MessagingActivity.this);
        try {
            dc.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(recipient_Id!=null){
            Cursor cursor= dc.getOne(currentUserId, recipient_Id);
            cursor.moveToFirst();

            while (! cursor.isAfterLast()){
                WritableMessage message= new WritableMessage(cursor.getString(cursor.getColumnIndex(MESSAGE_RECEIVER_ID)),
                        cursor.getString(cursor.getColumnIndex(MESSAGE_CONTENT)));
                if(cursor.getString(cursor.getColumnIndex(MESSAGE_SEND_ID)).equals(currentUserId)){
                    messageAdapter.addMessage(message, MessageAdapter.DIRECTION_OUTGOING);
                } else {
                    messageAdapter.addMessage(message, MessageAdapter.DIRECTION_INCOMING);
                }
                cursor.moveToNext();
            }
            cursor.close();

        } else {
            Cursor cursor= dc.getOne(event_id);
            cursor.moveToFirst();
            while(! cursor.isAfterLast()){
                WritableMessage message = new WritableMessage(groupUsersId, cursor.getString(cursor.getColumnIndex(MESSAGE_CONTENT)));
                if(cursor.getString(cursor.getColumnIndex(MESSAGE_SEND_ID)).equals(currentUserId)){
                    messageAdapter.addMessage(message, MessageAdapter.DIRECTION_OUTGOING);
                } else {
                    messageAdapter.addMessage(message, MessageAdapter.DIRECTION_INCOMING);
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        dc.close();
    }

    //unbind the service when the activity is destroyed
    @Override
    public void onDestroy() {
        messageService.removeMessageClientListener(messageClientListener);
        unbindService(serviceConnection);
        super.onDestroy();
    }
    private class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            messageService = (MessageService.MessageServiceInterface) iBinder;
            messageService.addMessageClientListener(messageClientListener);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            messageService = null;
        }
    }

    private class MyMessageClientListener implements MessageClientListener {
        //Notify the user if their message failed to send
        @Override
        public void onMessageFailed(MessageClient client, Message message,
                                    MessageFailureInfo failureInfo) {
            Toast.makeText(MessagingActivity.this, "Message failed to send.", Toast.LENGTH_LONG).show();
        }
        @Override
        public void onIncomingMessage(MessageClient client, Message message) {
            //Display an incoming message

            if (!message.getSenderId().equals(currentUserId)) {
                String senderName= new String ();
                if(recipient_Id==null){
                    int num = 0;
                    for(int i=0;i<group.length;i++){
                        if(group[i].equals(message.getSenderId()))
                            num= i;
                    }
                    senderName = groupName[num];
                } else {
                    senderName= "";
                }

                Calendar calendar= Calendar.getInstance();
                String message_add = senderName+" ("+calendar.get(Calendar.YEAR)+"/"+calendar.get(Calendar.MONTH)
                        +"/"+calendar.get(Calendar.DAY_OF_MONTH)
                        +" "+calendar.get(Calendar.HOUR_OF_DAY)+":"
                        +calendar.get(Calendar.MINUTE)+")";
                String message_final= message_add+" \n "+message.getTextBody();
                WritableMessage writableMessage;
                if(recipient_Id==null){
                    writableMessage = new WritableMessage(message.getRecipientIds()
                            , message_final);
                    saveToDatabaseGroup(message, message_final);
                } else {
                    writableMessage = new WritableMessage(message.getRecipientIds().get(0)
                            , message_final);
                    saveToDatabase(message, message_final);
                }
                messageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_INCOMING);
            }
        }
        @Override
        public void onMessageSent(MessageClient client, Message message, String recipientId) {
            //Display the message that was just sent
            //Later, I'll show you how to store the
            //message in Parse, so you can retrieve and
            //display them every time the conversation is opened
            Calendar calendar= Calendar.getInstance();
            String message_add = " ("+calendar.get(Calendar.YEAR)+"/"+calendar.get(Calendar.MONTH)
                    +"/"+calendar.get(Calendar.DAY_OF_MONTH)
                    +" "+calendar.get(Calendar.HOUR_OF_DAY)+":"
                    +calendar.get(Calendar.MINUTE)+")";
            String message_final= message_add+" \n "+message.getTextBody();
            WritableMessage writableMessage;

            if(recipient_Id==null){
                writableMessage = new WritableMessage(message.getRecipientIds()
                        , message_final);

                saveToDatabaseGroup(message, message_final);
            } else {
                writableMessage = new WritableMessage(message.getRecipientIds().get(0)
                        , message_final);
                saveToDatabase(message, message_final);
            }

            messageAdapter.addMessage(writableMessage, MessageAdapter.DIRECTION_OUTGOING);

        }
        //Do you want to notify your user when the message is delivered?
        @Override
        public void onMessageDelivered(MessageClient client, MessageDeliveryInfo deliveryInfo) {}
        //Don't worry about this right now
        @Override
        public void onShouldSendPushData(MessageClient client, Message message, List<PushPair> pushPairs) {}

        private void saveToDatabase(Message message, String message_final){
            String sender_id = message.getSenderId();
            String receiver_id = recipient_Id;
            String message_content= message_final;
            DatabaseLocalConnector dc= new DatabaseLocalConnector(MessagingActivity.this);
            dc.insertPer(sender_id, receiver_id, message_content);

        }

        private void saveToDatabaseGroup(Message message, String message_final){

            String sender_id= message.getSenderId();
            String message_content= message_final;
            DatabaseLocalConnector dc= new DatabaseLocalConnector(MessagingActivity.this);
            dc.insertGroup(sender_id, event_id, message_content);
        }

    }


}
