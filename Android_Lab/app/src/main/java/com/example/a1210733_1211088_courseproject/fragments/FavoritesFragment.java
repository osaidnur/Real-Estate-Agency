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

public class FavoritesFragment extends Fragment implements PropertyAdapter.OnPropertyInteractionListener {    private RecyclerView recyclerView;
    private PropertyAdapter adapter;
    private List<Property> favoritesList;
    private TextView emptyText;
    private DataBaseHelper dbHelper;
    private SharedPrefManager prefManager;
    private long currentUserId;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize database helper
        dbHelper = new DataBaseHelper(getContext(), "RealEstate", null, 1);
        
        // Get current user ID from SharedPreferences
        prefManager = SharedPrefManager.getInstance(getContext());
        currentUserId = prefManager.getCurrentUserId();

        recyclerView = view.findViewById(R.id.favorites_recycler);
        emptyText = view.findViewById(R.id.empty_favorites_text);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load favorites from database
        loadFavoriteProperties();
    }

    /**
     * Loads user's favorite properties from the database
     */
    private void loadFavoriteProperties() {
        favoritesList = dbHelper.getUserFavoriteProperties(currentUserId);

        if (favoritesList.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText("No favorite properties yet.\nAdd some properties to your favorites!");
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            // Set up adapter
            adapter = new PropertyAdapter(getContext(), favoritesList, this);
            recyclerView.setAdapter(adapter);
        }
    }    @Override
    public void onAddToFavorites(Property property) {
        // Only called when database operation was successful
        if (getContext() != null) {
            android.widget.Toast.makeText(getContext(),
                property.getTitle() + " added to favorites",
                android.widget.Toast.LENGTH_SHORT).show();
            // Refresh the list to show the new favorite
            loadFavoriteProperties();
        }
    }    @Override
    public void onRemoveFromFavorites(Property property) {
        // Only called when database operation was successful
        if (getContext() != null) {
            android.widget.Toast.makeText(getContext(),
                property.getTitle() + " removed from favorites",
                android.widget.Toast.LENGTH_SHORT).show();
            
            // Remove from UI
            favoritesList.remove(property);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }

            // Update empty state
            if (favoritesList.isEmpty()) {
                emptyText.setVisibility(View.VISIBLE);
                emptyText.setText("No favorite properties yet.\nAdd some properties to your favorites!");
                recyclerView.setVisibility(View.GONE);
            }
        }
    }@Override
    public void onReserveProperty(Property property) {
        // Open reservation fragment with smooth transition animation
        ReservationFragment reservationFragment = new ReservationFragment();
        Bundle args = new Bundle();
        args.putLong("propertyId", property.getPropertyId());
        args.putString("propertyTitle", property.getTitle());
        reservationFragment.setArguments(args);

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
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh favorites when fragment becomes visible
        if (dbHelper != null && currentUserId > 0) {
            loadFavoriteProperties();
        }
    }
}
