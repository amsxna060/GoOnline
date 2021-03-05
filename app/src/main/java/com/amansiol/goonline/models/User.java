package com.amansiol.goonline.models;

import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
/*
    This is User Custom class which is used to
    collect all the necessary details regarding
    one User
 */
public class User {
    String name;
    String email;
    String phonenumber;
    String profile_img;
    String uid;
    boolean haveshop;
    GeoPoint location;
    HashMap<String, String> address= new HashMap<>();
    boolean isVerified;
    String gender;


    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public User() {

    }

    public boolean getHaveshop() {
        return haveshop;
    }

    public void setHaveshop(boolean haveshop) {
        this.haveshop = haveshop;
    }

    public User(String name, String email, String phonenumber, String profile_img, String uid, GeoPoint location, HashMap<String, String> address) {
        this.name = name;
        this.email = email;
        this.phonenumber = phonenumber;
        this.profile_img = profile_img;
        this.uid = uid;
        this.location = location;
        this.address = address;
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

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
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

