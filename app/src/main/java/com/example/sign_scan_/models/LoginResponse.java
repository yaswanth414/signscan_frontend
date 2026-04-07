package com.example.sign_scan_.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("access")
    private String access;

    @SerializedName("refresh")
    private String refresh;

    @SerializedName("user")
    private User user;

    public String getMessage() {
        return message;
    }

    public String getAccess() {
        return access;
    }

    public String getRefresh() {
        return refresh;
    }

    public User getUser() {
        return user;
    }

    public static class User {
        @SerializedName("id")
        private int id;

        @SerializedName("full_name")
        private String fullName;

        @SerializedName("email")
        private String email;

        @SerializedName("role")
        private String role;

        public int getId() {
            return id;
        }

        public String getFullName() {
            return fullName;
        }

        public String getEmail() {
            return email;
        }

        public String getRole() {
            return role;
        }
    }
}
