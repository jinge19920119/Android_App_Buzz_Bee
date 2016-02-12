package com.goldenbros.buzzbee.database;

import java.sql.*;
import java.io.*;

import com.goldenbros.buzzbee.model.User;
import com.goldenbros.buzzbee.util.Variable_final;

public class UserDb implements Variable_final {

	Connection connection = null;
	Statement statement = null;
	ResultSet resultSet = null;

	static final String jdbcDriver = "com.mysql.jdbc.Driver";
	static final String dbUrl = "jdbc:mysql://localhost";
	static final String user = "root";
	static final String passwd = "";
	
	private static final String DEFAULT_PHOTO = "https://s3.amazonaws.com/buzzbeestorage-user/profile-web.png";

	public UserDb() {

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
					"userdb_schema.txt"));
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

	public boolean createNewUser(String email, String passwd) {
		boolean canAdded = false;
		try {
			String sql = "select * from users where `email` = '"
					+ email + "';";
			ResultSet rs = statement.executeQuery(sql);
			if (!rs.next()) {
				canAdded = true;
				sql = "insert into users (`email` , `passwd`, `photo`) values ('"
						+ email
						+ "','"
						+ passwd
						+ "','"
						+ DEFAULT_PHOTO
						+ "');";
				statement.executeUpdate(sql);
				System.out.println("%%New User added in DB, email: "
						+ email + ", passwd: " + passwd + ", photo: " + DEFAULT_PHOTO);
			} else {
				canAdded = false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return canAdded;

	}

	public boolean verifyUser(String email, String passwd) {
		boolean verified = false;

		try {
			String sql = "select * from users where email='"
					+ email + "';";
			ResultSet rs = statement.executeQuery(sql);
			while (rs.next()) {
				System.out.println("$$In DB, email exist:"
						+ email);
				String truePass = rs.getString("passwd");
				if (truePass.equals(passwd)) {
					verified = true;
					System.out.println("$$In DB, passwd exist:"
							+ truePass);
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return verified;

	}

	public User getUser(int id) {
		User user = new User();
		try {
			String sql = "select * from users where `id` =" + id
					+ ";";
			ResultSet rs = statement.executeQuery(sql);
			while (rs.next()) {
				String name = rs.getString("name");
				String email = rs.getString("email");
				String passwd = rs.getString("passwd");
				String title = rs.getString("title");
				String sex = rs.getString("sex");
				String description = rs
						.getString("description");
				int age = rs.getInt("age");
				String photoName = rs.getString("photo");
				String audioPath = rs.getString("audio");
				
				user = new User(id, name, email, passwd, title,
						sex, age, description, audioPath);
				user.setPhotoName(photoName);
			}

		} catch (SQLException e) {
			return null;
		}
		return user;
	}
	
	public User getUser(String email) {
		User user = new User();
		try {
			String sql = "select * from users where `email` ='" + email
					+ "';";
			ResultSet rs = statement.executeQuery(sql);
			while (rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				String passwd = rs.getString("passwd");
				String title = rs.getString("title");
				String sex = rs.getString("sex");
				String description = rs
						.getString("description");
				int age = rs.getInt("age");
				String photoName = rs.getString("photo");
				String audioPath = rs.getString("audio");
				
				user = new User(id, name, email, passwd, title,
						sex, age, description, audioPath);
				user.setPhotoName(photoName);
			}

		} catch (SQLException e) {
			return null;
		}
		return user;
	}
	

	public void updateUser(int id, String name, String email) {

		try {
			String sql = "update users set `name` ='" + name
					+ "', `email` = '" + email
					+ "' where `id` =" + id + ";";
			statement.executeUpdate(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateUser(User updateUser) {
		int id = updateUser.getID();
		String email = updateUser.getEmail();
		String name = updateUser.getName();
		String sex = updateUser.getSex();
		int age = updateUser.getAge();
		String description = updateUser.getDescrip();
		String audioPath = updateUser.getAudioPath();
		
		try {
			String sql = "update users set `name` ='" + name
					+ "', `sex` = '" + sex
					+ "', `age` = '" + age
					+ "', `description` = '" + description
					+ "', `audio` = '" + audioPath
					+ "' where `id` =" + id + ";";
			statement.executeUpdate(sql);
			System.out.println("int UserDb, update user: " + id + ", email: " + email);
			System.out.println("int UserDb, update Audio: " + audioPath);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateUserImage(User updateUser) {
		int id = updateUser.getID();
		String photoName = updateUser.getPhotoName();

		try {
			String sql = "update users set `photo` ='" + photoName
					+ "' where `id` =" + id + ";";
			statement.executeUpdate(sql);
			System.out.println("int UserDb, update userImage, id:" + id);
			System.out.println("int UserDb, update userImage, photo:" + photoName);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteUser(String email) {
		try {
			String sql = "delete from users where `email` ='"
					+ email + "';";
			statement.executeUpdate(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void clearAll() {
		try {
			String sql = "truncate users ;";
			statement.executeUpdate(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void dropTable() {
		try {
			String sql = "drop table users ;";
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
