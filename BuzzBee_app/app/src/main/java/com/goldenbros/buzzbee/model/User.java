package com.goldenbros.buzzbee.model;

import java.io.Serializable;

/**
 * Created by kimiko on 2015/7/17.
 */
public class User implements Serializable{

    private static final long serialVersionUID = -7188309341824921892L;

    private int id;
    private String email;
    private String passwd;
    private String name;
    private String title;
    private String sex;
    private int age;
    private String description;
    private String photoName;
    private String audioPath;

    public User() {

    }

    public User(String email, String passwd) {
        this.email = email;
        this.passwd = passwd;
    }

    public User(int id, String email, String photoName) {
        this.id = id;
        this.email = email;
        this.photoName = photoName;
    }


    public User(int id, String email, String name, String sex, int age, String description, String audioPath) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.description = description;
        this.audioPath = audioPath;
    }

    public User(int id, String name, String email, String passwd, String title,
                String sex, int age, String description, String audioPath) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwd = passwd;
        this.title = title;
        this.sex = sex;
        this.age = age;
        this.description = description;
        this.audioPath = audioPath;
    }

    public int getID() {
        return id;
    }
    public void setID(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String pass) {
        this.passwd = pass;
    }

    public String getPassword() {
        return passwd;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSex() {
        return sex;
    }

    public String getDescrip() {
        return this.description;
    }

    public void setDesc(String des) {
        this.description = des;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

}
