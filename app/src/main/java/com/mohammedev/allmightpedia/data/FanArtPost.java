package com.mohammedev.allmightpedia.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class FanArtPost implements Serializable {
    private String userName;
    private String userImageUrl;
    private String postImageUrl;
    private boolean likeButton;
    private int likeCounter;
    private String imageID;
    private LinkedList<String> likes;

    public FanArtPost() {
    }

    public FanArtPost( String postImage, boolean likeButton, int likeCounter, String imageID , String userName, String userImage, LinkedList<String> likes) {
        this.userName = userName;
        this.userImageUrl = userImage;
        this.postImageUrl = postImage;
        this.likeButton = likeButton;
        this.likeCounter = likeCounter;
        this.imageID = imageID;
        this.likes = likes;
    }


    public LinkedList<String> getLikes() {
        return likes;
    }

    public void setLikes(LinkedList<String> likes) {
        this.likes = likes;
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

    public boolean isLikeButton() {
        return likeButton;
    }

    public void setLikeButton(boolean likeButton) {
        this.likeButton = likeButton;
    }

    @Override
    public String toString() {
        return "FanArtPost{" +
                "userName='" + userName + '\'' +
                ", userImageUrl='" + userImageUrl + '\'' +
                ", postImageUrl='" + postImageUrl + '\'' +
                ", likeButton=" + likeButton +
                ", likeCounter=" + likeCounter +
                ", imageID='" + imageID + '\'' +
                '}';
    }

}
