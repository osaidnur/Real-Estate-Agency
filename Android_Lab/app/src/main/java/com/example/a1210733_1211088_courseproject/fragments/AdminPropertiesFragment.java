package com.example.a1210733_1211088_courseproject.fragments;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
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
import com.example.a1210733_1211088_courseproject.adapters.AdminPropertyAdapter;
import com.example.a1210733_1211088_courseproject.database.DataBaseHelper;
import com.example.a1210733_1211088_courseproject.database.sql.PropertyQueries;
import com.example.a1210733_1211088_courseproject.models.Property;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AdminPropertiesFragment extends Fragment implements AdminPropertyAdapter.OnPropertyActionListener {
    
    private static final String TAG = "AdminPropertiesFragment";
    
    private RecyclerView recyclerView;
    private TextView emptyView;
    private AdminPropertyAdapter adapter;
    private DataBaseHelper dbHelper;
    private List<Property> propertyList;
    private List<Property> originalPropertyList; // Keep original list for filtering      // Search and filter UI elements
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
        return inflater.inflate(R.layout.fragment_admin_properties, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        recyclerView = view.findViewById(R.id.admin_properties_recycler);
        emptyView = view.findViewById(R.id.admin_properties_empty_view);          // Initialize search and filter UI elements
        searchName = view.findViewById(R.id.admin_search_name);
        searchLocation = view.findViewById(R.id.admin_search_location);
        searchPrice = view.findViewById(R.id.admin_search_price);
        searchType = view.findViewById(R.id.admin_search_type);
        searchButton = view.findViewById(R.id.admin_search_button);
        clearButton = view.findViewById(R.id.admin_clear_button);
          // Initialize toggle UI elements
        toggleSearchButton = view.findViewById(R.id.admin_btn_toggle_search);
        searchContentLayout = view.findViewById(R.id.admin_search_content_layout);
        searchHeaderClickable = view.findViewById(R.id.admin_search_header_clickable);
        
        // Initialize database helper
        dbHelper = new DataBaseHelper(getContext(), "RealEstate", null, 1);
        
        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
          // Setup property type dropdown
        setupPropertyTypeDropdown();
        
        // Setup search functionality
        setupSearchFunctionality();
        
        // Setup toggle functionality
        setupToggleSearchButton();
        
        // Load properties
        loadProperties();
    }
      private void setupPropertyTypeDropdown() {
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
        
        Log.d(TAG, "Property type dropdown setup with " + propertyTypes.length + " items");
    }
    
    private void setupSearchFunctionality() {
        searchButton.setOnClickListener(v -> performSearch());
        clearButton.setOnClickListener(v -> clearFilters());
    }
      private void performSearch() {
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

        for (Property property : originalPropertyList) {
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
        propertyList.clear();
        propertyList.addAll(filteredList);
        adapter.notifyDataSetChanged();

        // Update visibility based on results
        if (filteredList.isEmpty()) {
            emptyView.setText("No properties match your search criteria.\nTry adjusting your filters.");
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            Toast.makeText(getContext(), "No properties match your search criteria", Toast.LENGTH_SHORT).show();
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Found " + filteredList.size() + " properties", Toast.LENGTH_SHORT).show();
        }        Log.d(TAG, "Search performed: name=" + propertyName + ", location=" + location + 
              ", maxPrice=" + maxPrice + ", type=" + type + ", results=" + filteredList.size());
    }
      private void clearFilters() {
        // Clear all search fields
        searchName.setText("");
        searchLocation.setText("");
        searchPrice.setText("");
        searchType.setText("All", false);
        
        // Restore original property list
        propertyList.clear();
        propertyList.addAll(originalPropertyList);
        adapter.notifyDataSetChanged();
        
        // Update visibility
        if (originalPropertyList.isEmpty()) {
            emptyView.setText("No properties found.\nConnect to API to load properties.");
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }        
        Toast.makeText(getContext(), "Filters cleared", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Filters cleared, showing all " + originalPropertyList.size() + " properties");
    }
    
    private void loadProperties() {
        propertyList = new ArrayList<>();
        originalPropertyList = new ArrayList<>(); // Initialize original list
        
        Cursor cursor = dbHelper.getAllProperties();
        
        if (cursor != null && cursor.moveToFirst()) {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_PROPERTY_ID));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_TYPE));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_DESCRIPTION));
                int bedrooms = cursor.getInt(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_BEDROOMS));
                int bathrooms = cursor.getInt(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_BATHROOMS));
                float area = cursor.getFloat(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_AREA));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_PRICE));
                String country = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_COUNTRY));
                String city = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_CITY));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_IMAGE_URL));
                boolean isSpecial = cursor.getInt(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_IS_SPECIAL)) == 1;
                double discount = cursor.getDouble(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_DISCOUNT));
                
                Property property = new Property(id, type, title, description, bedrooms, bathrooms,
                        area, price, country, city, imageUrl, isSpecial, discount);
                
                propertyList.add(property);
                originalPropertyList.add(property); // Add to both lists
                
            } while (cursor.moveToNext());
            
            cursor.close();
        } else {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        
        // Setup adapter
        adapter = new AdminPropertyAdapter(getContext(), propertyList, this);
        recyclerView.setAdapter(adapter);
        
        Log.d(TAG, "Loaded " + propertyList.size() + " properties");
    }
    
    @Override
    public void onAddSpecialOffer(Property property) {
        showDiscountDialog(property);
    }
    
    @Override
    public void onRemoveSpecialOffer(Property property) {
        // Update property in database
        if (updatePropertySpecialStatus(property.getPropertyId(), false, 0.0)) {
            property.setSpecial(false);
            property.setDiscount(0.0);
            adapter.notifyDataSetChanged();
            Toast.makeText(getContext(), "Special offer removed for " + property.getTitle(), 
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Failed to remove special offer", 
                    Toast.LENGTH_SHORT).show();
        }
    }
      private void showDiscountDialog(Property property) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Special Offer");
        builder.setMessage("Enter discount percentage for " + property.getTitle() + ":");
        
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("Enter 0-100");
        builder.setView(input);
        
        builder.setPositiveButton("Apply", (dialog, which) -> {
            String discountStr = input.getText().toString().trim();
            
            if (discountStr.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a discount percentage", 
                        Toast.LENGTH_SHORT).show();
                return;
            }
            
            try {
                double discount = Double.parseDouble(discountStr);
                
                if (discount <= 0 || discount >= 100) {
                    Toast.makeText(getContext(), "Discount must be between 0-100% (exclusive)", 
                            Toast.LENGTH_LONG).show();
                    return;
                }
                
                // Update property in database
                if (updatePropertySpecialStatus(property.getPropertyId(), true, discount)) {
                    property.setSpecial(true);
                    property.setDiscount(discount);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), 
                            String.format("Special offer of %.1f%% applied to %s", discount, property.getTitle()),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to apply special offer", 
                            Toast.LENGTH_SHORT).show();
                }
                
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter a valid number", 
                        Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("Cancel", null);
        
        AlertDialog dialog = builder.create();
        dialog.show();
        
        // Reset button fonts to default system font
        if (dialog.getButton(AlertDialog.BUTTON_POSITIVE) != null) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(android.graphics.Typeface.DEFAULT);
        }
        if (dialog.getButton(AlertDialog.BUTTON_NEGATIVE) != null) {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(android.graphics.Typeface.DEFAULT);
        }
    }
    
    private boolean updatePropertySpecialStatus(long propertyId, boolean isSpecial, double discount) {
        try {
            return dbHelper.updatePropertySpecialOffer(propertyId, isSpecial, discount);
        } catch (Exception e) {
            Log.e(TAG, "Error updating property special status", e);
            return false;
        }
    }
      /**
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
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
