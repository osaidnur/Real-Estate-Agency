package com.example.a1210733_1211088_courseproject.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1210733_1211088_courseproject.R;
import com.example.a1210733_1211088_courseproject.database.DataBaseHelper;
import com.example.a1210733_1211088_courseproject.database.sql.FavoriteQueries;
import com.example.a1210733_1211088_courseproject.models.Property;
import com.example.a1210733_1211088_courseproject.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder> {
    private Context context;
    private List<Property> propertyList;
    private List<Property> originalList;
    private OnPropertyInteractionListener listener;
    private DataBaseHelper dbHelper;
    private long currentUserId;

    public PropertyAdapter(Context context, List<Property> propertyList, OnPropertyInteractionListener listener) {
        this.context = context;
        this.propertyList = propertyList;
        this.originalList = new ArrayList<>(propertyList);
        this.listener = listener;
        this.dbHelper = new DataBaseHelper(context, "RealEstate", null, 1);

        // Get current user ID from SharedPreferences
        SharedPrefManager prefManager = SharedPrefManager.getInstance(context);
        this.currentUserId = prefManager.getCurrentUserId();
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_property, parent, false);
        return new PropertyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        Property property = propertyList.get(position);

        // Load image - if URL is not empty, could use Picasso or Glide here
        // For now, just using a placeholder image
        if (property.getImageUrl() != null && !property.getImageUrl().isEmpty()) {
            // In a real app, you would load the image from the URL:
            // Picasso.get().load(property.getImageUrl()).into(holder.propertyImage);
            holder.propertyImage.setImageResource(R.drawable.ic_home);
        } else {
            holder.propertyImage.setImageResource(R.drawable.ic_home);
        }

        holder.propertyTitle.setText(property.getTitle());
        holder.propertyDescription.setText(property.getDescription());
        holder.propertyPrice.setText(String.format("$%.2f", property.getPrice()));
        holder.propertyLocation.setText(property.getCountry());
        holder.propertyType.setText(property.getType());

        // Property info (bedrooms/bathrooms)
        holder.propertyInfo.setText(String.format("%d bed, %d bath",
                property.getBedrooms(), property.getBathrooms()));        // Check if property is in user's favorites
        boolean isFavorite = isPropertyInFavorites(property.getPropertyId());
        updateFavoriteButton(holder.favoriteButton, isFavorite);

        // Set click listeners
        holder.favoriteButton.setOnClickListener(v -> {
            boolean newFavoriteStatus = !isPropertyInFavorites(property.getPropertyId());

            // Update database
            if (newFavoriteStatus) {
                // Add to favorites
                addToFavorites(property.getPropertyId());
            } else {
                // Remove from favorites
                removeFromFavorites(property.getPropertyId());
            }

            // Update UI
            updateFavoriteButton(holder.favoriteButton, newFavoriteStatus);

            // Notify listener
            if (listener != null) {
                if (newFavoriteStatus) {
                    listener.onAddToFavorites(property);
                } else {
                    listener.onRemoveFromFavorites(property);
                }
            }
        });

        holder.reserveButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onReserveProperty(property);
            }
        });
    }

    /**
     * Checks if a property is in the user's favorites
     * @param propertyId The property ID to check
     * @return true if the property is in favorites, false otherwise
     */
    private boolean isPropertyInFavorites(long propertyId) {
        Cursor cursor = null;
        try {
            // Query for this user's favorite with this property ID
            String[] selectionArgs = {String.valueOf(currentUserId), String.valueOf(propertyId)};
            cursor = dbHelper.getReadableDatabase().rawQuery(
                    FavoriteQueries.GET_FAVORITE_BY_USER_AND_PROPERTY, selectionArgs);
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Adds a property to user's favorites
     * @param propertyId The property ID to add
     */
    private void addToFavorites(long propertyId) {
        dbHelper.addToFavorites(currentUserId, propertyId);
    }

    /**
     * Removes a property from user's favorites
     * @param propertyId The property ID to remove
     */
    private void removeFromFavorites(long propertyId) {
        dbHelper.removeFromFavorites(currentUserId, propertyId);
    }

    private void updateFavoriteButton(Button button, boolean isFavorite) {
        if (isFavorite) {
            button.setText("Remove from Favorites");
            button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_favorite, 0, 0, 0);
        } else {
            button.setText("Add to Favorites");
            button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_star, 0, 0, 0);
        }
    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }

    // Filter properties by type
    public void filterByType(String type) {
        propertyList.clear();
        if (type.equals("All")) {
            propertyList.addAll(originalList);
        } else {
            for (Property property : originalList) {
                if (property.getType().equals(type)) {
                    propertyList.add(property);
                }
            }
        }
        notifyDataSetChanged();
    }

    // Filter properties by price range
    public void filterByPriceRange(double maxPrice) {
        propertyList.clear();
        for (Property property : originalList) {
            if (property.getPrice() <= maxPrice) {
                propertyList.add(property);
            }
        }
        notifyDataSetChanged();
    }

    // Filter properties by location
    public void filterByLocation(String location) {
        propertyList.clear();
        if (location.isEmpty()) {
            propertyList.addAll(originalList);
        } else {
            for (Property property : originalList) {
                if (property.getCountry().toLowerCase().contains(location.toLowerCase())) {
                    propertyList.add(property);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class PropertyViewHolder extends RecyclerView.ViewHolder {
        ImageView propertyImage;
        TextView propertyTitle, propertyDescription, propertyPrice, propertyLocation, propertyType, propertyInfo;
        Button favoriteButton, reserveButton;

        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            propertyImage = itemView.findViewById(R.id.property_image);
            propertyTitle = itemView.findViewById(R.id.property_title);
            propertyDescription = itemView.findViewById(R.id.property_description);
            propertyPrice = itemView.findViewById(R.id.property_price);
            propertyLocation = itemView.findViewById(R.id.property_location);
            propertyType = itemView.findViewById(R.id.property_type);
            propertyInfo = itemView.findViewById(R.id.property_info);
            favoriteButton = itemView.findViewById(R.id.btn_favorite);
            reserveButton = itemView.findViewById(R.id.btn_reserve);
        }
    }

    public interface OnPropertyInteractionListener {
        void onAddToFavorites(Property property);
        void onRemoveFromFavorites(Property property);
        void onReserveProperty(Property property);
    }
}
