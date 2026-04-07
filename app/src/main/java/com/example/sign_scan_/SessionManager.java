package com.example.sign_scan_;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.sign_scan_.models.LoginResponse;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_ROLE = "user_role";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(LoginResponse response) {
        editor.putString(KEY_ACCESS_TOKEN, response.getAccess());
        editor.putString(KEY_REFRESH_TOKEN, response.getRefresh());
        
        if (response.getUser() != null) {
            editor.putInt(KEY_USER_ID, response.getUser().getId());
            editor.putString(KEY_USER_NAME, response.getUser().getFullName());
            editor.putString(KEY_USER_EMAIL, response.getUser().getEmail());
            editor.putString(KEY_USER_ROLE, response.getUser().getRole());
        }
        
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public void saveSession(com.example.sign_scan_.models.AdminLoginResponse response) {
        editor.putString(KEY_ACCESS_TOKEN, response.getAccess());
        editor.putString(KEY_REFRESH_TOKEN, response.getRefresh());
        
        if (response.getUser() != null) {
            editor.putInt(KEY_USER_ID, response.getUser().getId());
            editor.putString(KEY_USER_NAME, response.getUser().getFullName());
            editor.putString(KEY_USER_EMAIL, response.getUser().getEmail());
            editor.putString(KEY_USER_ROLE, response.getUser().getRole());
        } else if (response.getAdmin() != null) {
            editor.putInt(KEY_USER_ID, response.getAdmin().getId());
            editor.putString(KEY_USER_NAME, "Admin");
            editor.putString(KEY_USER_EMAIL, response.getAdmin().getEmail());
            editor.putString(KEY_USER_ROLE, "Admin");
        }
        
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public String getAccessToken() {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, null);
    }
    
    public String getRefreshToken() {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, null);
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getFullName() {
        return sharedPreferences.getString(KEY_USER_NAME, "User");
    }

    public String getUserRole() {
        return sharedPreferences.getString(KEY_USER_ROLE, "User");
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
