package com.mohammedev.allmightpedia.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User implements Parcelable {
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

    protected User(Parcel in) {
        userName = in.readString();
        userBio = in.readString();
        imageUrl = in.readString();
        email = in.readString();
        userID = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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

    public void setUserBio(String userBio) {
        this.userBio = userBio;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userName);
        dest.writeString(userBio);
        dest.writeString(imageUrl);
        dest.writeString(email);
        dest.writeString(userID);
    }
}
