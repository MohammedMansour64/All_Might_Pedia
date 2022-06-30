package com.mohammedev.allmightpedia.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {
    private String userName, userBio , imageUrl , email, userID;



    public User(String userName, String userBio, String imageUrl, String email, String userID) {
        this.userName = userName;
        this.userBio = userBio;
        this.imageUrl = imageUrl;
        this.email = email;
        this.userID = userID;
    }



    public User() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserBio() {
        return userBio;
    }

    public void setUserBio(String job) {
        this.userBio = userBio;
    }
}
