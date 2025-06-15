package com.example.a1210733_1211088_courseproject.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1210733_1211088_courseproject.R;
import com.example.a1210733_1211088_courseproject.models.Property;

import java.util.List;

public class AdminPropertyAdapter extends RecyclerView.Adapter<AdminPropertyAdapter.PropertyViewHolder> {
    
    private Context context;
    private List<Property> propertyList;
    private OnPropertyActionListener listener;
    
    public interface OnPropertyActionListener {
        void onAddSpecialOffer(Property property);
        void onRemoveSpecialOffer(Property property);
    }
    
    public AdminPropertyAdapter(Context context, List<Property> propertyList, OnPropertyActionListener listener) {
        this.context = context;
        this.propertyList = propertyList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_property, parent, false);
        return new PropertyViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        Property property = propertyList.get(position);
        
        holder.tvTitle.setText(property.getTitle());
        holder.tvType.setText(property.getType());
        holder.tvPrice.setText(String.format("$%.2f", property.getPrice()));
        holder.tvLocation.setText(property.getCity() + ", " + property.getCountry());
        holder.tvDescription.setText(property.getDescription());
        holder.tvDetails.setText(String.format("%d bed • %d bath • %.0f sq ft", 
                property.getBedrooms(), property.getBathrooms(), property.getArea()));
        
        // Handle special offer status
        if (property.isSpecial()) {
            holder.tvSpecialStatus.setText(String.format("SPECIAL OFFER: %.1f%% OFF", property.getDiscount()));
            holder.tvSpecialStatus.setVisibility(View.VISIBLE);
            holder.tvDiscountedPrice.setText(String.format("Discounted: $%.2f", property.getDiscountedPrice()));
            holder.tvDiscountedPrice.setVisibility(View.VISIBLE);
            holder.btnSpecialOffer.setText("Remove Offer");
            holder.cardView.setCardElevation(8f);
            // Highlight special properties
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.special_offer_background, null));
        } else {
            holder.tvSpecialStatus.setVisibility(View.GONE);
            holder.tvDiscountedPrice.setVisibility(View.GONE);
            holder.btnSpecialOffer.setText("Add Special Offer");
            holder.cardView.setCardElevation(4f);
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.white, null));
        }
        
        // Set click listener for special offer button
        holder.btnSpecialOffer.setOnClickListener(v -> {
            if (property.isSpecial()) {
                listener.onRemoveSpecialOffer(property);
            } else {
                listener.onAddSpecialOffer(property);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return propertyList.size();
    }
    
    public static class PropertyViewHolder extends RecyclerView.ViewHolder {
        
        CardView cardView;
        TextView tvTitle, tvType, tvPrice, tvLocation, tvDescription, tvDetails;
        TextView tvSpecialStatus, tvDiscountedPrice;
        Button btnSpecialOffer;
          public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cardView = itemView.findViewById(R.id.admin_property_card);
            tvTitle = itemView.findViewById(R.id.admin_property_title);
            tvType = itemView.findViewById(R.id.admin_property_type);
            tvPrice = itemView.findViewById(R.id.admin_property_price);
            tvLocation = itemView.findViewById(R.id.admin_property_location);
            tvDescription = itemView.findViewById(R.id.admin_property_description);
            tvDetails = itemView.findViewById(R.id.admin_property_details);
            tvSpecialStatus = itemView.findViewById(R.id.admin_property_special_status);
            tvDiscountedPrice = itemView.findViewById(R.id.admin_property_discounted_price);
            btnSpecialOffer = itemView.findViewById(R.id.btn_admin_special_offer);
            
            // Set Inter Variable font for the button
            Typeface interFont = ResourcesCompat.getFont(itemView.getContext(), R.font.inter_variablefont_opsz_wght);
            if (interFont != null) {
                btnSpecialOffer.setTypeface(interFont);
            }
        }
    }
}
