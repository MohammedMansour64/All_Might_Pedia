package com.mohammedev.allmightpedia.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class FanArtPost implements Serializable , Comparable<FanArtPost> , Comparator<FanArtPost> {
    private String userName;
    private String userImageUrl;
    private String postImageUrl;
    private int likeCounter;
    private String imageID;
    private String userID;
    private HashMap<String,String> likedUsers;

    public FanArtPost() {
    }

    public FanArtPost( String postImage, int likeCounter, String imageID , String userName, String userImage, String userID, HashMap<String,String> likedUsers) {
        this.userName = userName;
        this.userImageUrl = userImage;
        this.postImageUrl = postImage;
        this.likeCounter = likeCounter;
        this.imageID = imageID;
        this.likedUsers = likedUsers;
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public HashMap<String,String> getLikedUsers() {
        return likedUsers;
    }

    public void setLikedUsers(HashMap<String,String> likes) {
        this.likedUsers = likes;
    }

    public int getLikeCounter() {
        return likeCounter;
    }

    public void setLikeCounter(int likeCounter) {
        this.likeCounter = likeCounter;
    }

    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }


    @Override
    public String toString() {
        return "FanArtPost{" +
                "userName='" + userName + '\'' +
                ", userImageUrl='" + userImageUrl + '\'' +
                ", postImageUrl='" + postImageUrl + '\'' +
                ", likeCounter=" + likeCounter +
                ", imageID='" + imageID + '\'' +
                '}';
    }

    @Override
    public int compareTo(FanArtPost o) {
        return 0;
    }

    @Override
    public int compare(FanArtPost o1, FanArtPost o2) {
        return 0;
    }

    public class LikedUsers{
        private String userID;

        public LikedUsers(String userID) {
            this.userID = userID;
        }

        public String getUserID() {
            return userID;
        }

        public void setUserID(String userID) {
            this.userID = userID;
        }
    }

}
