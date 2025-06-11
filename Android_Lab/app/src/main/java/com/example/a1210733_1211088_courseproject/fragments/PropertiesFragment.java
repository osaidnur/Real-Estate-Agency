package com.example.a1210733_1211088_courseproject.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.a1210733_1211088_courseproject.fragments.ReservationFragment;
import com.example.a1210733_1211088_courseproject.models.Property;
import com.example.a1210733_1211088_courseproject.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class PropertiesFragment extends Fragment implements PropertyAdapter.OnPropertyInteractionListener {

    private RecyclerView recyclerView;
    private DataBaseHelper dbHelper;
    private SharedPrefManager sharedPrefManager;
    private List<Property> propertyList;
    private PropertyAdapter adapter;
    private long currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_properties, container, false);
    }    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        recyclerView = view.findViewById(R.id.properties_recycler);

        // Initialize database helper and shared preferences
        dbHelper = new DataBaseHelper(getContext(), "RealEstate", null, 1);
        sharedPrefManager = SharedPrefManager.getInstance(getContext());
        currentUserId = sharedPrefManager.getCurrentUserId();

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load properties from database
        loadProperties();
    }    private void loadProperties() {
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
                Property property = new Property(id, type, title, description, bedrooms, bathrooms,
                        area, price, country, city, imageUrl, isSpecial, discount);

                propertyList.add(property);

            } while (cursor.moveToNext());

            // Set adapter with property list using the proper PropertyAdapter
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

    // Implement PropertyAdapter.OnPropertyInteractionListener methods

    @Override
    public void onAddToFavorites(Property property) {
        // Add to database
        boolean success = dbHelper.addToFavorites(currentUserId, property.getPropertyId());
        
        if (getContext() != null) {
            if (success) {
                Toast.makeText(getContext(),
                    property.getTitle() + " added to favorites",
                    Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(),
                    "Failed to add to favorites or already in favorites",
                    Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRemoveFromFavorites(Property property) {
        // Remove from database
        boolean success = dbHelper.removeFromFavorites(currentUserId, property.getPropertyId());
        
        if (getContext() != null) {
            if (success) {
                Toast.makeText(getContext(),
                    property.getTitle() + " removed from favorites",
                    Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(),
                    "Failed to remove from favorites",
                    Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onReserveProperty(Property property) {
        if (currentUserId == -1) {
            Toast.makeText(getContext(), "Please log in to reserve properties", Toast.LENGTH_SHORT).show();
            return;
        }

        // Open reservation fragment
        ReservationFragment reservationFragment = new ReservationFragment();
        Bundle args = new Bundle();
        args.putLong("propertyId", property.getPropertyId());
        args.putString("propertyTitle", property.getTitle());
        reservationFragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.home_fragment_container, reservationFragment)
                .addToBackStack(null)
                .commit();
    }
}

