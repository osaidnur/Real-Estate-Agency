package com.example.a1210733_1211088_courseproject.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.a1210733_1211088_courseproject.R;
import com.example.a1210733_1211088_courseproject.database.DataBaseHelper;
import com.example.a1210733_1211088_courseproject.database.sql.ReservationQueries;
import com.example.a1210733_1211088_courseproject.database.sql.PropertyQueries;
import com.example.a1210733_1211088_courseproject.models.Property;
import com.example.a1210733_1211088_courseproject.utils.SharedPrefManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReservationFragment extends Fragment {    private TextView propertyTitleTextView;
    private TextView propertyLocationTextView;
    private TextView propertyPriceTextView;
    private TextView propertyCategoryTextView;
    private TextView propertyRoomsTextView;
    private TextView propertyAreaTextView;
    private TextView propertySpecialTextView;
    private LinearLayout specialPropertyLayout;
    private TextView reservationDateTextView;
    private TextView reservationTimeTextView;    private Button datePickerButton;
    private Button timePickerButton;
    private Button confirmReservationButton;
    private Button backButton;

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
    }    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation, container, false);        // Initialize views
        propertyTitleTextView = view.findViewById(R.id.property_title);
        propertyLocationTextView = view.findViewById(R.id.property_location);
        propertyPriceTextView = view.findViewById(R.id.property_price);
        propertyCategoryTextView = view.findViewById(R.id.property_category);
        propertyRoomsTextView = view.findViewById(R.id.property_rooms);
        propertyAreaTextView = view.findViewById(R.id.property_area);
        propertySpecialTextView = view.findViewById(R.id.property_special);
        specialPropertyLayout = view.findViewById(R.id.special_property_layout);
        reservationDateTextView = view.findViewById(R.id.reservation_date);
        reservationTimeTextView = view.findViewById(R.id.reservation_time);
        datePickerButton = view.findViewById(R.id.btn_pick_date);
        timePickerButton = view.findViewById(R.id.btn_pick_time);
        confirmReservationButton = view.findViewById(R.id.btn_confirm_reservation);
        backButton = view.findViewById(R.id.btn_back);        // Set up back button
        backButton.setOnClickListener(v -> {
            if (getParentFragmentManager() != null) {
                // Pop the back stack to return to the previous fragment
                getParentFragmentManager().popBackStack();
            }
        });

        // Set property details
        loadPropertyDetails();

        // Set up date picker
        datePickerButton.setOnClickListener(v -> showDatePicker());

        // Set up time picker
        timePickerButton.setOnClickListener(v -> showTimePicker());        // Set up confirm button
        confirmReservationButton.setOnClickListener(v -> confirmReservation());

        // Start content animation after a short delay
        view.postDelayed(() -> animateContent(view), 100);

        return view;
    }    /**
     * Animates the content elements with staggered fade-in animations
     */
    private void animateContent(View rootView) {
        // Find the main content containers by their IDs
        View propertyCard = rootView.findViewById(R.id.property_details_card);
        View dateTimeCard = rootView.findViewById(R.id.datetime_selection_card);
        View buttonsLayout = rootView.findViewById(R.id.buttons_layout);
        
        // Create staggered animations
        if (propertyCard != null) {
            Animation fadeIn1 = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_up);
            fadeIn1.setStartOffset(0);
            propertyCard.startAnimation(fadeIn1);
        }
        
        if (dateTimeCard != null) {
            Animation fadeIn2 = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_up);
            fadeIn2.setStartOffset(150);
            dateTimeCard.startAnimation(fadeIn2);
        }
        
        if (buttonsLayout != null) {
            Animation fadeIn3 = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in_up);
            fadeIn3.setStartOffset(300);
            buttonsLayout.startAnimation(fadeIn3);
            
            // Add pulsating animation to confirm button after content is loaded
            buttonsLayout.postDelayed(() -> {
                if (confirmReservationButton != null) {
                    Animation pulseAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.button_pulse);
                    confirmReservationButton.startAnimation(pulseAnimation);
                }
            }, 800); // Start pulsing after content animation completes
        }
    }private void loadPropertyDetails() {
        if (propertyId == -1) return;
        
        try {
            // Use the proper database method to get property by ID
            Property property = dbHelper.getPropertyById(propertyId);
            
            if (property != null) {
                // Set property title
                propertyTitleTextView.setText(property.getTitle());
                
                // Set location (city, country)
                String location = property.getCity() + ", " + property.getCountry();
                propertyLocationTextView.setText(location);
                
                // Set price with discount handling
                double displayPrice = property.getDiscountedPrice();
                String priceText;
                if (property.isSpecial() && property.getDiscount() > 0) {
                    priceText = String.format(Locale.getDefault(), "$%.2f (%.0f%% off)", 
                            displayPrice, property.getDiscount());
                } else {
                    priceText = String.format(Locale.getDefault(), "$%.2f", displayPrice);
                }
                propertyPriceTextView.setText(priceText);
                
                // Set property type/category
                propertyCategoryTextView.setText(property.getType());
                
                // Set rooms and bathrooms information
                String roomsInfo = property.getBedrooms() + " bedrooms, " + property.getBathrooms() + " bathrooms";
                propertyRoomsTextView.setText(roomsInfo);
                
                // Set area information
                String areaInfo = String.format(Locale.getDefault(), "%.1f m²", property.getArea());
                propertyAreaTextView.setText(areaInfo);
                
                // Handle special property badge
                if (property.isSpecial()) {
                    specialPropertyLayout.setVisibility(View.VISIBLE);
                    if (property.getDiscount() > 0) {
                        propertySpecialTextView.setText(String.format(Locale.getDefault(), 
                                "Special Offer! %.0f%% OFF", property.getDiscount()));
                    } else {
                        propertySpecialTextView.setText("Featured Property");
                    }
                } else {
                    specialPropertyLayout.setVisibility(View.GONE);
                }
                
            } else {
                // Fallback if property not found
                setFallbackPropertyDetails();
            }
        } catch (Exception e) {
            Log.e("ReservationFragment", "Error loading property details: " + e.getMessage());
            setFallbackPropertyDetails();
        }
    }
    
    private void setFallbackPropertyDetails() {
        propertyTitleTextView.setText(propertyTitle.isEmpty() ? "Property Details" : propertyTitle);
        propertyLocationTextView.setText("Location information available upon contact");
        propertyPriceTextView.setText("Contact for pricing");
        propertyCategoryTextView.setText("Property details");
        propertyRoomsTextView.setText("Room details available");
        propertyAreaTextView.setText("Area information available");
        specialPropertyLayout.setVisibility(View.GONE);
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
