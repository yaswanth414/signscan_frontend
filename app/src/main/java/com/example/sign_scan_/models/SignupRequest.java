package com.example.sign_scan_.models;

public class SignupRequest {

    @com.google.gson.annotations.SerializedName("full_name")
    private String name;
    private String email;
    private String password;

    public SignupRequest(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }
}
