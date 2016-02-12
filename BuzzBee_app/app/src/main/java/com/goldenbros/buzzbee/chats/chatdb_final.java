package com.goldenbros.buzzbee.chats;

import java.security.PublicKey;

/**
 * Created by jinge on 7/29/15.
 */
public interface chatdb_final {
    public final static String DATABASE_NAME = "BuzzBee";
    public final static String TABLE_MESSAGE = "message";
    public final static String TABLE_CHATS = "chats";

    public final static String MESSAGE_SEND_ID = "send_id";
    public final static String MESSAGE_RECEIVER_ID = "reci_id";
    public final static String MESSAGE_CONTENT = "message";
    public final static String MESSAGE_EVENT_ID = "event_id";

    public final static String CHATS_RECEIVER_ID = "reci_id";
    public final static String CHATS_EVENT_ID = "event_id";
    public final static String CHATS_NAME =  "name";
}