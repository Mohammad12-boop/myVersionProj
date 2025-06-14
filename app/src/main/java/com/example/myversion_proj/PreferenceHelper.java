package com.example.myversion_proj;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceHelper {
    private final SharedPreferences sharedPreferences;

    public PreferenceHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveAuthToken(String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.KEY_AUTH_TOKEN, token);
        editor.apply();
    }

    public String getAuthToken() {
        return sharedPreferences.getString(Constants.KEY_AUTH_TOKEN, "");
    }

    public void saveUserId(String userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.KEY_USER_ID, userId);
        editor.apply();
    }

    public String getUserId() {
        return sharedPreferences.getString(Constants.KEY_USER_ID, "");
    }

    public void saveUserRole(String role) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.KEY_USER_ROLE, role);
        editor.apply();
    }

    public String getUserRole() {
        return sharedPreferences.getString(Constants.KEY_USER_ROLE, "");
    }

    public void clearSession() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
