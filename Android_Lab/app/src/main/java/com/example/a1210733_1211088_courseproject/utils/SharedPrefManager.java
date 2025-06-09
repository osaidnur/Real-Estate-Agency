package com.example.a1210733_1211088_courseproject.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "Remember Sign-In info";
    private static final int SHARED_PREF_PRIVATE = Context.MODE_PRIVATE;
    private static SharedPrefManager ourInstance = null;
    private static SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor = null;
      // Keys for remember me functionality
    public static final String KEY_REMEMBER_EMAIL = "remember_email";
    public static final String KEY_REMEMBER_PASSWORD = "remember_password";
    public static final String KEY_REMEMBER_ME = "remember_me";
    
    // Key for current user ID
    public static final String KEY_CURRENT_USER_ID = "current_user_id";
    
    public static SharedPrefManager getInstance(Context context) {
        if (ourInstance != null) {
            return ourInstance;
        }
        ourInstance=new SharedPrefManager(context);
        return ourInstance;
    }
    private SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME,
                SHARED_PREF_PRIVATE);
        editor = sharedPreferences.edit();
    }
    public boolean writeString(String key, String value) {
        editor.putString(key, value);
        return editor.commit();
    }
    public String readString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }
    
    public boolean writeBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        return editor.commit();
    }
      public boolean readBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }
    
    public boolean writeLong(String key, long value) {
        editor.putLong(key, value);
        return editor.commit();
    }
    
    public long readLong(String key, long defaultValue) {
        return sharedPreferences.getLong(key, defaultValue);
    }
    
    // Methods for current user ID
    public boolean setCurrentUserId(long userId) {
        return writeLong(KEY_CURRENT_USER_ID, userId);
    }
    
    public long getCurrentUserId() {
        return readLong(KEY_CURRENT_USER_ID, -1); // -1 indicates no user logged in
    }
    
    public boolean clearCurrentUserId() {
        editor.remove(KEY_CURRENT_USER_ID);
        return editor.commit();
    }
      public boolean clearRememberMe() {
        editor.remove(KEY_REMEMBER_EMAIL);
        editor.remove(KEY_REMEMBER_PASSWORD);
        editor.remove(KEY_REMEMBER_ME);
        return editor.commit();
    }
    
    public boolean clearAll() {
        editor.clear();
        return editor.commit();
    }
    
    // Method to clear all user session data (including current user ID)
    public boolean logout() {
        editor.remove(KEY_CURRENT_USER_ID);
        editor.remove(KEY_REMEMBER_EMAIL);
        editor.remove(KEY_REMEMBER_PASSWORD);
        editor.remove(KEY_REMEMBER_ME);
        return editor.commit();
    }
}
