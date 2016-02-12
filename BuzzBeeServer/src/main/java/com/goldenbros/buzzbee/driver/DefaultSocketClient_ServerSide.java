package com.goldenbros.buzzbee.driver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import com.goldenbros.buzzbee.database.EventDb;
import com.goldenbros.buzzbee.database.FriendsDb;
import com.goldenbros.buzzbee.database.ScheduleDb;
import com.goldenbros.buzzbee.database.UserDb;
import com.goldenbros.buzzbee.model.Event;
import com.goldenbros.buzzbee.model.EventList;
import com.goldenbros.buzzbee.model.User;
import com.goldenbros.buzzbee.model.UserList;
import com.goldenbros.buzzbee.util.CommandConstants;
import com.goldenbros.buzzbee.util.ConfirmMsgConstants;

public class DefaultSocketClient_ServerSide extends Thread {

	private PrintWriter out;
	private BufferedReader in;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Socket clientSocket;
	
	private UserDb userDB;
	private EventDb eventDB;
	private ScheduleDb scheduleDB;
	private FriendsDb friendsDB;

	public DefaultSocketClient_ServerSide(Socket clientSocket, UserDb userDB, EventDb eventDB,
			ScheduleDb scheduleDB, FriendsDb friendsDB) {
		this.clientSocket = clientSocket;
		this.userDB = userDB;
		this.eventDB = eventDB;
		this.scheduleDB = scheduleDB;
		this.friendsDB = friendsDB;
	}

	public void run() {
		if (openConnection()) {
			handleSession();
		}
	}

	/**
	 * Create connection with Client
	 */
	public boolean openConnection() {
		try {
			out = new PrintWriter(clientSocket.getOutputStream(),
					true);
			in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to obtain object stream...");
			return false;
		}
		return true;
	}

	/**
	 * Deal with communication with Client
	 */
	public void handleSession() {
		String strInput = "";
		try {
			while (true) {
				strInput = in.readLine();
				if (strInput == null) {
					continue;
				}
				handleInput(strInput);
			}
		} catch (IOException e) {
			System.out.println("Handling session error...");
			e.printStackTrace();
		}
	}

	public void handleInput(String strInput) throws IOException {
		switch (strInput) {
			case CommandConstants.VERIFY:
				System.out.println("Handle: " + strInput);
		
				User oldUser = deserializeUser();
				boolean flag = userDB.verifyUser(oldUser.getEmail(),
						oldUser.getPassword());
				if (flag == true) {
					System.out.println("////Congratulations, Email: "
							+ oldUser.getEmail()
							+ ", Passwd: "
							+ oldUser.getPassword()
							+ ", is valid!");
					sendOutput(ConfirmMsgConstants.VALID_USER);
				} else {
					System.out.println("////Bad, Email: "
							+ oldUser.getEmail()
							+ ", Passwd: "
							+ oldUser.getPassword()
							+ ", is invalid!");
					sendOutput(ConfirmMsgConstants.ILLEGAL_USER);
				}
				break;
		
			case CommandConstants.CREATE_NEW_USER:
				System.out.println("Handle: " + strInput);
		
				User newUser = deserializeUser();
				boolean res = userDB.createNewUser(newUser.getEmail(),
						newUser.getPassword());
				if (res == true) {
					System.out.println("-----Great, new user added! Email: "
							+ newUser.getEmail()
							+ ", Passwd: "
							+ newUser.getPassword());
					sendOutput(ConfirmMsgConstants.NEW_USER_ADDED);
				} else {
					System.out.println("-----Shame, user already exist! Email: "
							+ newUser.getEmail()
							+ ", Passwd: "
							+ newUser.getPassword());
					sendOutput(ConfirmMsgConstants.USER_ADDED_FAIL);
				}
				break;
			
			case CommandConstants.QUERY_USER_INFO:
				System.out.println("Handle: " + strInput);
				
				User user = deserializeUser();
				String email = user.getEmail();
				User queryUser = userDB.getUser(email);
				System.out.println("Query User successfully. id: " + queryUser.getID()
						+ ", email: " + queryUser.getEmail());
				serializeUser(queryUser);
				
				break;
				
			case CommandConstants.UPDATE_USER:
				System.out.println("Handle: " + strInput);
				User updateUser = deserializeUser();
				userDB.updateUser(updateUser);
				sendOutput(ConfirmMsgConstants.USER_UPDATED);
				System.out.println("Update User successfully. id: " + updateUser.getID()
						+ ", email: " + updateUser.getEmail());
				break;
				
			case CommandConstants.UPDATE_USER_IMAGE:
				System.out.println("Handle: " + strInput);
				User updImgUser = deserializeUser();
				userDB.updateUserImage(updImgUser);
				sendOutput(ConfirmMsgConstants.USER_IMAGE_UPDATED);
				System.out.println("Update UserImg successfully. id: " + updImgUser.getID()
						+ ", email: " + updImgUser.getEmail()
						+ ", photo: " + updImgUser.getPhotoName());
				break;
				
				
			//following are for event
			case CommandConstants.CREATE_NEW_EVENT:
				System.out.println("Handle: " + strInput);
				Event newEvent = deserializeEvent();
				eventDB.createEvent(newEvent);
				
				//update schedulDB
				int event_id = eventDB.getOneEventId(newEvent);
				int holder_id = newEvent.getHolderId();
				scheduleDB.addSchedule(holder_id, event_id, 1);
				
				sendOutput(ConfirmMsgConstants.NEW_EVENT_ADDED);
				System.out.println("Create New Event successfully. name: " + newEvent.getName()
						+ ", holder_id: " + newEvent.getHolderId());
				
				break;
				
			case CommandConstants.QUERY_ALL_EVENTS:
				System.out.println("Handle: " + strInput);
				EventList allEvents = eventDB.getAllEvent();
				
				if(allEvents == null) {
					System.out.println("++Warning! There is no events in eventsDB");
					sendOutput(ConfirmMsgConstants.NO_EVENT_GET);
				} else {
					System.out.println("++Congratulations! Get All Events +++!!!");
					sendOutput(ConfirmMsgConstants.ALL_EVENTS_GET);
					serializeEventList(allEvents);
				}
				
				break;
				
			case CommandConstants.QUERY_ALL_EVENTS_ID:
				System.out.println("Handle: " + strInput);
				int currUserId = deserializeUserId();
				int[] eventIdArray = scheduleDB.getEventsByUser(currUserId);
				if(eventIdArray == null) {
					System.out.println("++Warning! There is no events in scheduleDB");
					sendOutput(ConfirmMsgConstants.NO_EVENT_ID_GET);
				} else {
					System.out.println("++Congratulations! Get All Events ID +++!!!");
					sendOutput(ConfirmMsgConstants.ALL_EVENTS_ID_GET);
					serializeEventIdArray(eventIdArray);
				}
				
				break;
				
			case CommandConstants.JOIN_EVENT:
				System.out.println("Handle: " + strInput);
				int u_id = deserializeID();
				int e_id = deserializeID();
				scheduleDB.addSchedule(u_id, e_id, 2);
				
				sendOutput(ConfirmMsgConstants.JOIN_EVENT_SUCCESS);
				System.out.println("Add new schedule successfully. e_id: " + e_id
						+ ", u_id: " + u_id);
				break;
			
			case CommandConstants.DISJOIN_EVENT:
				System.out.println("Handle: " + strInput);
				int uu_id = deserializeID();
				int ee_id = deserializeID();
				scheduleDB.deleteSchedule(uu_id, ee_id);
				
				sendOutput(ConfirmMsgConstants.DISJOIN_EVENT_SUCCESS);
				System.out.println("Delete schedule successfully. event_id: " + ee_id
						+ ", user_id: " + uu_id);
				break;
				
			case CommandConstants.QUERY_EVENT_USERS:
				System.out.println("Handle: " + strInput);
				int uid = deserializeID();
				int eid = deserializeID();
				int[] allUserIds = scheduleDB.getUsersByEvents(eid);
				UserList eventUserList = new UserList();
				for(int i = 0; i < allUserIds.length; i++) {
					if(allUserIds[i] == uid) {
						continue;
					}
					User u = userDB.getUser(allUserIds[i]);
					eventUserList.addUser(u);
				}
				sendOutput(ConfirmMsgConstants.QUERY_EVENT_USERS_SUCCESS);
				serializeEventUserList(eventUserList);
				System.out.println("Get All Users in same event successfully. event_id: " + eid
						+ ", size: " + eventUserList.getUserCount());
				
				break;
				
			case CommandConstants.CHECK_FRIEND_STATUS:
				System.out.println("Handle: " + strInput);
				int u1 = deserializeID();
				int u2 = deserializeID();
				boolean isFriends = friendsDB.checkFriends(u1, u2);
				if(isFriends) {
					sendOutput(ConfirmMsgConstants.IS_FRIEND);
					System.out.println("Great, user1: " + u1 + " and user2: " + u2 + " are friends");
				} else {
					sendOutput(ConfirmMsgConstants.NOT_FRIEND);
					System.out.println("Bad, user1: " + u1 + " and user2: " + u2 + " are not friends");
				}
				break;
			
			case CommandConstants.ADD_FRIEND:
				System.out.println("Handle: " + strInput);
				int f1 = deserializeID();
				int f2 = deserializeID();
				boolean alreadyFriends = friendsDB.addFriends(f1, f2);
				if(!alreadyFriends) {
					sendOutput(ConfirmMsgConstants.ADD_FRIEND_SUCCESS);
					System.out.println("Congratulations, user1: " + f1 + " and user2: " + f2 + " are now friends");
				} else {
					sendOutput(ConfirmMsgConstants.ADD_FRIEND_FAIL);
					System.out.println("Bad, user1: " + f1 + " and user2: " + f2 + " are already friends");
				}
				break;
			
			case CommandConstants.QUERY_FRIEND_LIST:
				System.out.println("Handle: " + strInput);
				int queryId = deserializeID();
				int[] allFriendsId = friendsDB.findFriends(queryId);
				UserList friendsList = new UserList();
				for(int i = 0; i < allFriendsId.length; i++) {
					User u = userDB.getUser(allFriendsId[i]);
					friendsList.addUser(u);
				}
				
				sendOutput(ConfirmMsgConstants.QUERY_FRIEND_LIST_SUCCESS);
				serializeFriendsList(friendsList);
				break;
		}
	}

	/**
	 * Helper function for output
	 * 
	 * @param strOutput
	 * @throws IOException
	 */
	public void sendOutput(String strOutput) throws IOException {
		out.println(strOutput);
	}

	/**
	 * Close communication with client
	 */
	public void closeSession() {
		try {
			out = null;
			in = null;
			clientSocket.close();
		} catch (IOException e) {
			System.out.println("Error when closing socket...");
		}
	}

	public User deserializeUser() {
		User user = null;
		try {
			ois = new ObjectInputStream(
					clientSocket.getInputStream());
			user = (User) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return user;
	}
	
	public void serializeUser(User queryUser) {
		try {
			oos = new ObjectOutputStream(
					clientSocket.getOutputStream());
			oos.writeObject(queryUser);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public Event deserializeEvent() {
		Event event = null;
		try {
			ois = new ObjectInputStream(
					clientSocket.getInputStream());
			event = (Event) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return event;
	}
	
	public void serializeEvent(Event queryEvent) {
		try {
			oos = new ObjectOutputStream(
					clientSocket.getOutputStream());
			oos.writeObject(queryEvent);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void serializeEventList(EventList allEvents) {
		try {
			oos = new ObjectOutputStream(
					clientSocket.getOutputStream());
			oos.writeObject(allEvents);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public int deserializeUserId() {
		int currUserId = -1;
		try {
			ois = new ObjectInputStream(
					clientSocket.getInputStream());
			currUserId = (Integer) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return currUserId;
	}
	
	public void serializeEventIdArray(int[] eventIdArray) {
		try {
			oos = new ObjectOutputStream(
					clientSocket.getOutputStream());
			oos.writeObject(eventIdArray);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public int deserializeID() {
		int id = -1;
		try {
			ois = new ObjectInputStream(
					clientSocket.getInputStream());
			id = (Integer) ois.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return id;
	}
	
	public void serializeEventUserList(UserList eventUserLists) {
		try {
			oos = new ObjectOutputStream(
					clientSocket.getOutputStream());
			oos.writeObject(eventUserLists);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void serializeFriendsList(UserList friendsList) {
		try {
			oos = new ObjectOutputStream(
					clientSocket.getOutputStream());
			oos.writeObject(friendsList);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
}
