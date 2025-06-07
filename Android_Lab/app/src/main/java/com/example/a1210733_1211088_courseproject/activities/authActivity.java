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
import com.example.a1210733_1211088_courseproject.models.User;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Interface for communication between fragments and activity
 */
interface AuthCallbackInterface {
    void onLoginRequested(String email, String password);
    void onRegisterRequested(String email, String firstName, String lastName, 
                           String password, String confirmPassword, String gender, String country);
}

public class authActivity extends AppCompatActivity implements AuthCallbackInterface {
    
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private AuthPagerAdapter authPagerAdapter;
    private AuthenticationManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_auth);
        
        // Initialize authentication manager
        authManager = new AuthenticationManager(this);
        
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
    public void onLoginRequested(String email, String password) {
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
            
            // Navigate based on user role
            navigateBasedOnRole(authenticatedUser);
        } else {
            // Authentication failed
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Callback method for fragments to trigger registration
     */
    @Override
    public void onRegisterRequested(String email, String firstName, String lastName, 
                                  String password, String confirmPassword, String gender, String country) {
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
        
        // TODO: Implement user registration in database
        Toast.makeText(this, "Registration functionality coming soon!", Toast.LENGTH_LONG).show();
        
        // For now, switch to login tab after showing the message
        viewPager.setCurrentItem(0, true);
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
            finish();
        } else {
            Toast.makeText(this, "Unknown user role", Toast.LENGTH_SHORT).show();
        }
    }
}