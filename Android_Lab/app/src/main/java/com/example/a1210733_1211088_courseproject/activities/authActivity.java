package com.example.a1210733_1211088_courseproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.a1210733_1211088_courseproject.R;
import com.example.a1210733_1211088_courseproject.auth.AuthPagerAdapter;
import com.example.a1210733_1211088_courseproject.auth.AuthenticationManager;
import com.example.a1210733_1211088_courseproject.database.DataBaseHelper;
import com.example.a1210733_1211088_courseproject.models.User;
import com.example.a1210733_1211088_courseproject.utils.SharedPrefManager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Interface for communication between fragments and activity
 */
interface AuthCallbackInterface {
    void onLoginRequested(String email, String password, boolean rememberMe);
    void onRegisterRequested(String email, String firstName, String lastName, 
                           String password, String confirmPassword, String gender, String country, String city, String phone);
}

public class authActivity extends AppCompatActivity implements AuthCallbackInterface {
      private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private AuthPagerAdapter authPagerAdapter;
    private AuthenticationManager authManager;
    private DataBaseHelper dbHelper;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);
          // Initialize authentication manager
        authManager = new AuthenticationManager(this);
          // Initialize database helper
        dbHelper = new DataBaseHelper(this, "RealEstate", null, 1);
        
        // Initialize shared preferences manager
        sharedPrefManager = SharedPrefManager.getInstance(this);
        
        // Check for auto-login
        checkAutoLogin();
        
        // Initialize views
        initializeViews();
        
        // Set up ViewPager and TabLayout
        setupViewPagerAndTabs();
        
        // Set up window insets
        View rootView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    
    private void initializeViews() {
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
    }
    
    private void setupViewPagerAndTabs() {
        // Set up ViewPager2 with adapter
        authPagerAdapter = new AuthPagerAdapter(this);
        viewPager.setAdapter(authPagerAdapter);
        
        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, 
            (tab, position) -> tab.setText(authPagerAdapter.getTabTitle(position))
        ).attach();
    }
      /**
     * Callback method for fragments to trigger authentication
     */
    @Override
    public void onLoginRequested(String email, String password, boolean rememberMe) {
        // Validate input
        if (email.isEmpty()) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (password.isEmpty()) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Attempt authentication
        User authenticatedUser = authManager.authenticateUser(email, password);
          if (authenticatedUser != null) {
            // Authentication successful
            Toast.makeText(this, "Login successful! Welcome " + authenticatedUser.getFirstName(), 
                          Toast.LENGTH_SHORT).show();
            
            // Handle remember me functionality
              if (rememberMe) {
                  // Save credentials
                  sharedPrefManager.writeString(SharedPrefManager.KEY_REMEMBER_EMAIL, email);
                  sharedPrefManager.writeString(SharedPrefManager.KEY_REMEMBER_PASSWORD, password);
                  sharedPrefManager.writeBoolean(SharedPrefManager.KEY_REMEMBER_ME, true);
              } else {
                  // Clear saved credentials
                  sharedPrefManager.clearRememberMe();
              }

            
            // Navigate based on user role
            navigateBasedOnRole(authenticatedUser);
        } else {
            // Authentication failed
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Callback method for fragments to trigger registration
     */    @Override
    public void onRegisterRequested(String email, String firstName, String lastName, 
                                  String password, String confirmPassword, String gender, String country, String city, String phone) {
        // Basic validation
        if (email.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }
          if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Check if user already exists
        if (dbHelper.isUserExists(email)) {
            Toast.makeText(this, "User with this email already exists", Toast.LENGTH_SHORT).show();
            return;
        }
          // Create new user object (default role is customer)
        User newUser = new User(email, password, firstName, lastName, gender, 
                               phone, country, city, "customer", null);
          // Insert user into database
        long userId = dbHelper.insertUser(newUser);
        
        if (userId != -1) {
            // Registration successful
            Toast.makeText(this, "Registration successful! Welcome " + firstName + "!", 
                          Toast.LENGTH_LONG).show();
            
            // Get the newly created user for auto-login
            User registeredUser = dbHelper.getUserById(userId);
            
            if (registeredUser != null) {
                // Automatically navigate to the appropriate activity based on role
                navigateBasedOnRole(registeredUser);
            } else {
                // Fallback: switch to login tab if we can't retrieve the user
                Toast.makeText(this, "Registration successful! Please login with your credentials.", 
                              Toast.LENGTH_SHORT).show();
                viewPager.setCurrentItem(0, true);
            }
        } else {
            // Registration failed
            Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void navigateBasedOnRole(User user) {
        if (authManager.isAdmin(user)) {
            // Navigate to admin dashboard
            Toast.makeText(this, "Admin login detected - redirecting to admin panel", 
                          Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, AdminActivity.class);
            intent.putExtra("user_id", user.getUserId());
            intent.putExtra("admin_name", user.getFirstName());
            startActivity(intent);
            finish();
        } else if (authManager.isCustomer(user)) {
            // Navigate to customer dashboard
            Toast.makeText(this, "Customer login detected - redirecting to home", 
                          Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("user_id", user.getUserId());
            startActivity(intent);
            finish();        } else {
            Toast.makeText(this, "Unknown user role", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Check if user credentials are saved and auto-login if remember me was checked
     */
    private void checkAutoLogin() {
        boolean rememberMe = sharedPrefManager.readBoolean(SharedPrefManager.KEY_REMEMBER_ME, false);
        
        if (rememberMe) {
            String savedEmail = sharedPrefManager.readString(SharedPrefManager.KEY_REMEMBER_EMAIL, "");
            String savedPassword = sharedPrefManager.readString(SharedPrefManager.KEY_REMEMBER_PASSWORD, "");
            
            if (!savedEmail.isEmpty() && !savedPassword.isEmpty()) {
                // Attempt auto-login
                User authenticatedUser = authManager.authenticateUser(savedEmail, savedPassword);
                
                if (authenticatedUser != null) {
                    // Auto-login successful
                    Toast.makeText(this, "Welcome back, " + authenticatedUser.getFirstName() + "!", 
                                  Toast.LENGTH_SHORT).show();
                    
                    // Navigate based on user role
                    navigateBasedOnRole(authenticatedUser);
                } else {
                    // Auto-login failed, clear saved credentials
                    sharedPrefManager.clearRememberMe();
                    Toast.makeText(this, "Auto-login failed. Please login again.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    
    /**
     *   remember me functionality - save or clear credentials
     */
//    private void handleRememberMe(String email, String password, boolean rememberMe) {
//        if (rememberMe) {
//            // Save credentials
//            sharedPrefManager.writeString(SharedPrefManager.KEY_REMEMBER_EMAIL, email);
//            sharedPrefManager.writeString(SharedPrefManager.KEY_REMEMBER_PASSWORD, password);
//            sharedPrefManager.writeBoolean(SharedPrefManager.KEY_REMEMBER_ME, true);
//        } else {
//            // Clear saved credentials
//            sharedPrefManager.clearRememberMe();
//        }
//    }
}