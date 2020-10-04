package com.example.projectpixer;

import com.google.firebase.database.Exclude;

public class ObjectUpload {

    private String mKey;

    private String mImageUrl;
    private int objects;

    public ObjectUpload() {
    }

    public ObjectUpload(String mImageUrl, int objects)
    {
        this.mImageUrl = mImageUrl;
        this.objects = objects;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public int getObjects(){return objects;}


    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public void setObjects(int objects){this.objects = objects;}

    @Exclude
    public String getKey() {
        return mKey;
    }
    @Exclude
    public void setKey(String key) {
        mKey = key;
    }
}
