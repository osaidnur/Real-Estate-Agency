package com.example.a1210733_1211088_courseproject.test;

import android.content.Context;
import android.util.Log;
import com.example.a1210733_1211088_courseproject.database.DataBaseHelper;

import java.util.List;
import java.util.Map;

/**
 * Test utility class to verify the enhanced dashboard functionality
 */
public class DashboardTest {
    
    private static final String TAG = "DashboardTest";
    
    /**
     * Test the dashboard statistics functionality
     * @param context Application context
     */
    public static void testDashboardStats(Context context) {
        try {
            DataBaseHelper dbHelper = new DataBaseHelper(context, "RealEstate", null, 1);
            
            Log.d(TAG, "=== Testing Enhanced Dashboard Statistics ===");
            
            // Test comprehensive dashboard stats
            DataBaseHelper.DashboardStats stats = dbHelper.getDashboardStats();
              // Log main statistics
            Log.d(TAG, "📊 Main Statistics:");
            Log.d(TAG, "   Total Users: " + stats.totalUsers);
            Log.d(TAG, "   Total Customers: " + stats.totalCustomers);
            Log.d(TAG, "   Total Properties: " + stats.totalProperties);
            Log.d(TAG, "   Total Reservations: " + stats.totalReservations);
            Log.d(TAG, "   Reserved Properties: " + stats.reservedProperties);
              // Log gender distribution
            Log.d(TAG, "👫 Gender Distribution:");
            Map<String, Integer> genderData = stats.genderDistribution;
            for (Map.Entry<String, Integer> entry : genderData.entrySet()) {
                Log.d(TAG, "   " + entry.getKey() + ": " + entry.getValue());
            }
            
            // Log top countries
            Log.d(TAG, "🌍 Customer Countries:");
            Map<String, Integer> countries = stats.customersByCountry;
            for (Map.Entry<String, Integer> entry : countries.entrySet()) {
                Log.d(TAG, "   " + entry.getKey() + ": " + entry.getValue() + " customers");
            }
            
            // Log reservation status
            Log.d(TAG, "📋 Reservation Status:");
            List<DataBaseHelper.DashboardStats.ReservationStatusInfo> reservations = stats.reservationStatus;
            for (DataBaseHelper.DashboardStats.ReservationStatusInfo info : reservations) {
                Log.d(TAG, "   " + info.status + ": " + info.count);
            }
            
            // Log property status
            Log.d(TAG, "🏘️ Property Status:");
            List<DataBaseHelper.DashboardStats.PropertyStatusInfo> properties = stats.propertyStatus;
            for (DataBaseHelper.DashboardStats.PropertyStatusInfo info : properties) {
                Log.d(TAG, "   " + info.status + ": " + info.count);
            }
            
            Log.d(TAG, "✅ Dashboard statistics test completed successfully!");
            
            dbHelper.close();
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Dashboard statistics test failed", e);
        }
    }
    
    /**
     * Test individual dashboard methods for debugging
     * @param context Application context
     */
    public static void testIndividualMethods(Context context) {
        try {
            DataBaseHelper dbHelper = new DataBaseHelper(context, "RealEstate", null, 1);
            
            Log.d(TAG, "=== Testing Individual Dashboard Methods ===");
            
            // Test individual count methods
            int totalUsers = dbHelper.getTotalUsersCount();
            int totalCustomers = dbHelper.getTotalCustomersCount();
            int totalProperties = dbHelper.getTotalPropertiesCount();
            int totalReservations = dbHelper.getTotalReservationsCount();
            int reservedProperties = dbHelper.getReservedPropertiesCount();
            
            Log.d(TAG, "Individual method results:");
            Log.d(TAG, "   getTotalUsersCount(): " + totalUsers);
            Log.d(TAG, "   getTotalCustomersCount(): " + totalCustomers);
            Log.d(TAG, "   getTotalPropertiesCount(): " + totalProperties);
            Log.d(TAG, "   getTotalReservationsCount(): " + totalReservations);
            Log.d(TAG, "   getReservedPropertiesCount(): " + reservedProperties);
            
            dbHelper.close();
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Individual methods test failed", e);
        }
    }
}
