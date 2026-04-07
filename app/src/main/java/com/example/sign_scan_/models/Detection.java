package com.example.sign_scan_.models;

import com.google.gson.annotations.SerializedName;

public class Detection {

    @SerializedName("id")
    private int id;

    @SerializedName("user_email")
    private String userEmail;

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

    @SerializedName("detected_at")
    private String detectedAt;

    public int getId() {
        return id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getSignName() {
        return signName;
    }

    public String getCategory() {
        return category;
    }

    public float getConfidence() {
        return confidence;
    }

    public String getMeaning() {
        return meaning;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDetectedAt() {
        return detectedAt;
    }
}
