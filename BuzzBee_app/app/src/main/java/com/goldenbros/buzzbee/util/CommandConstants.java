package com.goldenbros.buzzbee.util;

/**
 * Constants for command that is sent from client to server
 *
 * Created by wang on 7/24/15.
 */
public final class CommandConstants {
    public static final String VERIFY = "Verify";
    public static final String CREATE_NEW_USER = "CreateNewUser";

    public static final String QUERY_USER_INFO = "QueryUserInfo";

    public static final String UPDATE_USER = "UpdateUser";
    public static final String UPDATE_USER_IMAGE = "UpdateUserImage";

    public static final String CREATE_NEW_EVENT = "CreateNewEvent";

    public static final String QUERY_ALL_EVENTS = "QueryAllEvents";

    public static final String QUERY_ALL_EVENTS_ID = "QueryAllEventsId";

    public static final String JOIN_EVENT = "JoinEvent";

    public static final String DISJOIN_EVENT = "DisJoinEvent";

    public static final String QUERY_EVENT_USERS = "QueryEventUsers";

    public static final String QUERY_FRIEND_LIST = "QueryFriendList";

    public static final String CHECK_FRIEND_STATUS = "CheckFriendStatus";

    public static final String ADD_FRIEND = "AddFriend";
}
