package com.example.sign_scan_.models;

import com.google.gson.annotations.SerializedName;

public class DetectionResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("id")
    private int id;

    @SerializedName("sign_name")
    private String signName;

    @SerializedName("category")
    private String category;

    @SerializedName("confidence")
    private float confidence;

    @SerializedName("meaning")
    private String meaning;

    @SerializedName("image")
    private String imageUrl;

    public boolean isSuccess() {
        return success;
    }

    public Detection toDetection() {
        Detection d = new Detection();
        // We can't set private fields directly if there are no setters.
        // But we can use Gson or add setters to Detection.java. 
        // Or better, just instantiate Detection manually or map it.
        // For simplicity, let's just expose getters here and use them in Activity.
        return null; 
    }

    public int getId() { return id; }
    public String getSignName() { return signName; }
    public String getCategory() { return category; }
    public float getConfidence() { return confidence; }
    public String getMeaning() { return meaning; }
    public String getImageUrl() { return imageUrl; }
}
