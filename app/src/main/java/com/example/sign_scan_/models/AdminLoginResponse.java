package com.example.sign_scan_.models;

import com.google.gson.annotations.SerializedName;

public class AdminLoginResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("access")
    private String access;

    @SerializedName("refresh")
    private String refresh;

    @SerializedName("admin")
    private AdminInfo admin;

    @SerializedName("user")
    private LoginResponse.User user;

    public String getMessage() { return message; }
    public String getAccess() { return access; }
    public String getRefresh() { return refresh; }
    public AdminInfo getAdmin() { return admin; }
    public LoginResponse.User getUser() { return user; }

    public static class AdminInfo {
        @SerializedName("id")
        private int id;

        @SerializedName("email")
        private String email;

        @SerializedName("is_superuser")
        private boolean isSuperuser;

        public int getId() { return id; }
        public String getEmail() { return email; }
        public boolean isSuperuser() { return isSuperuser; }
    }
}
