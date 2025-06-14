package com.example.a1210733_1211088_courseproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1210733_1211088_courseproject.R;
import com.example.a1210733_1211088_courseproject.models.Property;
import com.example.a1210733_1211088_courseproject.models.Reservation;
import com.example.a1210733_1211088_courseproject.models.User;
import com.example.a1210733_1211088_courseproject.utils.ImageUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminReservationAdapter extends RecyclerView.Adapter<AdminReservationAdapter.ReservationViewHolder> {
    
    private Context context;
    private List<ReservationWithDetails> reservationsList;
    private OnReservationActionListener listener;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

    public interface OnReservationActionListener {
        void onConfirmReservation(Reservation reservation);
        void onRejectReservation(Reservation reservation);
    }

    public static class ReservationWithDetails {
        public Reservation reservation;
        public Property property;
        public User user;

        public ReservationWithDetails(Reservation reservation, Property property, User user) {
            this.reservation = reservation;
            this.property = property;
            this.user = user;
        }
    }

    public AdminReservationAdapter(Context context, List<ReservationWithDetails> reservationsList, 
                                 OnReservationActionListener listener) {
        this.context = context;
        this.reservationsList = reservationsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_reservation, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        ReservationWithDetails item = reservationsList.get(position);
        Reservation reservation = item.reservation;
        Property property = item.property;
        User user = item.user;        // Set property details
        holder.propertyTitle.setText(property.getTitle());
        holder.propertyPrice.setText(String.format("$%.2f", property.getPrice()));
        holder.propertyLocation.setText(property.getCity() + ", " + property.getCountry());
        
        // Load property image using ImageUtils
        ImageUtils.loadPropertyImage(context, property.getImageUrl(), holder.propertyImage);

        // Set customer details
        holder.customerName.setText(user.getFirstName() + " " + user.getLastName());
        holder.customerEmail.setText(user.getEmail());
        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            holder.customerPhone.setText(user.getPhone());
            holder.customerPhone.setVisibility(View.VISIBLE);
        } else {
            holder.customerPhone.setVisibility(View.GONE);
        }

        // Set reservation details
        holder.reservationDate.setText("Date: " + reservation.getReservationDate().format(dateFormatter));
        holder.reservationTime.setText("Time: " + reservation.getReservationDate().format(timeFormatter));
        
        // Set status with color coding
        String status = reservation.getStatus();
        holder.reservationStatus.setText("Status: " + status.toUpperCase());
        
        int statusColor;
        switch (status.toLowerCase()) {
            case "confirmed":
                statusColor = context.getResources().getColor(android.R.color.holo_green_dark);
                break;
            case "pending":
                statusColor = context.getResources().getColor(android.R.color.holo_orange_dark);
                break;
            case "cancelled":
                statusColor = context.getResources().getColor(android.R.color.holo_red_dark);
                break;
            default:
                statusColor = context.getResources().getColor(android.R.color.darker_gray);
                break;
        }
        holder.reservationStatus.setTextColor(statusColor);

        // Show/hide action buttons based on status
        if ("pending".equals(status.toLowerCase())) {
            holder.btnConfirm.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);
            
            holder.btnConfirm.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onConfirmReservation(reservation);
                }
            });
            
            holder.btnReject.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRejectReservation(reservation);
                }
            });
        } else {
            holder.btnConfirm.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
        }
    }    @Override
    public int getItemCount() {
        return reservationsList.size();
    }

    static class ReservationViewHolder extends RecyclerView.ViewHolder {
        ImageView propertyImage;
        TextView propertyTitle, propertyPrice, propertyLocation;
        TextView customerName, customerEmail, customerPhone;
        TextView reservationDate, reservationTime, reservationStatus;
        Button btnConfirm, btnReject;

        ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            
            // Property views
            propertyImage = itemView.findViewById(R.id.property_image);
            propertyTitle = itemView.findViewById(R.id.property_title);
            propertyPrice = itemView.findViewById(R.id.property_price);
            propertyLocation = itemView.findViewById(R.id.property_location);
            
            // Customer views
            customerName = itemView.findViewById(R.id.customer_name);
            customerEmail = itemView.findViewById(R.id.customer_email);
            customerPhone = itemView.findViewById(R.id.customer_phone);
            
            // Reservation views
            reservationDate = itemView.findViewById(R.id.reservation_date);
            reservationTime = itemView.findViewById(R.id.reservation_time);
            reservationStatus = itemView.findViewById(R.id.reservation_status);
            
            // Action buttons
            btnConfirm = itemView.findViewById(R.id.btn_confirm);
            btnReject = itemView.findViewById(R.id.btn_reject);
        }
    }
}
