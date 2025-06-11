package com.example.a1210733_1211088_courseproject.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
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
import com.example.a1210733_1211088_courseproject.database.sql.ReservationQueries;
import com.example.a1210733_1211088_courseproject.utils.SharedPrefManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReservationFragment extends Fragment {

    private TextView propertyTitleTextView;
    private TextView reservationDateTextView;
    private TextView reservationTimeTextView;
    private Button datePickerButton;
    private Button timePickerButton;
    private Button confirmReservationButton;

    private long propertyId;
    private String propertyTitle;
    private Calendar selectedDateTime;
    private DataBaseHelper dbHelper;
    private SharedPrefManager prefManager;
    private long currentUserId;

    public ReservationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            propertyId = getArguments().getLong("propertyId", -1);
            propertyTitle = getArguments().getString("propertyTitle", "");
        }
        selectedDateTime = Calendar.getInstance();

        // Initialize database helper
        dbHelper = new DataBaseHelper(getContext(), "RealEstate", null, 1);

        // Get current user ID
        prefManager = SharedPrefManager.getInstance(getContext());
        currentUserId = prefManager.getCurrentUserId();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation, container, false);

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
    }

    private void confirmReservation() {
        // Check if date and time are selected
        if (reservationDateTextView.getText().toString().isEmpty() ||
                reservationTimeTextView.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Please select both date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        // Format date and time for database
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String dateTimeStr = dateTimeFormat.format(selectedDateTime.getTime());

        // Save the reservation to database
        boolean success = saveReservationToDatabase(currentUserId, propertyId, dateTimeStr);

        if (success) {
            // Show confirmation message
            String confirmationMessage = "Property " + propertyTitle + " reserved for " +
                    reservationDateTextView.getText() + " at " + reservationTimeTextView.getText();
            Toast.makeText(getContext(), confirmationMessage, Toast.LENGTH_LONG).show();

            // Navigate back to the properties fragment
            getParentFragmentManager().popBackStack();
        } else {
            Toast.makeText(getContext(), "Failed to save reservation. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Saves a reservation to the database
     * @param userId The user ID making the reservation
     * @param propertyId The property ID being reserved
     * @param dateTime The date and time of the reservation
     * @return true if successful, false otherwise
     */
    private boolean saveReservationToDatabase(long userId, long propertyId, String dateTime) {
        try {
            ContentValues values = new ContentValues();
            values.put(ReservationQueries.COLUMN_USER_ID, userId);
            values.put(ReservationQueries.COLUMN_PROPERTY_ID, propertyId);
            values.put(ReservationQueries.COLUMN_RESERVATION_DATE, dateTime);
            values.put(ReservationQueries.COLUMN_STATUS, "Pending"); // Default status

            // Insert into database
            long id = dbHelper.getWritableDatabase().insert(
                    ReservationQueries.TABLE_NAME,
                    null,
                    values);

            return id != -1; // Return true if insertion was successful
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
