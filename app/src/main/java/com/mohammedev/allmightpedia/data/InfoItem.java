package com.mohammedev.allmightpedia.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class InfoItem implements Parcelable{
    private String infoTitle;
    private String infoDesc;
    private String infoImageUrl;
    private String infoVideoUrl;

    public InfoItem(String infoTitle, String infoDesc, String infoImageUrl, String infoVideoUrl) {
        this.infoTitle = infoTitle;
        this.infoDesc = infoDesc;
        this.infoImageUrl = infoImageUrl;
        this.infoVideoUrl = infoVideoUrl;
    }

    public InfoItem() {
    }

    protected InfoItem(Parcel in) {
        infoTitle = in.readString();
        infoDesc = in.readString();
        infoImageUrl = in.readString();
        infoVideoUrl = in.readString();
    }

    public static final Creator<InfoItem> CREATOR = new Creator<InfoItem>() {
        @Override
        public InfoItem createFromParcel(Parcel in) {
            return new InfoItem(in);
        }

        @Override
        public InfoItem[] newArray(int size) {
            return new InfoItem[size];
        }
    };

    public String getInfoVideoUrl() {
        return infoVideoUrl;
    }

    public void setInfoVideoUrl(String infoVideoUrl) {
        this.infoVideoUrl = infoVideoUrl;
    }

    public String getInfoTitle() {
        return infoTitle;
    }

    public void setInfoTitle(String infoTitle) {
        this.infoTitle = infoTitle;
    }

    public String getInfoDesc() {
        return infoDesc;
    }

    public void setInfoDesc(String infoDesc) {
        this.infoDesc = infoDesc;
    }

    public String getInfoImageUrl() {
        return infoImageUrl;
    }

    public void setInfoImageUrl(String infoImageUrl) {
        this.infoImageUrl = infoImageUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(infoTitle);
        parcel.writeString(infoDesc);
        parcel.writeString(infoImageUrl);
        parcel.writeString(infoVideoUrl);
    }
}
