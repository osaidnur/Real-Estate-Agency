package com.example.a1210733_1211088_courseproject.activities;

import android.os.Bundle;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.a1210733_1211088_courseproject.R;
import com.example.a1210733_1211088_courseproject.database.DataBaseHelper;
import com.example.a1210733_1211088_courseproject.fragments.ContactFragment;
import com.example.a1210733_1211088_courseproject.fragments.FavoritesFragment;
import com.example.a1210733_1211088_courseproject.fragments.FeaturedFragment;
import com.example.a1210733_1211088_courseproject.fragments.HomeFragment;
import com.example.a1210733_1211088_courseproject.fragments.ProfileFragment;
import com.example.a1210733_1211088_courseproject.fragments.ReservationsFragment;
import com.example.a1210733_1211088_courseproject.fragments.PropertiesFragment;
import com.example.a1210733_1211088_courseproject.models.User;
import com.example.a1210733_1211088_courseproject.utils.SharedPrefManager;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private DataBaseHelper dbHelper;
    private SharedPrefManager prefManager;    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_home);

        // Initialize database helper
        dbHelper = new DataBaseHelper(this, "RealEstate", null, 1);

        // Initialize shared preferences manager using getInstance
        prefManager = SharedPrefManager.getInstance(this);

        // Setup UI components
        setupUI();

        // Set default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_fragment_container, new HomeFragment())
                .commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    private void setupUI() {
        // Initialize toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize drawer layout
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Set up navigation item selection listener
        navigationView.setNavigationItemSelectedListener(this);

        // Set up the drawer toggle
        toggle = new ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Update the navigation header with the current user's email
        updateNavigationHeader();
    }

    /**
     * Updates the navigation header with the current user's email
     */
    private void updateNavigationHeader() {
        // Get current user ID from shared preferences
        long userId = prefManager.getCurrentUserId();

        if (userId != -1) {
            // Get the user object from the database
            User currentUser = dbHelper.getUserById(userId);

            if (currentUser != null) {
                // Find the email TextView in the navigation header and update it
                View headerView = navigationView.getHeaderView(0);
                TextView userEmailTextView = headerView.findViewById(R.id.nav_user_email);

                // Set the email text
                userEmailTextView.setText(currentUser.getEmail());
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks
        int id = item.getItemId();
        Fragment selectedFragment = null;

        if (id == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        } else if (id == R.id.nav_properties) {
            selectedFragment = new PropertiesFragment();
        } else if (id == R.id.nav_reservations) {
            selectedFragment = new ReservationsFragment();
        } else if (id == R.id.nav_favorites) {
            selectedFragment = new FavoritesFragment();
        } else if (id == R.id.nav_featured) {
            selectedFragment = new FeaturedFragment();
        } else if (id == R.id.nav_profile) {
            selectedFragment = new ProfileFragment();
        } else if (id == R.id.nav_contact) {
            selectedFragment = new ContactFragment();
        } else if (id == R.id.nav_logout) {
            logoutUser();
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_fragment_container, selectedFragment)
                .commit();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }    private void logoutUser() {
        // Clear user session data including current user ID
        prefManager.logout();

        // Show logout message
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();

        // Navigate to login screen (assuming MainActivity is the login screen)
        Intent intent = new Intent(this, authActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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
}
