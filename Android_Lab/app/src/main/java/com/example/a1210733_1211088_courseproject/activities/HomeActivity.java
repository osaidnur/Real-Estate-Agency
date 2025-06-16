package com.example.a1210733_1211088_courseproject.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
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

        // Force set status bar color more aggressively
        setupStatusBar();

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
        
        // Set navigation drawer toggle color to palette_gold
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.palette_white, getTheme()));
        
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Update the navigation header with the current user's email
        updateNavigationHeader();
    }    /**
     * Updates the navigation header with the current user's profile information
     */
    private void updateNavigationHeader() {
        // Get current user ID from shared preferences
        long userId = prefManager.getCurrentUserId();

        if (userId != -1) {
            // Get the user object from the database
            User currentUser = dbHelper.getUserById(userId);

            if (currentUser != null) {
                // Find the views in the navigation header
                View headerView = navigationView.getHeaderView(0);
                TextView userNameTextView = headerView.findViewById(R.id.nav_user_name);
                TextView userEmailTextView = headerView.findViewById(R.id.nav_user_email);
                ImageView userProfilePhoto = headerView.findViewById(R.id.nav_user_profile_photo);

                // Set the user's full name
                String fullName = currentUser.getFirstName() + " " + currentUser.getLastName();
                userNameTextView.setText(fullName);

                // Set the email text
                userEmailTextView.setText(currentUser.getEmail());                // Handle profile photo
                String profilePhotoPath = currentUser.getProfilePhoto();
                if (profilePhotoPath != null && !profilePhotoPath.isEmpty()) {
                    // Try to load the profile photo
                    try {
                        Bitmap bitmap = BitmapFactory.decodeFile(profilePhotoPath);
                        if (bitmap != null) {
                            // Clear any default styling and load the real photo
                            userProfilePhoto.setBackground(null);
                            userProfilePhoto.setPadding(0, 0, 0, 0);
                            userProfilePhoto.clearColorFilter();
                            userProfilePhoto.setImageBitmap(bitmap);
                            userProfilePhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            // Debug log
                            System.out.println("Successfully loaded profile photo from: " + profilePhotoPath);
                        } else {
                            // Use default icon if bitmap couldn't be loaded
                            System.out.println("Failed to decode bitmap from: " + profilePhotoPath);
                            setDefaultProfilePhoto(userProfilePhoto);
                        }
                    } catch (Exception e) {
                        // Use default icon if there's an error loading the image
                        System.out.println("Error loading profile photo: " + e.getMessage());
                        setDefaultProfilePhoto(userProfilePhoto);
                    }
                } else {
                    // Use default icon if no profile photo path
                    System.out.println("No profile photo path found, using default");
                    setDefaultProfilePhoto(userProfilePhoto);
                }
            }
        }
    }    /**
     * Sets the default profile photo icon with proper styling
     */
    private void setDefaultProfilePhoto(ImageView profilePhotoView) {
        // Clear any existing image and styling
        profilePhotoView.setImageDrawable(null);
        profilePhotoView.clearColorFilter();
        
        // Set default icon and styling
        profilePhotoView.setImageResource(R.drawable.ic_person);
        profilePhotoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        profilePhotoView.setBackground(getResources().getDrawable(R.drawable.profile_photo_background));
        profilePhotoView.setPadding(20, 20, 20, 20);
        profilePhotoView.setColorFilter(getResources().getColor(android.R.color.white));
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
            selectedFragment = new FeaturedFragment();        } else if (id == R.id.nav_profile) {
            try {
                selectedFragment = new ProfileFragment();
            } catch (Exception e) {
                android.util.Log.e("HomeActivity", "Error creating ProfileFragment", e);
                Toast.makeText(this, "Error opening profile. Please try again.", Toast.LENGTH_SHORT).show();
                return true; // Don't proceed with fragment transaction
            }
        } else if (id == R.id.nav_contact) {
            selectedFragment = new ContactFragment();
        } else if (id == R.id.nav_logout) {
            logoutUser();
        }        if (selectedFragment != null) {
            try {
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.home_fragment_container, selectedFragment)
                    .commit();
            } catch (Exception e) {
                android.util.Log.e("HomeActivity", "Error replacing fragment", e);
                Toast.makeText(this, "Error navigating. Please try again.", Toast.LENGTH_SHORT).show();
            }
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
    }    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Force status bar color again when activity resumes
        setupStatusBar();
    }
    
    private void setupStatusBar() {
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
}
