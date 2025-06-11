package com.example.a1210733_1211088_courseproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1210733_1211088_courseproject.R;
import com.example.a1210733_1211088_courseproject.models.Property;

import java.util.List;

public class SpecialOfferAdapter extends RecyclerView.Adapter<SpecialOfferAdapter.SpecialOfferViewHolder> {
    
    private Context context;
    private List<Property> specialOffersList;
    private OnSpecialOfferActionListener listener;
    
    public interface OnSpecialOfferActionListener {
        void onRemoveSpecialOffer(Property property);
        void onViewPropertyDetails(Property property);
    }
    
    public SpecialOfferAdapter(Context context, List<Property> specialOffersList, OnSpecialOfferActionListener listener) {
        this.context = context;
        this.specialOffersList = specialOffersList;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public SpecialOfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_special_offer, parent, false);
        return new SpecialOfferViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull SpecialOfferViewHolder holder, int position) {
        Property property = specialOffersList.get(position);
        
        holder.tvTitle.setText(property.getTitle());
        holder.tvType.setText(property.getType());
        holder.tvOriginalPrice.setText(String.format("Original: $%.2f", property.getPrice()));
        holder.tvDiscountedPrice.setText(String.format("Sale Price: $%.2f", property.getDiscountedPrice()));
        holder.tvDiscount.setText(String.format("%.1f%% OFF", property.getDiscount()));
        holder.tvLocation.setText(property.getCity() + ", " + property.getCountry());
        holder.tvSavings.setText(String.format("Save $%.2f", property.getPrice() - property.getDiscountedPrice()));
        
        // Set click listeners
        holder.btnRemoveOffer.setOnClickListener(v -> listener.onRemoveSpecialOffer(property));
        holder.btnViewDetails.setOnClickListener(v -> listener.onViewPropertyDetails(property));
        
        // Make the entire card clickable for details
        holder.cardView.setOnClickListener(v -> listener.onViewPropertyDetails(property));
    }
    
    @Override
    public int getItemCount() {
        return specialOffersList.size();
    }
    
    public static class SpecialOfferViewHolder extends RecyclerView.ViewHolder {
        
        CardView cardView;
        TextView tvTitle, tvType, tvOriginalPrice, tvDiscountedPrice, tvDiscount, tvLocation, tvSavings;
        Button btnRemoveOffer, btnViewDetails;
        
        public SpecialOfferViewHolder(@NonNull View itemView) {
            super(itemView);
            
            cardView = itemView.findViewById(R.id.special_offer_card);
            tvTitle = itemView.findViewById(R.id.special_offer_title);
            tvType = itemView.findViewById(R.id.special_offer_type);
            tvOriginalPrice = itemView.findViewById(R.id.special_offer_original_price);
            tvDiscountedPrice = itemView.findViewById(R.id.special_offer_discounted_price);
            tvDiscount = itemView.findViewById(R.id.special_offer_discount);
            tvLocation = itemView.findViewById(R.id.special_offer_location);
            tvSavings = itemView.findViewById(R.id.special_offer_savings);
            btnRemoveOffer = itemView.findViewById(R.id.btn_remove_special_offer);
            btnViewDetails = itemView.findViewById(R.id.btn_view_offer_details);
        }
    }
}
