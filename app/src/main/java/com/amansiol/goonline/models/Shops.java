package com.amansiol.goonline.models;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.HashMap;

public class Shops implements Serializable {
    String shopname;
    String name;
    String email;
    String phonenumber;
    String uid;
    GeoPoint location;
    HashMap<String, String> address= new HashMap<>();

    public Shops() {
    }

    public Shops(String shopname, String name, String email, String phonenumber, String uid, GeoPoint location, HashMap<String, String> address) {
        this.shopname = shopname;
        this.name = name;
        this.email = email;
        this.phonenumber = phonenumber;
        this.uid = uid;
        this.location = location;
        this.address = address;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public HashMap<String, String> getAddress() {
        return address;
    }

    public void setAddress(HashMap<String, String> address) {
        this.address = address;
    }
}
