package com.example.projectpixer;

import com.google.firebase.database.Exclude;

public class FaceUpload {

    private String mKey;

    private String mImageUrl;
    private int faces;

    public FaceUpload() {
    }

    public FaceUpload(String mImageUrl, int faces)
    {
        this.mImageUrl = mImageUrl;
        this.faces = faces;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public int getFaces(){return faces;}


    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public void setFaces(int faces){this.faces = faces;}

    @Exclude
    public String getKey() {
        return mKey;
    }
    @Exclude
    public void setKey(String key) {
        mKey = key;
    }
}
