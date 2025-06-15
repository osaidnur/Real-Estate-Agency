package com.example.a1210733_1211088_courseproject.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.a1210733_1211088_courseproject.R;
import com.example.a1210733_1211088_courseproject.database.DataBaseHelper;
import com.example.a1210733_1211088_courseproject.database.sql.PropertyQueries;
import com.example.a1210733_1211088_courseproject.database.sql.UserQueries;
import com.example.a1210733_1211088_courseproject.fragments.AdminCustomersFragment;
import com.example.a1210733_1211088_courseproject.fragments.AdminDashboardFragment;
import com.example.a1210733_1211088_courseproject.fragments.AdminPropertiesFragment;
import com.example.a1210733_1211088_courseproject.fragments.AdminReservationsFragment;
import com.example.a1210733_1211088_courseproject.fragments.AdminSpecialOffersFragment;
import com.example.a1210733_1211088_courseproject.fragments.AdminsFragment;
import com.example.a1210733_1211088_courseproject.models.User;
import com.example.a1210733_1211088_courseproject.utils.SharedPrefManager;
import com.google.android.material.navigation.NavigationView;

public class AdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    
    private static final String TAG = "AdminActivity";
    
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private DataBaseHelper dbHelper;
    private SharedPrefManager sharedPrefManager;
    private long userId;
    private String adminName;    @Override
    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_admin_drawer);

        // Force set status bar color more aggressively
        setupStatusBar();
        
        // Initialize database helper
        dbHelper = new DataBaseHelper(this, "RealEstate", null, 1);
        
        // Initialize shared preferences manager
        sharedPrefManager = SharedPrefManager.getInstance(this);

        // Get user info from intent
        userId = getIntent().getLongExtra("user_id", -1);
        adminName = getIntent().getStringExtra("admin_name");

        // Validate admin access
        if (userId == -1) {
            Toast.makeText(this, "Invalid session. Please login again.", Toast.LENGTH_LONG).show();
            logout();
            return;
        }

        if (adminName == null || adminName.isEmpty()) {
            adminName = "Admin";
        }

        // Setup UI components
        setupUI();

        // Set default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.admin_fragment_container, new AdminDashboardFragment())
                .commit();
            navigationView.setCheckedItem(R.id.admin_nav_dashboard);
        }

        // Update navigation header
        updateNavigationHeader();

        Log.d(TAG, "AdminActivity started for user ID: " + userId + ", name: " + adminName);
    }    private void setupUI() {
        // Initialize toolbar
        toolbar = findViewById(R.id.admin_toolbar);
        setSupportActionBar(toolbar);

        // Initialize drawer layout
        drawerLayout = findViewById(R.id.admin_drawer_layout);
        navigationView = findViewById(R.id.admin_nav_view);

        // Set up navigation item selection listener
        navigationView.setNavigationItemSelectedListener(this);

        // Set up the drawer toggle
        toggle = new ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        );
        
        // Set navigation drawer toggle color to palette_gold
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.palette_white, getTheme()));
        
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }    private void updateNavigationHeader() {
        // Update navigation header with admin info
        android.view.View headerView = navigationView.getHeaderView(0);
        TextView adminNameView = headerView.findViewById(R.id.admin_nav_user_name);
        TextView adminEmailView = headerView.findViewById(R.id.admin_nav_user_email);
        
        if (adminNameView != null) {
            adminNameView.setText("Welcome, " + adminName);
        }
        
        if (adminEmailView != null) {
            // Get the current admin's information from database
            User currentAdmin = dbHelper.getUserById(userId);
            if (currentAdmin != null) {
                adminEmailView.setText(currentAdmin.getEmail());
            } else {
                adminEmailView.setText("admin@admin.com"); // fallback
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks
        int id = item.getItemId();
        Fragment selectedFragment = null;        if (id == R.id.admin_nav_dashboard) {
            selectedFragment = new AdminDashboardFragment();
        } else if (id == R.id.admin_nav_view_customers) {
            selectedFragment = new AdminCustomersFragment();        } else if (id == R.id.admin_nav_view_admins) {
            selectedFragment = new AdminsFragment();
            // Pass current admin's user ID to prevent self-deletion
            Bundle args = new Bundle();
            args.putLong("current_admin_id", userId);
            selectedFragment.setArguments(args);        } else if (id == R.id.admin_nav_view_properties) {
            selectedFragment = new AdminPropertiesFragment();
        } else if (id == R.id.admin_nav_view_special_offers) {
            selectedFragment = new AdminSpecialOffersFragment();
        } else if (id == R.id.admin_nav_view_reservations) {
            selectedFragment = new AdminReservationsFragment();
        } else if (id == R.id.admin_nav_logout) {
            logout();
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.admin_fragment_container, selectedFragment)
                .commit();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        // Close drawer if open when back button is pressed
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        
        // Clear user session data including current user ID
        if (sharedPrefManager != null) {
            sharedPrefManager.logout();
        }
        
        // Navigate back to authentication
        Intent intent = new Intent(this, authActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }    private void setupStatusBar() {
        Window window = getWindow();
        
        // Enable drawing under status bar
        window.getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        
        // Make status bar translucent first
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        
        // Set to palette1 color (#183B4E)
        int statusBarColor = 0xFF183B4E; // palette1 color
        window.setStatusBarColor(statusBarColor);
        
        // Also try alternative method
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(0xFF183B4E);
        }
        
        // Ensure status bar text is white (since we're using a dark color)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }    @Override
    protected void onResume() {
        super.onResume();
        // Force status bar color again when activity resumes
        setupStatusBar();
        // Update navigation header with current admin email
        updateNavigationHeader();
    }
}
