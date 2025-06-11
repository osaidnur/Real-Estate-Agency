package com.example.a1210733_1211088_courseproject.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.a1210733_1211088_courseproject.R;

public class ContactFragment extends Fragment {    private Button callButton;
    private Button emailButton;
    private Button locateButton;private static final String AGENCY_PHONE = "+970599000000";
    private static final String AGENCY_EMAIL = "RealEstateHub@agency.com";
    // Example coordinates for a location in Palestine
    private static final double AGENCY_LAT = 31.952162;
    private static final double AGENCY_LNG = 35.233154;

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);        callButton = view.findViewById(R.id.btn_call);
        emailButton = view.findViewById(R.id.btn_email);
        locateButton = view.findViewById(R.id.btn_locate);

        callButton.setOnClickListener(v -> makePhoneCall());
        emailButton.setOnClickListener(v -> sendEmail());
        locateButton.setOnClickListener(v -> openMap());
    }    private void makePhoneCall() {
        try {
            // Use ACTION_DIAL which doesn't require special permissions
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + AGENCY_PHONE));
            
            // ACTION_DIAL should always be available on Android devices
            startActivity(intent);
            Toast.makeText(getContext(), "Opening phone dialer...", Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            // Fallback: show the phone number for manual dialing
            Toast.makeText(getContext(), 
                "Unable to open dialer. Please dial manually: " + AGENCY_PHONE,
                Toast.LENGTH_LONG).show();        }
    }
    
    private void sendEmail() {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + AGENCY_EMAIL));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Inquiry from Real Estate Hub App");
            intent.putExtra(Intent.EXTRA_TEXT, "Hello,\n\nI would like to inquire about your real estate services.\n\nBest regards,");

            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(intent);
                Toast.makeText(getContext(), "Opening email app...", Toast.LENGTH_SHORT).show();
            } else {
                // Fallback: try with generic ACTION_SEND
                Intent fallbackIntent = new Intent(Intent.ACTION_SEND);
                fallbackIntent.setType("message/rfc822");
                fallbackIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{AGENCY_EMAIL});
                fallbackIntent.putExtra(Intent.EXTRA_SUBJECT, "Inquiry from Real Estate Hub App");
                fallbackIntent.putExtra(Intent.EXTRA_TEXT, "Hello,\n\nI would like to inquire about your real estate services.\n\nBest regards,");
                
                if (fallbackIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    startActivity(Intent.createChooser(fallbackIntent, "Send Email"));
                    Toast.makeText(getContext(), "Choose an email app...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "No email app available. Please install an email app or contact us at " + AGENCY_EMAIL,
                            Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error opening email app: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }    private void openMap() {
        try {
            // Simple approach: use generic geo intent that works with any map app
            Uri geoUri = Uri.parse("geo:" + AGENCY_LAT + "," + AGENCY_LNG + "?q=" + AGENCY_LAT + "," + AGENCY_LNG + "(Real Estate Hub Agency)");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoUri);
            
            // Start the intent directly - Android will handle app selection
            startActivity(mapIntent);
            Toast.makeText(getContext(), "Opening location in maps...", Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            // If geo intent fails, try browser as fallback
            try {
                Uri browserUri = Uri.parse("https://maps.google.com/maps?q=" + AGENCY_LAT + "," + AGENCY_LNG);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, browserUri);
                startActivity(browserIntent);
                Toast.makeText(getContext(), "Opening location in browser...", Toast.LENGTH_SHORT).show();
            } catch (Exception e2) {
                // Final fallback: show coordinates
                Toast.makeText(getContext(), "Location: " + AGENCY_LAT + ", " + AGENCY_LNG + " (Real Estate Hub Agency)",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
