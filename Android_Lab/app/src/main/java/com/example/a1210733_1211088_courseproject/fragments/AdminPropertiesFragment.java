package com.example.a1210733_1211088_courseproject.fragments;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.List;

public class AdminPropertiesFragment extends Fragment implements AdminPropertyAdapter.OnPropertyActionListener {
    
    private static final String TAG = "AdminPropertiesFragment";
    
    private RecyclerView recyclerView;
    private TextView emptyView;
    private AdminPropertyAdapter adapter;
    private DataBaseHelper dbHelper;
    private List<Property> propertyList;
    
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
        emptyView = view.findViewById(R.id.admin_properties_empty_view);
        
        // Initialize database helper
        dbHelper = new DataBaseHelper(getContext(), "RealEstate", null, 1);
        
        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Load properties
        loadProperties();
    }
    
    private void loadProperties() {
        propertyList = new ArrayList<>();
        
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
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
