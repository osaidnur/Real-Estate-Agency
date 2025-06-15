package com.example.a1210733_1211088_courseproject.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
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
import com.example.a1210733_1211088_courseproject.adapters.SpecialOfferAdapter;
import com.example.a1210733_1211088_courseproject.database.DataBaseHelper;
import com.example.a1210733_1211088_courseproject.models.Property;

import java.util.List;

public class AdminSpecialOffersFragment extends Fragment implements SpecialOfferAdapter.OnSpecialOfferActionListener {
    
    private static final String TAG = "AdminSpecialOffersFragment";
    
    private RecyclerView recyclerView;
    private TextView emptyView;
    private SpecialOfferAdapter adapter;
    private DataBaseHelper dbHelper;
    private List<Property> specialOffersList;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_special_offers, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        recyclerView = view.findViewById(R.id.admin_special_offers_recycler);
        emptyView = view.findViewById(R.id.admin_special_offers_empty_view);
        
        // Initialize database helper
        dbHelper = new DataBaseHelper(getContext(), "RealEstate", null, 1);
        
        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Load special offers
        loadSpecialOffers();
    }
    
    private void loadSpecialOffers() {
        specialOffersList = dbHelper.getFeaturedProperties();
        
        if (specialOffersList != null && !specialOffersList.isEmpty()) {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            
            // Setup adapter
            adapter = new SpecialOfferAdapter(getContext(), specialOffersList, this);
            recyclerView.setAdapter(adapter);
            
            Log.d(TAG, "Loaded " + specialOffersList.size() + " special offers");
        } else {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            Log.d(TAG, "No special offers found");
        }
    }
      @Override
    public void onRemoveSpecialOffer(Property property) {
        // Show confirmation dialog
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Remove Special Offer")
                .setMessage("Are you sure you want to remove the special offer for \"" + property.getTitle() + "\"?")
                .setPositiveButton("Remove", (dialogInterface, which) -> {
                    // Remove special offer from database
                    if (dbHelper.updatePropertySpecialOffer(property.getPropertyId(), false, 0.0)) {
                        // Remove from local list and update adapter
                        specialOffersList.remove(property);
                        adapter.notifyDataSetChanged();
                        
                        // Check if list is now empty
                        if (specialOffersList.isEmpty()) {
                            emptyView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                        
                        Toast.makeText(getContext(), "Special offer removed for " + property.getTitle(),
                                Toast.LENGTH_SHORT).show();
                        
                        Log.d(TAG, "Special offer removed for property: " + property.getTitle());
                    } else {
                        Toast.makeText(getContext(), "Failed to remove special offer",
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Failed to remove special offer for property: " + property.getTitle());
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        
        dialog.show();
        
        // Reset button fonts to default system font
        if (dialog.getButton(AlertDialog.BUTTON_POSITIVE) != null) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(android.graphics.Typeface.DEFAULT);
        }
        if (dialog.getButton(AlertDialog.BUTTON_NEGATIVE) != null) {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(android.graphics.Typeface.DEFAULT);
        }
    }
    
    @Override
    public void onViewPropertyDetails(Property property) {
        // Show property details dialog
        showPropertyDetailsDialog(property);
    }
      private void showPropertyDetailsDialog(Property property) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Property Details");
        
        String details = String.format(
                "Title: %s\n\n" +
                "Type: %s\n" +
                "Location: %s, %s\n" +
                "Price: $%.2f\n" +
                "Discounted Price: $%.2f\n" +
                "Discount: %.1f%%\n\n" +
                "Details: %d bed, %d bath, %.0f sq ft\n\n" +
                "Description: %s",
                property.getTitle(),
                property.getType(),
                property.getCity(), property.getCountry(),
                property.getPrice(),
                property.getDiscountedPrice(),
                property.getDiscount(),
                property.getBedrooms(), property.getBathrooms(), property.getArea(),
                property.getDescription()
        );
        
        builder.setMessage(details);
        builder.setPositiveButton("Close", null);
        
        AlertDialog dialog = builder.create();
        dialog.show();
        
        // Reset button fonts to default system font
        if (dialog.getButton(AlertDialog.BUTTON_POSITIVE) != null) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(android.graphics.Typeface.DEFAULT);
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
