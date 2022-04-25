package com.mohammedev.allmightpedia.data;

public class FanArtPost {
    private String userName;
    private String userImageUrl;
    private String postImageUrl;
    private boolean likeButton;
    private int likeCounter;

    public FanArtPost(String userName, String userImage, String postImage, boolean likeButton, int likeCounter) {
        this.userName = userName;
        this.userImageUrl = userImage;
        this.postImageUrl = postImage;
        this.likeButton = likeButton;
        this.likeCounter = likeCounter;
    }

    public FanArtPost(){

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

    public int getLikeCounter() {
        return likeCounter;
    }

    public void setLikeCounter(int likeCounter) {
        this.likeCounter = likeCounter;
    }

    @Override
    public String toString() {
        return "FanArtPost{" +
                "userName='" + userName + '\'' +
                ", userImage=" + userImageUrl +
                ", postImage=" + postImageUrl +
                ", likeButton=" + likeButton +
                ", likeCounter=" + likeCounter +
                '}';
    }
}
