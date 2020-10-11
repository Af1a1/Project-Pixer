package com.example.projectpixer;

import com.google.firebase.database.Exclude;

public class TextUpload {

    private String mKey;

    private String mImageUrl;
    private String text;

    public TextUpload() {
    }

    public TextUpload(String mImageUrl, String text)
    {
        this.mImageUrl = mImageUrl;
        this.text = text;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public String getText(){return text;}


    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public void setText(String text){this.text = text;}

    @Exclude
    public String getKey() {
        return mKey;
    }
    @Exclude
    public void setKey(String key) {
        mKey = key;
    }
}