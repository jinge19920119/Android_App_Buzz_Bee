package com.goldenbros.buzzbee.database;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class ScheduleDb {
	Connection connection = null;
	Statement statement = null;
	ResultSet resultSet = null;

	static final String jdbcDriver = "com.mysql.jdbc.Driver";
	static final String dbUrl = "jdbc:mysql://localhost";
	static final String user = "root";
	static final String passwd = "";

	public ScheduleDb() {

	}

	public void buildConnect() {
		try {
			Class.forName(jdbcDriver);
			connection = DriverManager.getConnection(dbUrl, user,
					passwd);
			statement = connection.createStatement();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void createDB() {
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"scheduledb_schema.txt"));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				if (sb.length() > 0
						&& sb.charAt(sb.length() - 1) == ';') {
					statement.execute(sb.toString());
					sb = new StringBuilder();
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean addSchedule(int user_id, int event_id, int user_status) {
		// return true if added successfully, return false if it is
		// existed
		boolean canAdded = false;
		try {
			String sql = "select * from schedules where `user_id` ="
					+ user_id
					+ " and `event_id` ="
					+ event_id + ";";
			ResultSet rs = statement.executeQuery(sql);
			if (!rs.next()) {
				canAdded = true;
				sql = "insert into schedules (`user_id` , `event_id` , `user_status`) values ("
						+ user_id
						+ " , "
						+ event_id
						+ " , " + user_status + ");";
				System.out.println("status: " + user_status);
				statement.executeUpdate(sql);
			} else {
				canAdded = true;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return canAdded;
	}

	public void deleteSchedule(int user_id, int event_id) {
		try {
			String sql = "delete from schedules where `user_id` ="
					+ user_id + " and `event_id` ="
					+ event_id + ";";
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int[] getEventsByUser(int user_id) {
		ArrayList<Integer> arrayEvents = new ArrayList<Integer>();
		try {
			String sql = "select * from schedules where `user_id` ="
					+ user_id;
			ResultSet rs = statement.executeQuery(sql);
			while (rs.next()) {
				int event_id = rs.getInt("event_id");
				arrayEvents.add(event_id);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		int[] array = new int[arrayEvents.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = arrayEvents.get(i);
		}
		if (array.length == 0) {
			return null;
		} else {
			return array;
		}

	}

	public int[] getUsersByEvents(int event_id) {
		ArrayList<Integer> arrayUsers = new ArrayList<Integer>();
		try {
			String sql = "select * from schedules where `event_id` ="
					+ event_id;
			ResultSet rs = statement.executeQuery(sql);
			while (rs.next()) {
				int user_id = rs.getInt("user_id");
				arrayUsers.add(user_id);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		int[] array = new int[arrayUsers.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = arrayUsers.get(i);
		}
		
		return array;
	}

	public void clearAll() {
		try {
			String sql = "truncate schedules ;";
			statement.executeUpdate(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void dropTable() {
		try {
			String sql = "drop table schedules ;";
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
