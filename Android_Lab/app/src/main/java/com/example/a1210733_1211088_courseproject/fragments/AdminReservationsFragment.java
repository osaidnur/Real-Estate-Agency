package com.example.a1210733_1211088_courseproject.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1210733_1211088_courseproject.R;
import com.example.a1210733_1211088_courseproject.adapters.AdminReservationAdapter;
import com.example.a1210733_1211088_courseproject.database.DataBaseHelper;
import com.example.a1210733_1211088_courseproject.models.Property;
import com.example.a1210733_1211088_courseproject.models.Reservation;
import com.example.a1210733_1211088_courseproject.models.User;

import java.util.ArrayList;
import java.util.List;

public class AdminReservationsFragment extends Fragment implements AdminReservationAdapter.OnReservationActionListener {
    
    private static final String TAG = "AdminReservationsFragment";
    
    private RecyclerView recyclerView;
    private TextView emptyView;
    private AdminReservationAdapter adapter;
    private DataBaseHelper dbHelper;
    private List<AdminReservationAdapter.ReservationWithDetails> reservationsList;

    public AdminReservationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_reservations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        recyclerView = view.findViewById(R.id.admin_reservations_recycler);
        emptyView = view.findViewById(R.id.admin_reservations_empty_view);
        
        // Initialize database helper
        dbHelper = new DataBaseHelper(getContext(), "RealEstate", null, 1);
        
        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        // Load reservations
        loadReservations();
    }

    private void loadReservations() {
        reservationsList = new ArrayList<>();
        
        try {
            // Get all reservations from database
            List<Reservation> reservations = dbHelper.getAllReservations();
            
            if (reservations != null && !reservations.isEmpty()) {
                emptyView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                
                for (Reservation reservation : reservations) {
                    // Get associated property and user details
                    Property property = dbHelper.getPropertyById(reservation.getPropertyId());
                    User user = dbHelper.getUserById(reservation.getUserId());
                    
                    if (property != null && user != null) {
                        reservationsList.add(new AdminReservationAdapter.ReservationWithDetails(
                            reservation, property, user));
                    }
                }
                
                // Set up adapter
                adapter = new AdminReservationAdapter(getContext(), reservationsList, this);
                recyclerView.setAdapter(adapter);
                
                Log.d(TAG, "Loaded " + reservationsList.size() + " reservations");
                
            } else {
                // No reservations found
                emptyView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                Log.d(TAG, "No reservations found");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading reservations", e);
            Toast.makeText(getContext(), "Error loading reservations", Toast.LENGTH_SHORT).show();
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConfirmReservation(Reservation reservation) {
        showConfirmationDialog(
            "Confirm Reservation",
            "Are you sure you want to confirm this reservation?",
            () -> updateReservationStatus(reservation, "confirmed")
        );
    }

    @Override
    public void onRejectReservation(Reservation reservation) {
        showConfirmationDialog(
            "Reject Reservation", 
            "Are you sure you want to reject this reservation?",
            () -> updateReservationStatus(reservation, "cancelled")
        );
    }

    private void showConfirmationDialog(String title, String message, Runnable onConfirm) {
        new AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes", (dialog, which) -> onConfirm.run())
            .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
            .show();
    }

    private void updateReservationStatus(Reservation reservation, String newStatus) {
        try {
            boolean success = dbHelper.updateReservationStatus(reservation.getReservationId(), newStatus);
            
            if (success) {
                String message = newStatus.equals("confirmed") ? 
                    "Reservation confirmed successfully" : 
                    "Reservation rejected successfully";
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                
                // Reload reservations to reflect the change
                loadReservations();
                
                Log.d(TAG, "Updated reservation " + reservation.getReservationId() + " to " + newStatus);
            } else {
                Toast.makeText(getContext(), "Failed to update reservation", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to update reservation " + reservation.getReservationId());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating reservation status", e);
            Toast.makeText(getContext(), "Error updating reservation", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh reservations when fragment becomes visible
        if (dbHelper != null) {
            loadReservations();
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
