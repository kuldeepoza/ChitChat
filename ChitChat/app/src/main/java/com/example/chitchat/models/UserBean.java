package com.example.chitchat.models;

/**
 * Created by KD on 24-Jun-17.
 */

public class UserBean {
    public String email;
    public String password;
    public String name;
    public String mobile_no;
    public String date;
    public String uid;
    public String online;

    public String getOnline() {
        return online;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }

    public String profile_url;
    public UserBean(){}
    public UserBean(String profile_url,String email,String password,String mobile_no,String name,String date,String online) {
        this.profile_url=profile_url;
        this.name=name;
        this.mobile_no=mobile_no;
        this.password=password;
        this.email=email;
        this.date=date;
        this.online=online;
    }

    public void setOnline(String online) {
        this.online = online;
    }
}
