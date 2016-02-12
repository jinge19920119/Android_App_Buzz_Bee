package com.goldenbros.buzzbee.model;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class EventList implements Serializable {
	
	private static final long serialVersionUID = 71711230542787019L;
	
	private LinkedHashMap<Integer, Event> list;

	public EventList() {
		list = new LinkedHashMap<Integer, Event>();
	}

	public void addToList(int id, Event event) {
		list.put((Integer) id, event);
	}

	public int getEventNumber() {
		return list.size();
	}

	public Event getEvent(int id) {
		return list.get((Integer) id);
	}

	public LinkedHashMap<Integer, Event> getList() {
		return list;
	}

	public void updateEvent(int id, Event event) {
		list.put((Integer) id, event);
	}

	public void deleteEvent(int id) {
		list.remove((Integer) id);
	}

}
