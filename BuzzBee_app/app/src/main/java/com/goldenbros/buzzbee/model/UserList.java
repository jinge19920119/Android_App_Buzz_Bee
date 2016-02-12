package com.goldenbros.buzzbee.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by kimiko on 2015/7/27.
 */
public class UserList implements Serializable{
    private static final long serialVersionUID = 897037276423607765L;

    private LinkedHashMap<Integer, User> user_list;

    public UserList(){
        user_list = new LinkedHashMap<Integer, User>();
    }

    public void addUser(User e){
        user_list.put(e.getID(),e);
    }

    public User getUser(int p){
        List<User> l = new ArrayList<User>(user_list.values());
        return  l.get(p);
    }

    public int getUserCount(){
        return user_list.size();
    }

    public LinkedHashMap<Integer, User> getUserList() {
        return user_list;
    }

    public String[] getUserIDArray(){
        String [] arr = new String [user_list.size()];
        Integer[] id_arr = new Integer[user_list.size()];
        user_list.keySet().toArray(id_arr);

        for(int i = 0; i < user_list.size(); i++){
            arr[i] = id_arr[i] +"";
        }

        return arr;
    }
}
