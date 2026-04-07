package com.example.sign_scan_.models;

import com.google.gson.annotations.SerializedName;

public class AdminQuizResult {
    @SerializedName("id")
    private int id;

    @SerializedName("email")
    private String email;

    @SerializedName("date")
    private String date;

    @SerializedName("score_text")
    private String scoreText;

    public int getId() { return id; }
    public String getEmail() { return email; }
    public String getDate() { return date; }
    public String getScoreText() { return scoreText; }
}
