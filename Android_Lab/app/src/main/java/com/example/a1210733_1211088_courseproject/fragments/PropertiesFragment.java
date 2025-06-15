package com.example.a1210733_1211088_courseproject.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1210733_1211088_courseproject.R;
import com.example.a1210733_1211088_courseproject.adapters.PropertyAdapter;
import com.example.a1210733_1211088_courseproject.database.DataBaseHelper;
import com.example.a1210733_1211088_courseproject.database.sql.PropertyQueries;
import com.example.a1210733_1211088_courseproject.models.Property;
import com.example.a1210733_1211088_courseproject.utils.SharedPrefManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class PropertiesFragment extends Fragment implements PropertyAdapter.OnPropertyInteractionListener {

    private RecyclerView recyclerView;
    private DataBaseHelper dbHelper;
    private List<Property> propertyList;
    private PropertyAdapter adapter;
    private SharedPrefManager prefManager;
    private long currentUserId;    // Search UI elements
    private TextInputEditText searchName;
    private TextInputEditText searchLocation;
    private TextInputEditText searchPrice;
    private AutoCompleteTextView searchType;
    private MaterialButton searchButton;
    private MaterialButton clearButton;
      // Toggle UI elements
    private ImageButton toggleSearchButton;
    private LinearLayout searchContentLayout;
    private LinearLayout searchHeaderClickable;
    private boolean isSearchVisible = false; // Start hidden

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_properties, container, false);
    }    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            // Initialize UI components
            recyclerView = view.findViewById(R.id.properties_recycler);            // Initialize search UI elements
            searchName = view.findViewById(R.id.search_name);
            searchLocation = view.findViewById(R.id.search_location);
            searchPrice = view.findViewById(R.id.search_price);
            searchType = view.findViewById(R.id.search_type);
            searchButton = view.findViewById(R.id.search_button);
            clearButton = view.findViewById(R.id.clear_button);
              // Initialize toggle UI elements
            toggleSearchButton = view.findViewById(R.id.btn_toggle_search);
            searchContentLayout = view.findViewById(R.id.search_content_layout);
            searchHeaderClickable = view.findViewById(R.id.search_header_clickable);

            // Check for null views
            if (searchType == null) {
                Toast.makeText(getContext(), "Search type view not found", Toast.LENGTH_SHORT).show();
                return;
            }

            // Setup property type dropdown
            setupPropertyTypeDropdown();
            
            // Setup toggle functionality
            setupToggleSearchButton();

        // Initialize database helper
        dbHelper = new DataBaseHelper(getContext(), "RealEstate", null, 1);

        // Get current user ID from SharedPreferences
        prefManager = SharedPrefManager.getInstance(getContext());
        currentUserId = prefManager.getCurrentUserId();

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));        // Set click listeners for buttons
        searchButton.setOnClickListener(v -> performSearch());
        clearButton.setOnClickListener(v -> clearFilters());

        // Load all properties initially
        loadProperties();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error initializing properties view: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }private void setupPropertyTypeDropdown() {
        try {
            String[] propertyTypes = getResources().getStringArray(R.array.property_types);
            
            // Create a material design dropdown adapter
            ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                    requireContext(),
                    com.google.android.material.R.layout.support_simple_spinner_dropdown_item,
                    propertyTypes
            );
            
            searchType.setAdapter(typeAdapter);
            searchType.setText("All", false); // Set default selection without triggering dropdown
            
            // Set the dropdown to be non-editable
            searchType.setKeyListener(null);
            
            // Ensure dropdown shows when clicked
            searchType.setOnClickListener(v -> searchType.showDropDown());
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error setting up dropdown: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }private void performSearch() {
        String propertyName = searchName.getText().toString().trim();
        String location = searchLocation.getText().toString().trim();
        String priceStr = searchPrice.getText().toString().trim();
        String type = searchType.getText().toString().trim();

        // Default max price if not specified
        double maxPrice = Double.MAX_VALUE;

        if (!priceStr.isEmpty()) {
            try {
                maxPrice = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter a valid price", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Filter the properties based on search criteria
        List<Property> filteredList = new ArrayList<>();

        for (Property property : propertyList) {
            boolean matchesName = propertyName.isEmpty() ||
                    property.getTitle().toLowerCase().contains(propertyName.toLowerCase());
            
            boolean matchesLocation = location.isEmpty() ||
                    property.getCity().toLowerCase().contains(location.toLowerCase()) ||
                    property.getCountry().toLowerCase().contains(location.toLowerCase());

            boolean matchesPrice = property.getPrice() <= maxPrice;

            boolean matchesType = type.equals("All") || type.isEmpty() || property.getType().equals(type);

            if (matchesName && matchesLocation && matchesPrice && matchesType) {
                filteredList.add(property);
            }
        }

        // Update the adapter with filtered results
        adapter = new PropertyAdapter(getContext(), filteredList, this);
        recyclerView.setAdapter(adapter);

        // Show message with results
        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "No properties match your search criteria", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Found " + filteredList.size() + " properties", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFilters() {
        // Clear all search fields
        searchName.setText("");
        searchLocation.setText("");
        searchPrice.setText("");
        searchType.setText("All", false);
        
        // Reset to show all properties
        adapter = new PropertyAdapter(getContext(), propertyList, this);
        recyclerView.setAdapter(adapter);
        
        Toast.makeText(getContext(), "Filters cleared", Toast.LENGTH_SHORT).show();
    }

    private void loadProperties() {
        propertyList = new ArrayList<>();

        // Get all properties from database
        Cursor cursor = dbHelper.getAllProperties();

        if (cursor != null && cursor.moveToFirst()) {
            // Show RecyclerView
            recyclerView.setVisibility(View.VISIBLE);

            do {
                // Extract property data from cursor
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_PROPERTY_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_DESCRIPTION));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_TYPE));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_PRICE));
                float area = cursor.getFloat(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_AREA));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_CITY));
                int bedrooms = cursor.getInt(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_BEDROOMS));
                int bathrooms = cursor.getInt(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_BATHROOMS));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_IMAGE_URL));
                boolean isSpecial = cursor.getInt(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_IS_SPECIAL)) == 1;
                String country = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_COUNTRY));
                String city = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_CITY));
                double discount = cursor.getDouble(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_DISCOUNT));
                // Create Property object and add to list

                Property property = new Property(id, type,title,description, bedrooms, bathrooms,
                area, price, country, city,imageUrl, isSpecial,discount);

                propertyList.add(property);

            } while (cursor.moveToNext());

            // Set adapter with property list - use the constructor that includes the listener
            adapter = new PropertyAdapter(getContext(), propertyList, this);
            recyclerView.setAdapter(adapter);

        } else {
            // No properties found, display message
            Toast.makeText(getContext(), "No properties available", Toast.LENGTH_SHORT).show();
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    // Implement OnPropertyInteractionListener methods
    @Override
    public void onAddToFavorites(Property property) {
        boolean success = dbHelper.addToFavorites(currentUserId, property.getPropertyId());
        if (success) {
            Toast.makeText(getContext(), property.getTitle() + " added to favorites", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Property already in favorites", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRemoveFromFavorites(Property property) {
        boolean success = dbHelper.removeFromFavorites(currentUserId, property.getPropertyId());
        if (success) {
            Toast.makeText(getContext(), property.getTitle() + " removed from favorites", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Error removing property from favorites", Toast.LENGTH_SHORT).show();
        }
    }    @Override
    public void onReserveProperty(Property property) {
        // Navigate to reservation form with smooth transition animation
        ReservationFragment reservationFragment = new ReservationFragment();
        Bundle args = new Bundle();
        args.putLong("propertyId", property.getPropertyId());
        args.putString("propertyTitle", property.getTitle());
        reservationFragment.setArguments(args);

        // Replace current fragment with reservation fragment with custom animations
        getParentFragmentManager().beginTransaction()
            .setCustomAnimations(
                R.anim.reservation_enter,      // Enter animation - slides up with bounce
                R.anim.reservation_exit,       // Exit animation - slides up and fades
                R.anim.reservation_pop_enter,  // Pop enter animation (when returning)
                R.anim.reservation_pop_exit    // Pop exit animation (when returning)
            )
            .replace(R.id.home_fragment_container, reservationFragment)
            .addToBackStack(null)
            .commit();
    }    /**
     * Sets up the toggle button functionality for showing/hiding the search panel
     */
    private void setupToggleSearchButton() {
        if (toggleSearchButton != null && searchContentLayout != null) {
            toggleSearchButton.setOnClickListener(v -> toggleSearchVisibility());
        }
          // Make the entire header clickable to toggle search (expand/collapse)
        if (searchHeaderClickable != null) {
            searchHeaderClickable.setOnClickListener(v -> toggleSearchVisibility());
        }
    }    /**
     * Toggles the visibility of the search content with smooth animation
     */
    private void toggleSearchVisibility() {
        if (searchContentLayout == null || toggleSearchButton == null) return;

        Animation animation;
        
        if (isSearchVisible) {
            // Hide search content with smooth collapse
            animation = AnimationUtils.loadAnimation(getContext(), R.anim.collapse_smooth_out);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    searchContentLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            searchContentLayout.startAnimation(animation);
            
            // Update toggle button icon to expand_more
            toggleSearchButton.setImageResource(R.drawable.ic_expand_more);
            isSearchVisible = false;
        } else {
            // Show search content with smooth expand
            searchContentLayout.setVisibility(View.VISIBLE);
            animation = AnimationUtils.loadAnimation(getContext(), R.anim.expand_smooth_in);
            searchContentLayout.startAnimation(animation);
            
            // Update toggle button icon to expand_less
            toggleSearchButton.setImageResource(R.drawable.ic_expand_less);
            isSearchVisible = true;
        }
    }
}
