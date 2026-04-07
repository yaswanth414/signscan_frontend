package com.example.sign_scan_.models;

import com.google.gson.annotations.SerializedName;

public class AdminLoginRequest {
    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    public AdminLoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
