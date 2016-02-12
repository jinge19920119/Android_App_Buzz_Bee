package com.goldenbros.buzzbee.model;

import java.io.Serializable;

public class Event implements Serializable {
    private static final long serialVersionUID = -8320510218256481084L;

    private int id;
    private int holder_id;
    private int event_status;
    private int population;
    private String name;
    private String date;
    private String time;
    private String location;
    private String category;
    private String description;
    private String photo_filename;

    public Event() {

    }

    public Event(int event_status, int holder_id, int population,
                 String name, String date, String time, String location,
                 String category, String description,
                 String photo_filename) {
        this.holder_id = holder_id;
        this.event_status = event_status;
        this.population = population;
        this.name = name;
        this.date = date;
        this.time = time;
        this.location = location;
        this.category = category;
        this.description = description;
        this.photo_filename = photo_filename;
    }

    public Event(int id, int event_status, int holder_id, int population,
                 String name, String date, String time, String location,
                 String category, String description,
                 String photo_filename) {
        this.id = id;
        this.holder_id = holder_id;
        this.event_status = event_status;
        this.population = population;
        this.name = name;
        this.date = date;
        this.time = time;
        this.location = location;
        this.category = category;
        this.description = description;
        this.photo_filename = photo_filename;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setHolder(int id) {
        this.holder_id = id;
    }

    public int getHolderId() {
        return this.holder_id;
    }

    public int getPopulation() {
        return this.population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getEventStat() {
        return this.event_status;
    }

    public void setStatus(int status) {
        this.event_status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String loca) {
        this.location = loca;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDesc(String desc) {
        this.description = desc;
    }

    public String getDesc() {
        return this.description;
    }

    public String getPhotoFilename() {
        return this.photo_filename;
    }

    public void setPhotoFilename(String filename) {
        this.photo_filename = filename;
    }

}
