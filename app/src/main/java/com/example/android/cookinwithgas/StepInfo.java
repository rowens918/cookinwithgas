package com.example.android.cookinwithgas;

public class StepInfo {
    String stepId;
    String stepTitle;
    String stepDescription;
    String stepVideo;
    String stepImage;

    public StepInfo(){

    }

    public StepInfo(String sId, String sTitle, String sDescription, String sVideo, String sImage) {
        this.stepId = sId;
        this.stepTitle = sTitle;
        this.stepDescription = sDescription;
        this.stepVideo = sVideo;
        this.stepImage = sImage;
    }
}
