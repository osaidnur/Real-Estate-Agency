package com.example.a1210733_1211088_courseproject.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.a1210733_1211088_courseproject.R;
import com.example.a1210733_1211088_courseproject.database.DataBaseHelper;
import com.example.a1210733_1211088_courseproject.models.Reservation;
import com.example.a1210733_1211088_courseproject.utils.SharedPrefManager;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Locale;

public class ReservationFragment extends Fragment {

    private TextView propertyTitleTextView;
    private TextView reservationDateTextView;
    private TextView reservationTimeTextView;
    private Button datePickerButton;
    private Button timePickerButton;
    private Button confirmReservationButton;    private long propertyId;
    private String propertyTitle;
    private Calendar selectedDateTime;
    
    // Database and user session
    private DataBaseHelper dbHelper;
    private SharedPrefManager prefManager;
    private long currentUserId;

    public ReservationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        if (getArguments() != null) {
            propertyId = getArguments().getLong("propertyId", -1);
            propertyTitle = getArguments().getString("propertyTitle", "");
        }
        selectedDateTime = Calendar.getInstance();
    }    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation, container, false);        // Initialize database and user session
        dbHelper = new DataBaseHelper(getContext(), "RealEstate", null, 1);
        prefManager = SharedPrefManager.getInstance(getContext());
        currentUserId = prefManager.getCurrentUserId();

        // Force database schema update to remove UNIQUE constraint (one-time fix)
        dbHelper.forceDatabaseUpdate();

        // Clean up any invalid reservations from previous bugs
        dbHelper.cleanupInvalidReservations();

        // Initialize views
        propertyTitleTextView = view.findViewById(R.id.property_title);
        reservationDateTextView = view.findViewById(R.id.reservation_date);
        reservationTimeTextView = view.findViewById(R.id.reservation_time);
        datePickerButton = view.findViewById(R.id.btn_pick_date);
        timePickerButton = view.findViewById(R.id.btn_pick_time);
        confirmReservationButton = view.findViewById(R.id.btn_confirm_reservation);

        // Set property title
        propertyTitleTextView.setText(propertyTitle);

        // Set up date picker
        datePickerButton.setOnClickListener(v -> showDatePicker());

        // Set up time picker
        timePickerButton.setOnClickListener(v -> showTimePicker());

        // Set up confirm button
        confirmReservationButton.setOnClickListener(v -> confirmReservation());

        return view;
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateTextView();
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH)
        );

        // Disable past dates
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                (view, hourOfDay, minute) -> {
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, minute);
                    updateTimeTextView();
                },
                selectedDateTime.get(Calendar.HOUR_OF_DAY),
                selectedDateTime.get(Calendar.MINUTE),
                false
        );
        timePickerDialog.show();
    }

    private void updateDateTextView() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        String dateStr = dateFormat.format(selectedDateTime.getTime());
        reservationDateTextView.setText(dateStr);
    }

    private void updateTimeTextView() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        String timeStr = timeFormat.format(selectedDateTime.getTime());
        reservationTimeTextView.setText(timeStr);
    }    private void confirmReservation() {
        // Check if date and time are selected
        if (reservationDateTextView.getText().toString().isEmpty() ||
                reservationTimeTextView.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Please select both date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if user is logged in
        if (currentUserId <= 0) {
            Toast.makeText(getContext(), "Please log in to make a reservation", Toast.LENGTH_SHORT).show();
            return;
        }        try {
            // Convert Calendar to LocalDateTime
            LocalDateTime reservationDateTime = LocalDateTime.ofInstant(
                selectedDateTime.toInstant(), 
                ZoneId.systemDefault()
            );

            // Create reservation object
            Reservation reservation = new Reservation(
                currentUserId,
                propertyId,
                reservationDateTime,
                "pending"
            );            // Save to database
            long reservationId = dbHelper.insertReservation(reservation);

            if (reservationId > 0) {
                // Success message
                String confirmationMessage = "Property " + propertyTitle + " reserved for " +
                        reservationDateTextView.getText() + " at " + reservationTimeTextView.getText();
                Toast.makeText(getContext(), confirmationMessage, Toast.LENGTH_LONG).show();

                // Navigate back to the properties fragment
                getParentFragmentManager().popBackStack();
            } else if (reservationId == -2) {
                Toast.makeText(getContext(), "This property is already reserved by another user.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to create reservation. Please try again.", Toast.LENGTH_SHORT).show();
                Log.e("ReservationFragment", "Failed to insert reservation - insertReservation returned: " + reservationId);
            }

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error creating reservation: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("ReservationFragment", "Exception in confirmReservation: " + e.getMessage(), e);
        }
    }
}
