package com.goldenbros.buzzbee.driver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.goldenbros.buzzbee.database.EventDb;
import com.goldenbros.buzzbee.database.FriendsDb;
import com.goldenbros.buzzbee.database.ScheduleDb;
import com.goldenbros.buzzbee.database.UserDb;

public class Main {
	
	private static ServerSocket serverSocket = null;
	
	/**
	 * Helper function to accept request from client
	 */
	public static void acceptRequest(UserDb userDB, EventDb eventDB, ScheduleDb scheduleDB, FriendsDb friendsDB) {
		Socket clientSocket = null;
		try {
			clientSocket = serverSocket.accept();
			DefaultSocketClient_ServerSide a1 = 
					new DefaultSocketClient_ServerSide(clientSocket, userDB, eventDB, scheduleDB, friendsDB);
			Thread t1 = new Thread(a1);
			t1.start();
		} catch(IOException e) {
			System.out.println("Accept failed...");
			System.exit(1);
		}
	}
	
	public static void main(String[] args){
		//Create ServerSocket
		try {
			serverSocket = new ServerSocket(4444);
		} catch(IOException e) {
			System.out.println("Could not listen on port: 4444");
			System.exit(1);
		}
		
		//Create User DB
		UserDb userDB = new UserDb();
		userDB.buildConnect();
		userDB.createDB();
//		userDB.clearAll();
//		userDB.dropTable();
		
		//Create Event DB
		EventDb eventDB = new EventDb();
		eventDB.buildConnect();
		eventDB.createDB();
//		eventDB.clearAll();
//		eventDB.dropTable();
		
		//Create Schedule DB
		ScheduleDb scheduleDB = new ScheduleDb();
		scheduleDB.buildConnect();
		scheduleDB.createDB();
//		scheduleDB.clearAll();
//		scheduleDB.dropTable();
		
		//create Friends DB
		FriendsDb friendsDB = new FriendsDb();
		friendsDB.buildConnect();
		friendsDB.createDB();
//		friendsDB.deleteFriends(1,2);
//		friendsDB.clearAll();
		
		//Waiting for connection.
		System.out.println("Server starts...");
		while(true) {
			acceptRequest(userDB, eventDB, scheduleDB, friendsDB);
		}
	
	}

}