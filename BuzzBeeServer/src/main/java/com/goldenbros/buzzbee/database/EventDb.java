package com.goldenbros.buzzbee.database;

import java.sql.*;
import java.io.*;

import com.goldenbros.buzzbee.model.Event;
import com.goldenbros.buzzbee.model.EventList;
import com.goldenbros.buzzbee.util.Variable_final;

public class EventDb implements Variable_final {
	Connection connection = null;
	Statement statement = null;
	ResultSet resultSet = null;

	static final String jdbcDriver = "com.mysql.jdbc.Driver";
	static final String dbUrl = "jdbc:mysql://localhost";
	static final String user = "root";
	static final String passwd = "";

	public EventDb() {

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
					"eventdb_schema.txt"));
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

	/*
	 * this event does not contain id
	 */
	public void createEvent(Event event) {

		int holder_id = event.getHolderId();
		int event_status = event.getEventStat();
		int population = event.getPopulation();
		String date = event.getDate();
		String time = event.getTime();
		String location = event.getLocation();
		String name = event.getName();
		String category = event.getCategory();
		String description = event.getDesc();
		String photo_filename = event.getPhotoFilename();
		try {
			String sql = "insert into events (holder_id, event_status, population, date, time,"
					+ " location, name, category, description, photo_filename) "
					+ " values ( "
					+ holder_id
					+ ","
					+ event_status
					+ ","
					+ population
					+ ",'"
					+ date
					+ "','"
					+ time
					+ "','"
					+ location
					+ "','"
					+ name
					+ "','"
					+ category
					+ "','"
					+ description
					+ "','" + photo_filename + "');";
			statement.executeUpdate(sql);

			System.out.println("--In EventDB, Create Event Successfully.");
			System.out.println("--In EventDB, New Event name:"
					+ name + ", holder_id:" + holder_id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * this event contains id.
	 */
	public void updateEvent(Event event) {
		int id = event.getId();
		int event_status = event.getEventStat();
		int holder_id = event.getHolderId();
		int population = event.getPopulation();
		String date = event.getDate();
		String time = event.getTime();
		String location = event.getLocation();
		String name = event.getName();
		String category = event.getCategory();
		String description = event.getDesc();
		String photo_filename = event.getPhotoFilename();
		try {
			String sql = "update events set event_status = "
					+ event_status + ", holder_id="
					+ holder_id + ", population="
					+ population + ",date='" + date
					+ "',time= '" + time + "' ,location='"
					+ location + "',name='" + name
					+ "',category= '" + category
					+ "',description='" + description
					+ "' , photo_filename ='"
					+ photo_filename + "' where `id`=" + id
					+ ";";
			System.out.println(sql);
			statement.executeUpdate(sql);

			System.out.println("--In EventDB, Update Event Successfully.");
			System.out.println("--In EventDB, Event name:" + name
					+ ", E_id: " + id + ", holder_id:"
					+ holder_id);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public EventList getAllEvent() {
		EventList list = new EventList();
		try {
			String sql = "select * from events ;";
			ResultSet rs = statement.executeQuery(sql);
			while (rs.next()) {

				int id = rs.getInt("id");
				int event_status = rs.getInt("event_status");
				int holder_id = rs.getInt("holder_id");
				int population = rs.getInt("population");
				String date = rs.getString("date");
				String time = rs.getString("time");
				String location = rs.getString("location");
				String name = rs.getString("name");
				String category = rs.getString("category");
				String description = rs
						.getString("description");
				String filename = rs
						.getString("photo_filename");
				Event event = new Event(id, event_status,
						holder_id, population, name,
						date, time, location, category,
						description, filename);
				list.addToList(id, event);
			}
			if (list.getEventNumber() == 0)
				return null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/*
	 * return null if there is no result of these conditions.
	 */
	public EventList getByOptions(String category, String date,
			String time, String location) {
		EventList list = new EventList();
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("select * from events where `event_status` = "
					+ EVENT_STATUS_STARTED);
			if (category != null) {
				sb.append(" and `type` =");
				sb.append(category);
			}
			if (date != null) {
				sb.append(" and `date` =");
				sb.append(date);
			}
			if (time != null) {
				sb.append(" and `time` =");
				sb.append(time);
			}
			if (location != null) {
				sb.append(" and `location` =");
				sb.append(location);
			}
			sb.append(";");
			String sql = sb.toString();
			ResultSet rs = statement.executeQuery(sql);
			while (rs.next()) {

				int id = rs.getInt("id");
				int event_status = rs.getInt("event_status");
				int holder_id = rs.getInt("holder_id");
				int population = rs.getInt("population");
				String event_date = rs.getString("date");
				String event_time = rs.getString("time");
				String event_location = rs
						.getString("location");
				String name = rs.getString("name");
				String event_category = rs
						.getString("category");
				String description = rs
						.getString("description");
				String filename = rs
						.getString("photo_filename");
				Event event = new Event(id, event_status,
						holder_id, population, name,
						event_date, event_time,
						event_location, event_category,
						description, filename);
				list.addToList(id, event);
			}
			if (list.getEventNumber() == 0)
				return null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public int getOneEventId(Event event) {
		int holder_id = event.getHolderId();
		int population = event.getPopulation();
		String name = event.getName();
		String category = event.getCategory();
		String date = event.getDate();
		String time = event.getTime();
		String description = event.getDesc();

		try {
			String sql = "select * from events where `holder_id` = "
					+ holder_id
					+ " and `population`="
					+ population
					+ " and `name`='"
					+ name
					+ "' and `category`='"
					+ category
					+ "' and `date`='"
					+ date
					+ "' and `time`='"
					+ time
					+ "' and `description`='"
					+ description
					+ "';";

			ResultSet rs = statement.executeQuery(sql);
			while (rs.next()) {
				int id = rs.getInt("id");
				return id;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public void deleteEvent(int id) {
		try {
			String sql = "delete from events where `id` =" + id
					+ ";";
			statement.executeUpdate(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void clearAll() {
		try {
			String sql = "truncate events ;";
			statement.executeUpdate(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void dropTable() {
		try {
			String sql = "drop table events ;";
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}