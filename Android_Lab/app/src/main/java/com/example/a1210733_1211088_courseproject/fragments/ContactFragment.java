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

public class ContactFragment extends Fragment {

    private Button callButton;
    private Button emailButton;
    private Button locateButton;

    private static final String AGENCY_PHONE = "+970599000000";
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
        super.onViewCreated(view, savedInstanceState);

        callButton = view.findViewById(R.id.btn_call);
        emailButton = view.findViewById(R.id.btn_email);
        locateButton = view.findViewById(R.id.btn_locate);

        callButton.setOnClickListener(v -> makePhoneCall());
        emailButton.setOnClickListener(v -> sendEmail());
        locateButton.setOnClickListener(v -> openMap());
    }

    private void makePhoneCall() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + AGENCY_PHONE));

        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "No application can handle this action",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + AGENCY_EMAIL));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Inquiry from Real Estate Hub App");

        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "No application can handle this action",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void openMap() {
        Uri gmmIntentUri = Uri.parse("geo:" + AGENCY_LAT + "," + AGENCY_LNG + "?q=" + AGENCY_LAT + "," + AGENCY_LNG + "(Real Estate Hub Agency)");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // If Google Maps is not installed, open location in browser
            Uri browserUri = Uri.parse("https://maps.google.com/maps?q=" + AGENCY_LAT + "," + AGENCY_LNG);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, browserUri);
            startActivity(browserIntent);
        }
    }
}
