package com.goldenbros.buzzbee.util;

public interface Variable_final {
	public final static String DATABASE_NAME = "BuzzBee";

	public final static String TABLE_USER = "users";
	public final static String TABLE_FRIEND = "friends";
	public final static String TABLE_SCHEDULE = "schedules";
	public final static String TABLE_EVENT = "events";
	public final static String TABLE_CHATS = "chatting";

	public final static String USER_NAME = "name";
	public final static String USER_EMAIL = "email";
	public final static String USER_BIRTHDAY = "birthday";
	public final static String USER_AGE = "age";
	public final static String USER_SEX = "sex";
	public final static String USER_INTEREST = "interest";
	public final static String USER_CITY = "city";
	public final static String USER_SIGNATURE = "signature";

	public final static String FRIENDS_USER1_ID = "user1_id";// fk
	public final static String FRIENDS_USER2_ID = "user2_id";

	public final static String SCHEDULE_USER_ID = "user_id";
	public final static String SCHEDULE_EVENT_ID = "event_id";// fk
	public final static String SCHEDULE_USER_STATUS = "user_status";

	public final static String EVENT_EVENT_STATUS = "event_status";
	public final static String EVENT_TIME = "time";
	public final static String EVENT_LOCATION = "location";
	public final static String EVENT_TITLE = "title";
	public final static String EVENT_DESCRIPTION = "description";
	public final static String EVENT_TYPE = "type";

	public final static String CHATS_SEND_ID = "send_id";
	public final static String CHATS_CHATTING_ID = "chatting_id";
	public final static String CHATS_MESSAGE = "message";
	public final static String CHATS_TIME = "time";

	public final static int USER_STATUS_HOLD = 1;
	public final static int USER_STATUS_PAR = 2;

	public final static int EVENT_STATUS_STARTED = 1;
	public final static int EVENT_STATUS_FINISHED = 2;

}
