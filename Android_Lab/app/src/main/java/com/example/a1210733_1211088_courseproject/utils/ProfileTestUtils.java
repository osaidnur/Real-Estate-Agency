package com.example.a1210733_1211088_courseproject.utils;

import android.content.Context;
import android.util.Log;
import com.example.a1210733_1211088_courseproject.database.DataBaseHelper;
import com.example.a1210733_1211088_courseproject.models.User;

/**
 * Utility class to test profile management functionality
 * This class can be used to verify that profile updates work correctly
 */
public class ProfileTestUtils {
    private static final String TAG = "ProfileTestUtils";
    
    /**
     * Test profile update functionality
     * @param context Application context
     * @param userId User ID to test with
     * @return true if all tests pass, false otherwise
     */
    public static boolean testProfileUpdate(Context context, long userId) {
        DataBaseHelper dbHelper = new DataBaseHelper(context, "RealEstate", null, 1);
        
        try {
            // Get original user data
            User originalUser = dbHelper.getUserById(userId);
            if (originalUser == null) {
                Log.e(TAG, "User not found for ID: " + userId);
                return false;
            }
            
            // Test profile update
            String testFirstName = "TestFirst_" + System.currentTimeMillis();
            String testLastName = "TestLast_" + System.currentTimeMillis();
            String testPhone = "+1234567890";
            String testPhoto = "content://test/photo";
            
            boolean updateSuccess = dbHelper.updateUserProfile(
                userId, testFirstName, testLastName, testPhone, testPhoto
            );
            
            if (!updateSuccess) {
                Log.e(TAG, "Profile update failed");
                return false;
            }
            
            // Verify update
            User updatedUser = dbHelper.getUserById(userId);
            if (updatedUser == null) {
                Log.e(TAG, "Could not retrieve updated user");
                return false;
            }
            
            boolean profileTestPassed = testFirstName.equals(updatedUser.getFirstName()) &&
                                       testLastName.equals(updatedUser.getLastName()) &&
                                       testPhone.equals(updatedUser.getPhone()) &&
                                       testPhoto.equals(updatedUser.getProfilePhoto());
            
            if (!profileTestPassed) {
                Log.e(TAG, "Profile data verification failed");
                return false;
            }
            
            // Restore original data
            dbHelper.updateUserProfile(
                userId, 
                originalUser.getFirstName(), 
                originalUser.getLastName(), 
                originalUser.getPhone(), 
                originalUser.getProfilePhoto()
            );
            
            Log.d(TAG, "Profile update test passed successfully");
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Profile test failed with exception: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Test password update functionality
     * @param context Application context
     * @param userId User ID to test with
     * @param currentPassword Current password
     * @return true if test passes, false otherwise
     */
    public static boolean testPasswordUpdate(Context context, long userId, String currentPassword) {
        DataBaseHelper dbHelper = new DataBaseHelper(context, "RealEstate", null, 1);
        
        try {
            String testPassword = "TestPass123!";
            
            // Test password update
            boolean updateSuccess = dbHelper.updateUserPassword(userId, currentPassword, testPassword);
            
            if (!updateSuccess) {
                Log.e(TAG, "Password update failed");
                return false;
            }
            
            // Restore original password
            dbHelper.updateUserPassword(userId, testPassword, currentPassword);
            
            Log.d(TAG, "Password update test passed successfully");
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "Password test failed with exception: " + e.getMessage());
            return false;
        }
    }
}
