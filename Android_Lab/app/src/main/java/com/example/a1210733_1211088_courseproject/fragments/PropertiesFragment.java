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
import com.example.a1210733_1211088_courseproject.database.DataBaseHelper;
import com.example.a1210733_1211088_courseproject.database.sql.PropertyQueries;
import com.example.a1210733_1211088_courseproject.models.Property;

import java.util.ArrayList;
import java.util.List;

public class PropertiesFragment extends Fragment {

    private RecyclerView recyclerView;
    private DataBaseHelper dbHelper;
    private List<Property> propertyList;
    private PropertyAdapter adapter;

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

            boolean matchesType = type.equals("All Types") || property.getType().equals(type);

            if (matchesLocation && matchesPrice && matchesType) {
                filteredList.add(property);
            }
        }

        // Update the adapter with filtered results
        adapter = new PropertyAdapter(filteredList);
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

            // Set adapter with property list
            adapter = new PropertyAdapter(propertyList);
            recyclerView.setAdapter(adapter);

        } else {
            // No properties found, display message
            Toast.makeText(getContext(), "No properties available", Toast.LENGTH_SHORT).show();
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    // RecyclerView Adapter for properties
    private class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder> {

        private List<Property> properties;

        PropertyAdapter(List<Property> properties) {
            this.properties = properties;
        }

        @NonNull
        @Override
        public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_property, parent, false);
            return new PropertyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
            Property property = properties.get(position);
            holder.titleTextView.setText(property.getTitle());
            holder.priceTextView.setText(String.format("$%.2f", property.getPrice()));
            holder.locationTextView.setText(property.getCity());
            holder.typeTextView.setText(property.getType());
            holder.infoTextView.setText(String.format("%d bed, %d bath",
                    property.getBedrooms(), property.getBathrooms()));

            // Set click listener for item
            holder.itemView.setOnClickListener(v -> {
                // Show property details or navigate to detail fragment
                Toast.makeText(getContext(), "Selected: " + property.getTitle(), Toast.LENGTH_SHORT).show();
                // Future enhancement: Navigate to property detail page
            });
        }

        @Override
        public int getItemCount() {
            return properties.size();
        }

        class PropertyViewHolder extends RecyclerView.ViewHolder {
            TextView titleTextView;
            TextView priceTextView;
            TextView locationTextView;
            TextView typeTextView;
            TextView infoTextView;

            PropertyViewHolder(View view) {
                super(view);
                titleTextView = view.findViewById(R.id.property_title);
                priceTextView = view.findViewById(R.id.property_price);
                locationTextView = view.findViewById(R.id.property_location);
                typeTextView = view.findViewById(R.id.property_type);
                infoTextView = view.findViewById(R.id.property_info);
            }
        }
    }
}
