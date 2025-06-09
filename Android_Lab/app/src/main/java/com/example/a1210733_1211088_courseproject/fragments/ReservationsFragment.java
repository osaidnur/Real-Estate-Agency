package com.example.a1210733_1211088_courseproject.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1210733_1211088_courseproject.R;
import com.example.a1210733_1211088_courseproject.database.DataBaseHelper;
import com.example.a1210733_1211088_courseproject.database.sql.PropertyQueries;
import com.example.a1210733_1211088_courseproject.database.sql.ReservationQueries;
import com.example.a1210733_1211088_courseproject.models.Property;
import com.example.a1210733_1211088_courseproject.models.Reservation;
import com.example.a1210733_1211088_courseproject.utils.SharedPrefManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReservationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReservationsAdapter adapter;
    private List<ReservationWithProperty> reservationsList;
    private TextView emptyText;
    private DataBaseHelper dbHelper;
    private SharedPrefManager prefManager;
    private long currentUserId;

    // Inner class to hold both reservation and its associated property
    private static class ReservationWithProperty {
        Reservation reservation;
        Property property;

        ReservationWithProperty(Reservation reservation, Property property) {
            this.reservation = reservation;
            this.property = property;
        }
    }

    public ReservationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reservations_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);        // Initialize database helper
        dbHelper = new DataBaseHelper(getContext(), "RealEstate", null, 1);

        // Get current user ID from SharedPreferences
        prefManager = SharedPrefManager.getInstance(getContext());
        currentUserId = prefManager.getCurrentUserId();

        recyclerView = view.findViewById(R.id.reservations_recycler);
        emptyText = view.findViewById(R.id.empty_reservations_text);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch reservations from database
        loadReservations();
    }    private void loadReservations() {
        reservationsList = new ArrayList<>();

        // Get user's reservations from database using DataBaseHelper method
        List<Reservation> reservations = dbHelper.getUserReservations(currentUserId);

        if (reservations != null && !reservations.isEmpty()) {
            emptyText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            for (Reservation reservation : reservations) {
                // Get associated property
                Property property = dbHelper.getPropertyById(reservation.getPropertyId());

                if (property != null) {
                    reservationsList.add(new ReservationWithProperty(reservation, property));
                }
            }

            // Set up adapter
            adapter = new ReservationsAdapter(reservationsList);
            recyclerView.setAdapter(adapter);

        } else {
            // No reservations found
            emptyText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }    // Adapter class for reservations
    private class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.ReservationViewHolder> {
        private List<ReservationWithProperty> reservations;
        private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

        public ReservationsAdapter(List<ReservationWithProperty> reservations) {
            this.reservations = reservations;
        }

        @NonNull
        @Override
        public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_reservation, parent, false);
            return new ReservationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
            ReservationWithProperty rwp = reservations.get(position);
            Property property = rwp.property;
            Reservation reservation = rwp.reservation;

            // Set property details
            // Check if there's an image URL and load it, otherwise use placeholder
            if (property.getImageUrl() != null && !property.getImageUrl().isEmpty()) {
                // You could use a library like Picasso or Glide to load the image
                // For now, just using a placeholder
                holder.propertyImage.setImageResource(R.drawable.ic_home);
            } else {
                holder.propertyImage.setImageResource(R.drawable.ic_home);
            }            holder.propertyTitle.setText(property.getTitle());
            holder.propertyPrice.setText(String.format("$%.2f", property.getPrice()));
            holder.propertyLocation.setText(property.getCity() + ", " + property.getCountry());

            // Format and set reservation date and time
            LocalDateTime dateTime = reservation.getReservationDate();
            holder.reservationDate.setText("Date: " + dateTime.format(dateFormatter));
            holder.reservationTime.setText("Time: " + dateTime.format(timeFormatter));

            // Set status indicator based on reservation status
            String status = reservation.getStatus();
            int statusColor;

            switch (status.toLowerCase()) {
                case "confirmed":
                    statusColor = getResources().getColor(android.R.color.holo_green_dark);
                    break;
                case "pending":
                    statusColor = getResources().getColor(android.R.color.holo_orange_dark);
                    break;
                case "cancelled":
                    statusColor = getResources().getColor(android.R.color.holo_red_dark);
                    break;
                default:
                    statusColor = getResources().getColor(android.R.color.darker_gray);
                    break;
            }

            holder.reservationStatus.setTextColor(statusColor);
            holder.reservationStatus.setText("Status: " + status);
        }

        @Override
        public int getItemCount() {
            return reservations.size();
        }

        class ReservationViewHolder extends RecyclerView.ViewHolder {
            ImageView propertyImage;
            TextView propertyTitle, propertyPrice, propertyLocation;
            TextView reservationDate, reservationTime, reservationStatus;
            Button btnViewDetails, btnCancel;

            public ReservationViewHolder(@NonNull View itemView) {
                super(itemView);
                propertyImage = itemView.findViewById(R.id.property_image);
                propertyTitle = itemView.findViewById(R.id.property_title);
                propertyPrice = itemView.findViewById(R.id.property_price);
                propertyLocation = itemView.findViewById(R.id.property_location);
                reservationDate = itemView.findViewById(R.id.reservation_date);
                reservationTime = itemView.findViewById(R.id.reservation_time);
                reservationStatus = itemView.findViewById(R.id.reservation_status);
                btnViewDetails = itemView.findViewById(R.id.btn_view_details);
                btnCancel = itemView.findViewById(R.id.btn_cancel);

                // Set up click listeners for buttons
                btnViewDetails.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        ReservationWithProperty rwp = reservations.get(position);
                        Toast.makeText(itemView.getContext(),
                                "Viewing details for: " + rwp.property.getTitle(),
                                Toast.LENGTH_SHORT).show();
                        // Future enhancement: Navigate to reservation detail screen
                    }
                });

                btnCancel.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        ReservationWithProperty rwp = reservations.get(position);
                        // Show confirmation dialog
                        showCancelConfirmationDialog(rwp.reservation);
                    }
                });
            }
        }
    }

    /**
     * Shows a confirmation dialog before cancelling a reservation
     * @param reservation The reservation to cancel
     */
    private void showCancelConfirmationDialog(Reservation reservation) {
        // Create an AlertDialog
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Cancel Reservation");
        builder.setMessage("Are you sure you want to cancel this reservation?");

        // Add the buttons
        builder.setPositiveButton("Yes", (dialog, id) -> {
            // User clicked Yes button - cancel the reservation
            cancelReservation(reservation);
        });
        builder.setNegativeButton("No", (dialog, id) -> {
            // User cancelled the dialog
            dialog.dismiss();
        });

        // Create and show the AlertDialog
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Cancels a reservation by updating its status in the database
     * @param reservation The reservation to cancel
     */
    private void cancelReservation(Reservation reservation) {
        // Update reservation status to "cancelled" in database
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ReservationQueries.COLUMN_STATUS, "cancelled");

        String selection = ReservationQueries.COLUMN_RESERVATION_ID + " = ?";
        String[] selectionArgs = { String.valueOf(reservation.getReservationId()) };

        int count = db.update(
                ReservationQueries.TABLE_NAME,
                values,
                selection,
                selectionArgs);

        if (count > 0) {
            Toast.makeText(getContext(), "Reservation cancelled successfully", Toast.LENGTH_SHORT).show();
            // Reload reservations to reflect the change
            loadReservations();
        } else {
            Toast.makeText(getContext(), "Failed to cancel reservation", Toast.LENGTH_SHORT).show();
        }
    }
}
