package com.example.a1210733_1211088_courseproject.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.res.ResourcesCompat;

import com.example.a1210733_1211088_courseproject.R;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Typeface wastedVindey = ResourcesCompat.getFont(requireContext(), R.font.wasted_vindey);

        TextView aboutUsTitle = view.findViewById(R.id.about_us_title);
        TextView aboutUsContent = view.findViewById(R.id.about_us_content);
        TextView companyHistoryTitle = view.findViewById(R.id.company_history_title);
        TextView companyHistoryContent = view.findViewById(R.id.company_history_content);

        aboutUsTitle.setTypeface(wastedVindey);
        aboutUsContent.setTypeface(wastedVindey);
        companyHistoryTitle.setTypeface(wastedVindey);
        companyHistoryContent.setTypeface(wastedVindey);

        return view;
    }
}
