package com.example.a1210733_1211088_courseproject.auth;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.a1210733_1211088_courseproject.database.DataBaseHelper;
import com.example.a1210733_1211088_courseproject.database.sql.UserQueries;
import com.example.a1210733_1211088_courseproject.models.User;
import com.example.a1210733_1211088_courseproject.utils.PasswordUtils;

/**
 * Authentication manager for handling user login and authentication
 */
public class AuthenticationManager {
    private static final String TAG = "AuthManager";
    private DataBaseHelper dbHelper;
    private Context context;
    
    public AuthenticationManager(Context context) {
        this.context = context;
        this.dbHelper = new DataBaseHelper(context, "RealEstate", null, 1);
    }
    
    /**
     * Authenticates a user with email and password
     * @param email User's email
     * @param password User's plain text password
     * @return User object if authentication successful, null otherwise
     */
    public User authenticateUser(String email, String password) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(UserQueries.GET_USER_BY_EMAIL, new String[]{email});
            
            if (cursor != null && cursor.moveToFirst()) {
                // Get user data from cursor
                long userId = cursor.getLong(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_USER_ID));
                String storedPassword = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_PASSWORD));
                String firstName = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_FIRST_NAME));
                String lastName = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_LAST_NAME));
                String role = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_ROLE));
                String gender = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_GENDER));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_PHONE));
                String country = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_COUNTRY));
                String city = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_CITY));
                String profilePhoto = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_PROFILE_PHOTO));
                
                // Verify password
                // First try simple hash (for compatibility with existing admin account)
                String hashedInputPassword = PasswordUtils.hashPasswordSimple(password);
                boolean isPasswordValid = false;
                
                if (hashedInputPassword != null && hashedInputPassword.equals(storedPassword)) {
                    isPasswordValid = true;
                } else {
                    // Try plain text comparison (for existing data)
                    isPasswordValid = password.equals(storedPassword);
                }
                
                if (isPasswordValid) {
                    Log.d(TAG, "Authentication successful for user: " + email);
                    return new User(userId, email, null, firstName, lastName, gender, 
                                  phone, country, city, role, profilePhoto);
                } else {
                    Log.d(TAG, "Invalid password for user: " + email);
                    return null;
                }
            } else {
                Log.d(TAG, "User not found: " + email);
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during authentication", e);
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    
    /**
     * Checks if a user is an admin
     * @param user The user to check
     * @return true if user is admin, false otherwise
     */
    public boolean isAdmin(User user) {
        return user != null && "admin".equalsIgnoreCase(user.getRole());
    }
    
    /**
     * Checks if a user is a regular user/customer
     * @param user The user to check
     * @return true if user is customer, false otherwise
     */
    public boolean isCustomer(User user) {
        return user != null && "customer".equalsIgnoreCase(user.getRole());
    }
}
