package com.example.a1210733_1211088_courseproject.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.List;

public class PropertiesFragment extends Fragment implements PropertyAdapter.OnPropertyInteractionListener {

    private RecyclerView recyclerView;
    private DataBaseHelper dbHelper;
    private List<Property> propertyList;
    private PropertyAdapter adapter;
    private SharedPrefManager prefManager;
    private long currentUserId;

    // Search UI elements
    private EditText searchLocation;
    private EditText searchPrice;
    private Spinner searchType;
    private Button searchButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_properties, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        recyclerView = view.findViewById(R.id.properties_recycler);

        // Initialize search UI elements
        searchLocation = view.findViewById(R.id.search_location);
        searchPrice = view.findViewById(R.id.search_price);
        searchType = view.findViewById(R.id.search_type);
        searchButton = view.findViewById(R.id.search_button);

        // Setup property type spinner
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.property_types,
                android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchType.setAdapter(typeAdapter);

        // Initialize database helper
        dbHelper = new DataBaseHelper(getContext(), "RealEstate", null, 1);

        // Get current user ID from SharedPreferences
        prefManager = SharedPrefManager.getInstance(getContext());
        currentUserId = prefManager.getCurrentUserId();

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set click listener for search button
        searchButton.setOnClickListener(v -> performSearch());

        // Load all properties initially
        loadProperties();
    }

    private void performSearch() {
        String location = searchLocation.getText().toString().trim();
        String priceStr = searchPrice.getText().toString().trim();
        String type = searchType.getSelectedItem().toString();

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
            boolean matchesLocation = location.isEmpty() ||
                    property.getCity().toLowerCase().contains(location.toLowerCase()) ||
                    property.getCountry().toLowerCase().contains(location.toLowerCase());

            boolean matchesPrice = property.getPrice() <= maxPrice;

            boolean matchesType = type.equals("All") || property.getType().equals(type);

            if (matchesLocation && matchesPrice && matchesType) {
                filteredList.add(property);
            }
        }

        // Update the adapter with filtered results
        adapter = new PropertyAdapter(getContext(), filteredList, this);
        recyclerView.setAdapter(adapter);

        // Show message if no results found
        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "No properties match your search criteria", Toast.LENGTH_SHORT).show();
        }
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
    }

    @Override
    public void onReserveProperty(Property property) {
        // Navigate to reservation form
        ReservationFragment reservationFragment = new ReservationFragment();
        Bundle args = new Bundle();
        args.putLong("propertyId", property.getPropertyId());
        args.putString("propertyTitle", property.getTitle());
        reservationFragment.setArguments(args);

        // Replace current fragment with reservation fragment
        getParentFragmentManager().beginTransaction()
            .replace(R.id.home_fragment_container, reservationFragment)
            .addToBackStack(null)
            .commit();
    }
}
