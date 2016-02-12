package com.goldenbros.buzzbee.model;

/**
 * Created by kimiko on 2015/7/17.
 */
public interface ManipulateUserEvents {

    public void addHolderEvent(Event e);
    public void removeHolderEvent(int e_id);
    public void addJoinEvent(Event e);
    public void removeJoinEvent(int e_id);
    public void findHoldEvent(int e_id);
    public void findJoinEvent(int e_id);
}
