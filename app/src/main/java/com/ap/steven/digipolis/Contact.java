package com.ap.steven.digipolis;

/**
 * Created by Steven on 2/13/2015.
 * contact class
 */
public class Contact {
    public String name;
    public String tel;
    public Boolean favorite;

    public Contact(String name, String tel) {
        this.name = name;
        this.tel = tel;
        favorite = false;
    }

    @Override public String toString() {
        return name;
    }
}
