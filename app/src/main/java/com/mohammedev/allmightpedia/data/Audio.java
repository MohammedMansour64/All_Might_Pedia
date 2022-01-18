package com.mohammedev.allmightpedia.data;

public class Audio {
    private String audioValue;
    private String audioName;

    public Audio(String audioValue, String audioName) {
        this.audioValue = audioValue;
        this.audioName = audioName;
    }

    public String getAudioName() {
        return audioName;
    }

    public void setAudioName(String audioName) {
        this.audioName = audioName;
    }

    public String getAudioValue() {
        return audioValue;
    }

    public void setAudioValue(String audioValue) {
        this.audioValue = audioValue;
    }
}
