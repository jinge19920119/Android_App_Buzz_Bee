package com.goldenbros.buzzbee.model;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by kimiko on 2015/7/17.
 */
public class UserInfo implements TitleConstants{
    private int id;
    private String email;
    private String name;
    private int title;
    private String photoURL;

    protected UserInfo(){}

    protected UserInfo(int id, String email){
        setID(id);
        setEmail(email);
        setTitle(WORKER_BEE);
    }

    protected void setID(int id){
        this.id = id;
    }

    protected void setName(String name){
        this.name = name;
    }

    protected void setEmail(String email){
        this.email = email;
    }

    protected void setTitle(int title){
        this.title = title;
    }

    protected void setPhotoURL(String url) { this.photoURL = url;}

    protected int getId(){
        return id;
    }

    protected String getEmail(){
        return email;
    }

    protected int getTitle(){
        return title;
    }

    protected String getName(){ return name; }

    protected String getPhotoURL(){ return photoURL; }
}
