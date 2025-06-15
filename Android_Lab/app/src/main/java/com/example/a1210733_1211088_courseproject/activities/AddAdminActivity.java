package com.example.a1210733_1211088_courseproject.activities;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a1210733_1211088_courseproject.R;
import com.example.a1210733_1211088_courseproject.database.DataBaseHelper;
import com.example.a1210733_1211088_courseproject.models.User;

import java.util.HashMap;
import java.util.Map;

public class AddAdminActivity extends AppCompatActivity {

    private EditText emailEditText, firstNameEditText, lastNameEditText;
    private EditText passwordEditText, confirmPasswordEditText, phoneEditText;
    private Spinner genderSpinner, countrySpinner, citySpinner;
    private Button addAdminButton;
    private DataBaseHelper dbHelper;
    
    // Country-City mapping (same as in registration)
    private Map<String, String[]> countryCityMap;    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_admin);        // Setup status bar like AdminActivity
        setupStatusBar();
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_admin_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize database helper
        dbHelper = new DataBaseHelper(this, "RealEstate", null, 1);

        // Setup toolbar
        setupToolbar();

        // Initialize views
        initializeViews();

        // Initialize country and city data
        initializeCountryCityData();

        // Setup spinners
        setupSpinners();

        // Setup add admin button
        setupAddAdminButton();
    }    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_add_admin);
        setSupportActionBar(toolbar);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add New Admin");
            
            // Set the back arrow color to palette_gold
            toolbar.getNavigationIcon().setTint(getResources().getColor(R.color.palette_white, getTheme()));
        }
        
        // Apply Olivera font to toolbar title
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                Typeface olivera = ResourcesCompat.getFont(this, R.font.olivera_font_family);
                textView.setTypeface(olivera);
                textView.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
            }
        }
    }

    private void initializeViews() {
        emailEditText = findViewById(R.id.add_admin_email);
        firstNameEditText = findViewById(R.id.add_admin_fname);
        lastNameEditText = findViewById(R.id.add_admin_lname);
        passwordEditText = findViewById(R.id.add_admin_password);
        confirmPasswordEditText = findViewById(R.id.add_admin_confirm);
        genderSpinner = findViewById(R.id.add_admin_gender);
        countrySpinner = findViewById(R.id.add_admin_country);
        citySpinner = findViewById(R.id.add_admin_city);
        phoneEditText = findViewById(R.id.add_admin_phone);
        addAdminButton = findViewById(R.id.btn_add_admin);
    }

    private void initializeCountryCityData() {
        countryCityMap = new HashMap<>();
        countryCityMap.put("Palestine", new String[]{"Gaza", "Hebron", "Ramallah", "Nablus", "Bethlehem", "Jenin"});
        countryCityMap.put("Jordan", new String[]{"Amman", "Zarqa", "Irbid", "Aqaba", "Salt", "Madaba"});
        countryCityMap.put("Syria", new String[]{"Damascus", "Aleppo", "Homs", "Latakia", "Hama", "Deir ez-Zor"});
    }

    private void setupSpinners() {
        // Gender spinner
        String[] genders = {"Select Gender", "Male", "Female"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        // Country spinner
        String[] countries = {"Select Country", "Palestine", "Jordan", "Syria"};
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, countries);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(countryAdapter);

        // Set up country selection listener
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCountry = parent.getItemAtPosition(position).toString();
                updateCitySpinner(selectedCountry);
                updatePhoneCountryCode(selectedCountry);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                updateCitySpinner("Select Country");
            }
        });

        // Initialize city spinner with default values
        updateCitySpinner("Select Country");
    }

    private void updateCitySpinner(String selectedCountry) {
        String[] cities;
        
        if (countryCityMap.containsKey(selectedCountry)) {
            cities = countryCityMap.get(selectedCountry);
        } else {
            cities = new String[]{"Select City"};
        }
        
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, 
                android.R.layout.simple_spinner_item, cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);
    }

    private void updatePhoneCountryCode(String selectedCountry) {
        String countryCode = "";
        switch (selectedCountry) {
            case "Palestine":
                countryCode = "+970 ";
                break;
            case "Jordan":
                countryCode = "+962 ";
                break;
            case "Syria":
                countryCode = "+963 ";
                break;
        }
        
        if (!countryCode.isEmpty() && !phoneEditText.getText().toString().startsWith("+")) {
            phoneEditText.setText(countryCode);
            phoneEditText.setSelection(phoneEditText.getText().length());
        }
    }

    private void setupAddAdminButton() {
        addAdminButton.setOnClickListener(v -> {
            if (validateForm()) {
                createAdmin();
            }
        });
    }

    private boolean validateForm() {
        String email = emailEditText.getText().toString().trim();
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String gender = genderSpinner.getSelectedItem().toString();
        String country = countrySpinner.getSelectedItem().toString();
        String city = citySpinner.getSelectedItem().toString();
        String phone = phoneEditText.getText().toString().trim();

        // Basic validation
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email");
            emailEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(firstName)) {
            firstNameEditText.setError("First name is required");
            firstNameEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(lastName)) {
            lastNameEditText.setError("Last name is required");
            lastNameEditText.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            confirmPasswordEditText.requestFocus();
            return false;
        }

        if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&^()_+=\\-{}\\[\\]:;\"'<>,./~`|\\\\]).+$")) {
            passwordEditText.setError("Password must include at least one letter, one number, and one special character");
            passwordEditText.requestFocus();
            return false;
        }
        if(firstName.length() < 2 || lastName.length() < 2) {
            firstNameEditText.setError("First and last names must be at least 2 characters long");
            firstNameEditText.requestFocus();
            return false;
        }

        if (gender.equals("Select Gender")) {
            Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (country.equals("Select Country")) {
            Toast.makeText(this, "Please select a country", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (city.equals("Select City")) {
            Toast.makeText(this, "Please select a city", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(phone.isEmpty()){
            phoneEditText.setError("Phone number cannot be empty");
            phoneEditText.requestFocus();
            return false;
        }

        // Validate phone number format (optional)
        if (!phone.matches("^\\+\\d{1,3}\\s?\\d{6,15}$")) {
            phoneEditText.setError("Invalid phone number format");
            phoneEditText.requestFocus();
            return false;
        }

        // Check if user already exists
        if (dbHelper.isUserExists(email)) {
            emailEditText.setError("An account with this email already exists");
            emailEditText.requestFocus();
            return false;
        }

        return true;
    }

    private void createAdmin() {
        String email = emailEditText.getText().toString().trim();
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String gender = genderSpinner.getSelectedItem().toString();
        String country = countrySpinner.getSelectedItem().toString();
        String city = citySpinner.getSelectedItem().toString();
        String phone = phoneEditText.getText().toString().trim();

        // Create new admin user
        User newAdmin = new User(email, password, firstName, lastName, gender, phone, country, city,"admin","");
//        newAdmin.setRole("admin"); // Set role as admin

        // Insert into database
        long userId = dbHelper.insertUser(newAdmin);

        if (userId != -1) {
            Toast.makeText(this, "Admin created successfully!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Failed to create admin. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
    }
}
