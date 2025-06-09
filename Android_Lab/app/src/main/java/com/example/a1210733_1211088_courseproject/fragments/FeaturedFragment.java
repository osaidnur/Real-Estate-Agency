package com.example.a1210733_1211088_courseproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1210733_1211088_courseproject.R;
import com.example.a1210733_1211088_courseproject.fragments.ReservationFragment;
import com.example.a1210733_1211088_courseproject.adapters.PropertyAdapter;
import com.example.a1210733_1211088_courseproject.models.Property;
import com.example.a1210733_1211088_courseproject.database.DataBaseHelper;
import com.example.a1210733_1211088_courseproject.utils.SharedPrefManager;
import java.util.ArrayList;
import java.util.List;

public class FeaturedFragment extends Fragment implements PropertyAdapter.OnPropertyInteractionListener {

    private RecyclerView recyclerView;
    private PropertyAdapter adapter;
    private List<Property> featuredList;
    private TextView emptyText;
    private DataBaseHelper dbHelper;
    private SharedPrefManager prefManager;
    private long currentUserId;

    public FeaturedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_featured, container, false);
    }    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize database helper
        dbHelper = new DataBaseHelper(getContext(), "RealEstate", null, 1);
        
        // Get current user ID from SharedPreferences
        prefManager = SharedPrefManager.getInstance(getContext());
        currentUserId = prefManager.getCurrentUserId();

        recyclerView = view.findViewById(R.id.featured_recycler);
        emptyText = view.findViewById(R.id.empty_featured_text);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load featured properties from database
        loadFeaturedProperties();
    }

    /**
     * Loads featured properties from the database
     */
    private void loadFeaturedProperties() {
        featuredList = dbHelper.getFeaturedProperties();

        if (featuredList.isEmpty()) {
            // Show empty state
            if (emptyText != null) {
                emptyText.setVisibility(View.VISIBLE);
                emptyText.setText("No featured properties available at the moment.");
            }
            recyclerView.setVisibility(View.GONE);
        } else {
            // Hide empty state and show list
            if (emptyText != null) {
                emptyText.setVisibility(View.GONE);
            }
            recyclerView.setVisibility(View.VISIBLE);

            // Set up adapter
            adapter = new PropertyAdapter(getContext(), featuredList, this);
            recyclerView.setAdapter(adapter);
        }
    }@Override
    public void onAddToFavorites(Property property) {
        // Add to database
        boolean success = dbHelper.addToFavorites(currentUserId, property.getPropertyId());
        
        if (getContext() != null) {
            if (success) {
                android.widget.Toast.makeText(getContext(),
                    property.getTitle() + " added to favorites",
                    android.widget.Toast.LENGTH_SHORT).show();
            } else {
                android.widget.Toast.makeText(getContext(),
                    "Failed to add to favorites",
                    android.widget.Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRemoveFromFavorites(Property property) {
        // Remove from database
        boolean success = dbHelper.removeFromFavorites(currentUserId, property.getPropertyId());
        
        if (getContext() != null) {
            if (success) {
                android.widget.Toast.makeText(getContext(),
                    property.getTitle() + " removed from favorites",
                    android.widget.Toast.LENGTH_SHORT).show();
            } else {
                android.widget.Toast.makeText(getContext(),
                    "Failed to remove from favorites",
                    android.widget.Toast.LENGTH_SHORT).show();
            }
        }
    }    @Override
    public void onReserveProperty(Property property) {
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

    @Override
    public void onResume() {
        super.onResume();
        // Refresh featured properties when fragment becomes visible
        if (dbHelper != null) {
            loadFeaturedProperties();
        }
    }
}
