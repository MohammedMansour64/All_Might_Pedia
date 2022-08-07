package com.mohammedev.allmightpedia.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Moment implements Parcelable {
    private String momentTitle;
    private String momentDesc;
    private String momentImage;
    private String momentVideo;

    public Moment(String momentTitle, String momentDesc, String momentImageUrl, String momentVideoUrl) {
        this.momentTitle = momentTitle;
        this.momentDesc = momentDesc;
        this.momentImage = momentImageUrl;
        this.momentVideo = momentVideoUrl;
    }

    public Moment() {
    }

    protected Moment(Parcel in) {
        momentTitle = in.readString();
        momentDesc = in.readString();
        momentImage = in.readString();
        momentVideo = in.readString();
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

    public String getMomentVideo() {
        return momentVideo;
    }

    public void setMomentVideo(String momentVideo) {
        this.momentVideo = momentVideo;
    }

    public String getMomentTitle() {
        return momentTitle;
    }

    public void setMomentTitle(String momentTitle) {
        this.momentTitle = momentTitle;
    }

    public String getMomentDesc() {
        return momentDesc;
    }

    public void setMomentDesc(String momentDesc) {
        this.momentDesc = momentDesc;
    }

    public String getMomentImage() {
        return momentImage;
    }

    public void setMomentImage(String momentImage) {
        this.momentImage = momentImage;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(momentTitle);
        parcel.writeString(momentDesc);
        parcel.writeString(momentImage);
        parcel.writeString(momentVideo);
    }
}