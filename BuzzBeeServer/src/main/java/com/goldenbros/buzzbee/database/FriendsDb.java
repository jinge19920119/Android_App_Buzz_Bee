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


public class FriendsDb {

	Connection connection= null;
    Statement statement= null;
    ResultSet resultSet=null;

    static final String jdbcDriver = "com.mysql.jdbc.Driver";
    static final String dbUrl= "jdbc:mysql://localhost";
    static final String user= "root";
    static final String passwd= "";
    
    public FriendsDb(){
    	
    }
    public void buildConnect() {
        try {
            Class.forName(jdbcDriver);
            connection= DriverManager.getConnection(dbUrl, user, passwd);
            statement= connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void createDB(){
        try {
            BufferedReader br= new BufferedReader(new FileReader("friendsdb_schema.txt"));
            StringBuilder sb = new StringBuilder();
            String line= null;
            while ((line=br.readLine())!=null){
                sb.append(line);
                if(sb.length()>0 && sb.charAt(sb.length()-1)==';'){
                    statement.execute(sb.toString());
                    sb= new StringBuilder();
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
    
    public boolean checkFriends(int user1_id, int user2_id) {
	    boolean isFriends= false;
	    try {
		    String sql= "select * from friends where `user1_id` ="+ user1_id +" and `user2_id` ="+user2_id+";";
		    ResultSet rs= statement.executeQuery(sql);
		    if(rs.next()){
			    isFriends = true;
		    } 	
		} catch (SQLException e) {
			e.printStackTrace();
		}
	    return isFriends;
    }
    
    public boolean addFriends(int user1_id, int user2_id){//return true if they are friends already
    	boolean isFriends= false;
    	try {
    		String sql= "select * from friends where `user1_id` ="+ user1_id +" and `user2_id` ="+user2_id+";";
			ResultSet rs= statement.executeQuery(sql);
			if(!rs.next()){
				sql= "insert into friends (`user1_id` , `user2_id`) values ("+user1_id +" , "+user2_id + ");";
				statement.executeUpdate(sql);
				sql= "insert into friends (`user1_id` , `user2_id`) values ("+user2_id +" , "+user1_id + ");";
				statement.executeUpdate(sql);
			} else {
				isFriends = true;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return isFriends;
    }
    public void deleteFriends(int user1_id, int user2_id){
    	try {
    		String sql= "delete from friends where `user1_id` ="+ user1_id +" and `user2_id` ="+user2_id+";";
			statement.executeUpdate(sql);
			sql= "delete from friends where `user1_id` ="+ user2_id +" and `user2_id` ="+user1_id+";";
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    public void deleteUsers(int user_id){
    	try {
    		String sql= "delete from friends where `user1_id` ="+ user_id +";";
			statement.executeUpdate(sql);
			sql= "delete from friends where `user2_id` ="+ user_id +";";
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    public int[] findFriends(int user_id){
    	ArrayList<Integer> arrayFriends= new ArrayList<Integer>();
    	try {
    		String sql= "select * from friends where `user1_id` ="+ user_id +";";
			ResultSet rs= statement.executeQuery(sql);
			while(rs.next()){
				arrayFriends.add(rs.getInt("user2_id"));
			}
			int[] array= new int[arrayFriends.size()];
			for(int i=0;i<array.length;i++){
				array[i]= arrayFriends.get(i);
			}
			System.out.println("^^In friendsDB, get number friends:" + array.length);
			return array;
		} catch (SQLException e) {
			return null;
		}
    }
    public void clearAll(){
    	try {
    		String sql= "truncate friends ;";
			statement.executeUpdate(sql);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
}
