package com.example.sign_scan_.models;

import com.google.gson.annotations.SerializedName;

public class LoginActivity {
    @SerializedName("id")
    private int id;

    @SerializedName("email")
    private String email;

    @SerializedName("role")
    private String role;

    @SerializedName("login_time")
    private String loginTime;

    public int getId() { return id; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getLoginTime() { return loginTime; }
}
