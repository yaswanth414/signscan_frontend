package com.example.sign_scan_.models;

import com.google.gson.annotations.SerializedName;

public class TrafficSign {
    private int id;
    private String title;
    
    @SerializedName("image")
    private String imageUrl;
    
    private String category;
    private String shape;
    private String color;
    private String meaning;

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getImageUrl() { return imageUrl; }
    public String getCategory() { return category; }
    public String getShape() { return shape; }
    public String getColor() { return color; }
    public String getMeaning() { return meaning; }
}
