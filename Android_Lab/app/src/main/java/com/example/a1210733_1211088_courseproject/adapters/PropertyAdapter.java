package com.example.a1210733_1211088_courseproject.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1210733_1211088_courseproject.R;
import com.example.a1210733_1211088_courseproject.database.DataBaseHelper;
import com.example.a1210733_1211088_courseproject.database.sql.FavoriteQueries;
import com.example.a1210733_1211088_courseproject.models.Property;
import com.example.a1210733_1211088_courseproject.utils.SharedPrefManager;
import com.example.a1210733_1211088_courseproject.utils.ImageUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
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
    }    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_property, parent, false);
        return new PropertyViewHolder(view);
    }    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        Property property = propertyList.get(position);

        // Load image from URL or local file using ImageUtils
        ImageUtils.loadPropertyImage(context, property.getImageUrl(), holder.propertyImage);

        holder.propertyTitle.setText(property.getTitle());
        holder.propertyDescription.setText(property.getDescription());
        holder.propertyType.setText(property.getType());

        // Set area with formatting
        holder.propertyArea.setText(String.format("%.0f m²", property.getArea()));

        // Set location with city and country
        String location = "";
        if (property.getCity() != null && !property.getCity().isEmpty()) {
            location = property.getCity();
            if (property.getCountry() != null && !property.getCountry().isEmpty()) {
                location += ", " + property.getCountry();
            }
        } else if (property.getCountry() != null && !property.getCountry().isEmpty()) {
            location = property.getCountry();
        }
        holder.propertyLocation.setText(location);

        // Property info (bedrooms/bathrooms)
        holder.propertyInfo.setText(String.format("%d bed, %d bath",
                property.getBedrooms(), property.getBathrooms()));

        // Handle special offers and pricing
        if (property.isSpecial() && property.getDiscount() > 0) {
            // Show special offer section
            holder.specialOfferSection.setVisibility(View.VISIBLE);
            holder.regularPriceSection.setVisibility(View.GONE);
            
            // Set discount badge
            holder.discountBadge.setText(String.format("%.0f%% OFF", property.getDiscount()));
            
            // Calculate prices
            double originalPriceValue = property.getPrice();
            double discountAmount = originalPriceValue * (property.getDiscount() / 100);
            double salePriceValue = originalPriceValue - discountAmount;
            
            // Set original price with strikethrough
            holder.originalPrice.setText(String.format("$%.2f", originalPriceValue));
            holder.originalPrice.setPaintFlags(holder.originalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            
            // Set sale price
            holder.salePrice.setText(String.format("$%.2f", salePriceValue));
            
            // Set savings amount
            holder.savingsAmount.setText(String.format("You save $%.2f!", discountAmount));
        } else {
            // Show regular price section
            holder.specialOfferSection.setVisibility(View.GONE);
            holder.regularPriceSection.setVisibility(View.VISIBLE);
            
            // Set regular price
            holder.propertyPrice.setText(String.format("$%.2f", property.getPrice()));
        }

        // Check if property is in user's favorites
        boolean isFavorite = isPropertyInFavorites(property.getPropertyId());
        updateFavoriteButton(holder.favoriteButton, isFavorite);        // Set click listeners
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

            // Update UI with animation
            animateHeartButton(holder.favoriteButton, newFavoriteStatus);

            // Notify listener
            if (listener != null) {
                if (newFavoriteStatus) {
                    listener.onAddToFavorites(property);
                } else {
                    listener.onRemoveFromFavorites(property);
                }
            }
        });        holder.reserveButton.setOnClickListener(v -> {
            // Add enhanced button press animation
            Animation pressAnimation = AnimationUtils.loadAnimation(context, R.anim.reserve_button_enhanced);
            v.startAnimation(pressAnimation);
            
            // Delay the navigation to allow animation to complete
            v.postDelayed(() -> {
                if (listener != null) {
                    listener.onReserveProperty(property);
                }
            }, 300); // Delay matches animation duration
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
    }    private void updateFavoriteButton(ImageButton button, boolean isFavorite) {
        button.setSelected(isFavorite);
        if (isFavorite) {
            button.setContentDescription("Remove from favorites");
        } else {
            button.setContentDescription("Add to favorites");
        }
    }
    
    private void animateHeartButton(ImageButton button, boolean isFavorite) {
        // Update the button state first
        updateFavoriteButton(button, isFavorite);
        
        // Apply animation
        Animation animation;
        if (isFavorite) {
            // Heart filled - bounce animation
            animation = AnimationUtils.loadAnimation(context, R.anim.heart_bounce);
        } else {
            // Heart empty - shrink animation
            animation = AnimationUtils.loadAnimation(context, R.anim.heart_shrink);
        }
        button.startAnimation(animation);
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
    }    public static class PropertyViewHolder extends RecyclerView.ViewHolder {
        ImageView propertyImage;
        TextView propertyTitle, propertyDescription, propertyPrice, propertyLocation, propertyType, propertyInfo, propertyArea;
        TextView discountBadge, originalPrice, salePrice, savingsAmount;
        LinearLayout specialOfferSection, regularPriceSection;
        ImageButton favoriteButton;
        Button reserveButton;

        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            propertyImage = itemView.findViewById(R.id.property_image);
            propertyTitle = itemView.findViewById(R.id.property_title);
            propertyDescription = itemView.findViewById(R.id.property_description);
            propertyPrice = itemView.findViewById(R.id.property_price);
            propertyLocation = itemView.findViewById(R.id.property_location);
            propertyType = itemView.findViewById(R.id.property_type);
            propertyInfo = itemView.findViewById(R.id.property_info);
            propertyArea = itemView.findViewById(R.id.property_area);
            
            // Special offer views
            specialOfferSection = itemView.findViewById(R.id.special_offer_section);
            regularPriceSection = itemView.findViewById(R.id.regular_price_section);
            discountBadge = itemView.findViewById(R.id.discount_badge);
            originalPrice = itemView.findViewById(R.id.original_price);
            salePrice = itemView.findViewById(R.id.sale_price);
            savingsAmount = itemView.findViewById(R.id.savings_amount);
            
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
