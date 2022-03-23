package com.mohammedev.allmightpedia.data;

public class User {
    private String userName, userBio , imageUrl , email;


    public User(String name, String userBio , String imageUrl , String email) {
        this.userName = name;
        this.userBio = userBio;
        this.imageUrl = imageUrl;
        this.email = email;
    }

    public User(String name, String userBio , String imageUrl) {
        this.userName = name;
        this.userBio = userBio;
        this.imageUrl = imageUrl;
    }

    public User() {
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