package com.example.a1210733_1211088_courseproject.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1210733_1211088_courseproject.R;
import com.example.a1210733_1211088_courseproject.database.DataBaseHelper;
import com.example.a1210733_1211088_courseproject.database.sql.PropertyQueries;
import com.example.a1210733_1211088_courseproject.database.sql.UserQueries;

public class AdminActivity extends AppCompatActivity {
    
    private static final String TAG = "AdminActivity";
    
    private TextView tvWelcomeAdmin, tvPropertiesCount, tvUsersCount;
    private Button btnManageProperties, btnManageUsers, btnViewReports, btnLogout;
    private RecyclerView rvRecentActivities;
    
    private DataBaseHelper dbHelper;
    private long userId;
    private String adminName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
//        // Initialize database helper
//        dbHelper = new DataBaseHelper(this, "RealEstate", null, 1);
//
//        // Get user info from intent
//        userId = getIntent().getLongExtra("user_id", -1);
//        adminName = getIntent().getStringExtra("admin_name");
//
//        // Validate admin access
//        if (userId == -1) {
//            Toast.makeText(this, "Invalid session. Please login again.", Toast.LENGTH_LONG).show();
//            logout();
//            return;
//        }
//
//        if (adminName == null || adminName.isEmpty()) {
//            adminName = "Admin";
//        }
//
//        // Initialize views
//        initializeViews();
//
//        // Set up window insets
//        View rootView = findViewById(android.R.id.content);
//        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//
//        // Set up click listeners
//        setupClickListeners();
//
//        // Load dashboard data
//        loadDashboardData();
//
//        Log.d(TAG, "AdminActivity started for user ID: " + userId + ", name: " + adminName);
    }
    
    private void initializeViews() {
//        tvWelcomeAdmin = findViewById(R.id.tv_welcome_admin);
//        tvPropertiesCount = findViewById(R.id.tv_properties_count);
//        tvUsersCount = findViewById(R.id.tv_users_count);
//        btnManageProperties = findViewById(R.id.btn_manage_properties);
//        btnManageUsers = findViewById(R.id.btn_manage_users);
//        btnViewReports = findViewById(R.id.btn_view_reports);
//        btnLogout = findViewById(R.id.btn_logout);
//        rvRecentActivities = findViewById(R.id.rv_recent_activities);
        
        // Set welcome message
        tvWelcomeAdmin.setText("Welcome, " + adminName);
    }
    
    private void setupClickListeners() {
        btnManageProperties.setOnClickListener(v -> manageProperties());
        btnManageUsers.setOnClickListener(v -> manageUsers());
        btnViewReports.setOnClickListener(v -> viewReports());
        btnLogout.setOnClickListener(v -> logout());
    }
    
    private void loadDashboardData() {
        // Load property count
        loadPropertiesCount();
        
        // Load users count
        loadUsersCount();
        
        // Load recent activities (placeholder for now)
        setupRecentActivities();
    }
    
    private void loadPropertiesCount() {
        try {
            Cursor cursor = dbHelper.getAllProperties();
            int count = (cursor != null) ? cursor.getCount() : 0;
            tvPropertiesCount.setText(String.valueOf(count));
            
            if (cursor != null) {
                cursor.close();
            }
            
            Log.d(TAG, "Properties count loaded: " + count);
        } catch (Exception e) {
            Log.e(TAG, "Error loading properties count", e);
            tvPropertiesCount.setText("0");
        }
    }
    
    private void loadUsersCount() {
        try {
            Cursor cursor = dbHelper.getAllUsers();
            int count = (cursor != null) ? cursor.getCount() : 0;
            tvUsersCount.setText(String.valueOf(count));
            
            if (cursor != null) {
                cursor.close();
            }
            
            Log.d(TAG, "Users count loaded: " + count);
        } catch (Exception e) {
            Log.e(TAG, "Error loading users count", e);
            tvUsersCount.setText("0");
        }
    }
    
    private void setupRecentActivities() {
        // Set up RecyclerView for recent activities
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvRecentActivities.setLayoutManager(layoutManager);
        
        // TODO: Create adapter for recent activities
        // For now, we'll leave this as a placeholder
    }
    
    private void manageProperties() {
        Toast.makeText(this, "Property management coming soon!", Toast.LENGTH_SHORT).show();
        
        // Log all properties for admin review
        try {
            Cursor cursor = dbHelper.getAllProperties();
            
            Log.d(TAG, "==================================================");
            Log.d(TAG, "               PROPERTIES MANAGEMENT              ");
            Log.d(TAG, "==================================================");
            
            if (cursor == null || cursor.getCount() == 0) {
                Log.d(TAG, "No properties found in the database.");
            } else {
                Log.d(TAG, "Total properties: " + cursor.getCount());
                
                while (cursor.moveToNext()) {
                    @SuppressLint("Range") 
                    int propertyId = cursor.getInt(cursor.getColumnIndex(PropertyQueries.COLUMN_PROPERTY_ID));
                    @SuppressLint("Range") 
                    String title = cursor.getString(cursor.getColumnIndex(PropertyQueries.COLUMN_TITLE));
                    @SuppressLint("Range") 
                    String type = cursor.getString(cursor.getColumnIndex(PropertyQueries.COLUMN_TYPE));
                    @SuppressLint("Range") 
                    double price = cursor.getDouble(cursor.getColumnIndex(PropertyQueries.COLUMN_PRICE));
                    @SuppressLint("Range") 
                    String city = cursor.getString(cursor.getColumnIndex(PropertyQueries.COLUMN_CITY));
                    
                    Log.d(TAG, String.format("ID: %d | %s | %s | $%.2f | %s", 
                          propertyId, title, type, price, city));
                }
                
                cursor.close();
            }
            
            Log.d(TAG, "==================================================");
        } catch (Exception e) {
            Log.e(TAG, "Error in property management", e);
        }
    }
    
    private void manageUsers() {
        Toast.makeText(this, "User management coming soon!", Toast.LENGTH_SHORT).show();
        
        // Log all users for admin review
        try {
            Cursor cursor = dbHelper.getAllUsers();
            
            Log.d(TAG, "==================================================");
            Log.d(TAG, "                 USER MANAGEMENT                  ");
            Log.d(TAG, "==================================================");
            
            if (cursor == null || cursor.getCount() == 0) {
                Log.d(TAG, "No users found in the database.");
            } else {
                Log.d(TAG, "Total users: " + cursor.getCount());
                
                while (cursor.moveToNext()) {
                    @SuppressLint("Range") 
                    long userIdVal = cursor.getLong(cursor.getColumnIndex(UserQueries.COLUMN_USER_ID));
                    @SuppressLint("Range") 
                    String email = cursor.getString(cursor.getColumnIndex(UserQueries.COLUMN_EMAIL));
                    @SuppressLint("Range") 
                    String firstName = cursor.getString(cursor.getColumnIndex(UserQueries.COLUMN_FIRST_NAME));
                    @SuppressLint("Range") 
                    String lastName = cursor.getString(cursor.getColumnIndex(UserQueries.COLUMN_LAST_NAME));
                    @SuppressLint("Range") 
                    String role = cursor.getString(cursor.getColumnIndex(UserQueries.COLUMN_ROLE));
                    
                    Log.d(TAG, String.format("ID: %d | %s | %s %s | %s", 
                          userIdVal, email, firstName, lastName, role));

                }
                
                cursor.close();
            }
            
            Log.d(TAG, "==================================================");
        } catch (Exception e) {
            Log.e(TAG, "Error in user management", e);
        }
    }
    
    private void viewReports() {
        Toast.makeText(this, "Reports functionality coming soon!", Toast.LENGTH_SHORT).show();
        
        // Generate basic reports
        try {
            Log.d(TAG, "==================================================");
            Log.d(TAG, "                   ADMIN REPORTS                 ");
            Log.d(TAG, "==================================================");
            
            // Property statistics
            Cursor propertyCursor = dbHelper.getAllProperties();
            int totalProperties = (propertyCursor != null) ? propertyCursor.getCount() : 0;
            
            // User statistics
            Cursor userCursor = dbHelper.getAllUsers();
            int totalUsers = (userCursor != null) ? userCursor.getCount() : 0;
            
            Log.d(TAG, "Total Properties: " + totalProperties);
            Log.d(TAG, "Total Users: " + totalUsers);
            Log.d(TAG, "Admin User ID: " + userId);
            Log.d(TAG, "==================================================");
            
            if (propertyCursor != null) propertyCursor.close();
            if (userCursor != null) userCursor.close();
            
        } catch (Exception e) {
            Log.e(TAG, "Error generating reports", e);
        }
    }
    
    private void logout() {
        Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
        
        // Navigate back to authentication
        Intent intent = new Intent(this, authActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
