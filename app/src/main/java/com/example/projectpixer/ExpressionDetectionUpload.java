package com.example.projectpixer;

import com.google.firebase.database.Exclude;

public class ExpressionDetectionUpload {

    private String mKey;

    private String mImageUrl;
    private String BoundingPolygon;
    private String AnglesOfRotation;
    private String LeftEarPos;
    private String RightEarPos;
    private String SmileProbability;
    private String RightEyeOpenProbability;
    private String LeftEyeOpenProbability;

    public ExpressionDetectionUpload() {
    }

    public ExpressionDetectionUpload(String mImageUrl, String boundingPolygon, String AnglesOfRotation, String leftEarPos, String rightEarPos,
                                     String smileProbability, String rightEyeOpenProbability, String leftEyeOpenProbability)
    {
        this.mImageUrl = mImageUrl;
        this.BoundingPolygon = boundingPolygon;
        this.AnglesOfRotation = AnglesOfRotation;
        this.LeftEarPos = leftEarPos;
        this.RightEarPos = rightEarPos;
        this.SmileProbability = smileProbability;
        this.RightEyeOpenProbability = rightEyeOpenProbability;
        this.LeftEyeOpenProbability = leftEyeOpenProbability;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public String getBoundingPolygon() {
        return BoundingPolygon;
    }

    public String getAnglesOfRotation() {
        return AnglesOfRotation;
    }


    public String getLeftEarPos() {
        return LeftEarPos;
    }

    public String getRightEarPos() {
        return RightEarPos;
    }

    public String getSmileProbability() {
        return SmileProbability;
    }

    public String getRightEyeOpenProbability() {
        return RightEyeOpenProbability;
    }

    public String getLeftEyeOpenProbability() {
        return LeftEyeOpenProbability;
    }


    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public void setBoundingPolygon(String boundingPolygon) {
        BoundingPolygon = boundingPolygon;
    }

    public void setAnglesOfRotation(String anglesOfRotation) {
        AnglesOfRotation = anglesOfRotation;
    }


    public void setLeftEarPos(String leftEarPos) {
        LeftEarPos = leftEarPos;
    }

    public void setRightEarPos(String rightEarPos) {
        RightEarPos = rightEarPos;
    }

    public void setSmileProbability(String smileProbability) {
        SmileProbability = smileProbability;
    }

    public void setRightEyeOpenProbability(String rightEyeOpenProbability) {
        RightEyeOpenProbability = rightEyeOpenProbability;
    }

    public void setLeftEyeOpenProbability(String leftEyeOpenProbability) {
        LeftEyeOpenProbability = leftEyeOpenProbability;
    }

    @Exclude
    public String getKey() {
        return mKey;
    }
    @Exclude
    public void setKey(String key) {
        mKey = key;
    }
}
