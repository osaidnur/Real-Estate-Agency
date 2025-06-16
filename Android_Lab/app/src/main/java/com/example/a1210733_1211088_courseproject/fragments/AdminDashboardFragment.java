package com.example.a1210733_1211088_courseproject.fragments;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1210733_1211088_courseproject.R;
import com.example.a1210733_1211088_courseproject.adapters.CountryStatsAdapter;
import com.example.a1210733_1211088_courseproject.database.DataBaseHelper;
import com.example.a1210733_1211088_courseproject.database.sql.PropertyQueries;
import com.example.a1210733_1211088_courseproject.database.sql.UserQueries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDashboardFragment extends Fragment {
    
    private static final String TAG = "AdminDashboard";
    private TextView tvPropertiesCount, tvUsersCount, tvCustomersCount, tvReservationsCount;
    private RecyclerView rvCountries;
    
    // Gender distribution views
    private TextView tvMaleCount, tvFemaleCount, tvMalePercent, tvFemalePercent;
    private ProgressBar pbMaleProgress, pbFemaleProgress;
      // Reservation status views
    private TextView tvPendingCount, tvConfirmedCount, tvRejectedCount;
    private ProgressBar pbPendingProgress, pbConfirmedProgress, pbRejectedProgress;
    
    private DataBaseHelper dbHelper;
    private CountryStatsAdapter countryAdapter;

    public AdminDashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
    }    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize database helper
        dbHelper = new DataBaseHelper(getContext(), "RealEstate", null, 1);
          // Initialize views
        initializeViews(view);
        
        // Set up charts and RecyclerView
        setupCharts();
        setupCountriesRecyclerView();

        // Load dashboard data
        loadDashboardData();
    }    private void initializeViews(View view) {
        tvPropertiesCount = view.findViewById(R.id.tv_properties_count);
        tvUsersCount = view.findViewById(R.id.tv_users_count);
        tvCustomersCount = view.findViewById(R.id.tv_customers_count);
        tvReservationsCount = view.findViewById(R.id.tv_reservations_count);
        rvCountries = view.findViewById(R.id.rv_countries);
        
        // Gender distribution views
        tvMaleCount = view.findViewById(R.id.tv_male_count);
        tvFemaleCount = view.findViewById(R.id.tv_female_count);
        tvMalePercent = view.findViewById(R.id.tv_male_percent);
        tvFemalePercent = view.findViewById(R.id.tv_female_percent);
        pbMaleProgress = view.findViewById(R.id.pb_male_progress);
        pbFemaleProgress = view.findViewById(R.id.pb_female_progress);
          // Reservation status views
        tvPendingCount = view.findViewById(R.id.tv_pending_count);
        tvConfirmedCount = view.findViewById(R.id.tv_confirmed_count);
        tvRejectedCount = view.findViewById(R.id.tv_rejected_count);
        pbPendingProgress = view.findViewById(R.id.pb_pending_progress);
        pbConfirmedProgress = view.findViewById(R.id.pb_confirmed_progress);
        pbRejectedProgress = view.findViewById(R.id.pb_rejected_progress);
    }
      private void setupCharts() {
        // Configure progress bars for better visual appeal
        if (pbMaleProgress != null) {
            pbMaleProgress.setMax(100);
            pbMaleProgress.setProgressTintList(getResources().getColorStateList(R.color.chart_male, null));
        }
        if (pbFemaleProgress != null) {
            pbFemaleProgress.setMax(100);
            pbFemaleProgress.setProgressTintList(getResources().getColorStateList(R.color.chart_female, null));
        }
        if (pbPendingProgress != null) {
            pbPendingProgress.setMax(100);
            pbPendingProgress.setProgressTintList(getResources().getColorStateList(R.color.chart_pending, null));
        }
        if (pbConfirmedProgress != null) {
            pbConfirmedProgress.setMax(100);
            pbConfirmedProgress.setProgressTintList(getResources().getColorStateList(R.color.chart_confirmed, null));
        }
        if (pbRejectedProgress != null) {
            pbRejectedProgress.setMax(100);
            pbRejectedProgress.setProgressTintList(getResources().getColorStateList(R.color.chart_rejected, null));
        }
    }    private void setupCountriesRecyclerView() {
        countryAdapter = new CountryStatsAdapter(getContext());
        rvCountries.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCountries.setAdapter(countryAdapter);
        rvCountries.setNestedScrollingEnabled(false);
        rvCountries.setHasFixedSize(false); // Allow dynamic sizing
        
        Log.d(TAG, "Countries RecyclerView setup completed with custom wrap content");
    }
      private void loadDashboardData() {
        try {
            // Get comprehensive dashboard statistics
            DataBaseHelper.DashboardStats stats = dbHelper.getDashboardStats();
            
            // Update main statistics cards
            updateStatisticsCards(stats);
            
            // Update charts
            updateGenderChart(stats.genderDistribution);
            updateReservationChart(stats.reservationStatus);
            
            // Update countries list
            updateCountriesList(stats.customersByCountry);
            
            Log.d(TAG, "Dashboard data loaded successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error loading dashboard data", e);
            // Set default values in case of error
            setDefaultValues();
        }
    }
    
    private void updateStatisticsCards(DataBaseHelper.DashboardStats stats) {
        tvUsersCount.setText(String.valueOf(stats.totalUsers));
        tvPropertiesCount.setText(String.valueOf(stats.totalProperties));
        tvCustomersCount.setText(String.valueOf(stats.totalCustomers));
        tvReservationsCount.setText(String.valueOf(stats.totalReservations));
    }
      private void updateGenderChart(Map<String, Integer> genderData) {
        int maleCount = genderData.getOrDefault("Male", 0);
        int femaleCount = genderData.getOrDefault("Female", 0);
        int total = maleCount + femaleCount;
        
        if (total > 0) {
            // Calculate percentages
            int malePercent = (int) ((maleCount * 100.0) / total);
            int femalePercent = (int) ((femaleCount * 100.0) / total);
            
            // Update text views
            if (tvMaleCount != null) tvMaleCount.setText(String.valueOf(maleCount));
            if (tvFemaleCount != null) tvFemaleCount.setText(String.valueOf(femaleCount));
            if (tvMalePercent != null) tvMalePercent.setText(malePercent + "%");
            if (tvFemalePercent != null) tvFemalePercent.setText(femalePercent + "%");
            
            // Update progress bars
            if (pbMaleProgress != null) pbMaleProgress.setProgress(malePercent);
            if (pbFemaleProgress != null) pbFemaleProgress.setProgress(femalePercent);
        } else {
            // Show no data state
            if (tvMaleCount != null) tvMaleCount.setText("0");
            if (tvFemaleCount != null) tvFemaleCount.setText("0");
            if (tvMalePercent != null) tvMalePercent.setText("0%");
            if (tvFemalePercent != null) tvFemalePercent.setText("0%");
            if (pbMaleProgress != null) pbMaleProgress.setProgress(0);
            if (pbFemaleProgress != null) pbFemaleProgress.setProgress(0);
        }
    }    private void updateReservationChart(List<DataBaseHelper.DashboardStats.ReservationStatusInfo> reservationData) {
        // Convert status list to map for easier processing
        Map<String, Integer> statusMap = new HashMap<>();
        for (DataBaseHelper.DashboardStats.ReservationStatusInfo info : reservationData) {
            statusMap.put(info.status, info.count);
        }
        
        int pendingCount = statusMap.getOrDefault("Pending", 0);
        int confirmedCount = statusMap.getOrDefault("Confirmed", 0);
        int rejectedCount = statusMap.getOrDefault("Rejected", 0) + 
                           statusMap.getOrDefault("Cancelled", 0); // Handle both rejected and cancelled
        
        int total = pendingCount + confirmedCount + rejectedCount;
        
        if (total > 0) {
            // Calculate percentages for progress bars
            int pendingPercent = (int) ((pendingCount * 100.0) / total);
            int confirmedPercent = (int) ((confirmedCount * 100.0) / total);
            int rejectedPercent = (int) ((rejectedCount * 100.0) / total);
            
            // Update text views
            if (tvPendingCount != null) tvPendingCount.setText(String.valueOf(pendingCount));
            if (tvConfirmedCount != null) tvConfirmedCount.setText(String.valueOf(confirmedCount));
            if (tvRejectedCount != null) tvRejectedCount.setText(String.valueOf(rejectedCount));
            
            // Update progress bars
            if (pbPendingProgress != null) pbPendingProgress.setProgress(pendingPercent);
            if (pbConfirmedProgress != null) pbConfirmedProgress.setProgress(confirmedPercent);
            if (pbRejectedProgress != null) pbRejectedProgress.setProgress(rejectedPercent);
        } else {
            // Show no data state
            if (tvPendingCount != null) tvPendingCount.setText("0");
            if (tvConfirmedCount != null) tvConfirmedCount.setText("0");
            if (tvRejectedCount != null) tvRejectedCount.setText("0");
            if (pbPendingProgress != null) pbPendingProgress.setProgress(0);
            if (pbConfirmedProgress != null) pbConfirmedProgress.setProgress(0);
            if (pbRejectedProgress != null) pbRejectedProgress.setProgress(0);
        }
    }    private void updateCountriesList(Map<String, Integer> countriesData) {
        Log.d(TAG, "updateCountriesList called with " + countriesData.size() + " countries");
        for (Map.Entry<String, Integer> entry : countriesData.entrySet()) {
            Log.d(TAG, "Country from DB: " + entry.getKey() + " = " + entry.getValue());
        }
        
        countryAdapter.updateData(countriesData);
        
        // The custom WrapContentRecyclerView will automatically adjust its height
        // But let's also use the programmatic approach as backup
        rvCountries.post(() -> {
            setRecyclerViewHeightBasedOnContent();
        });
        
        Log.d(TAG, "Countries list updated - height will adjust automatically");
    }/**
     * Public method to refresh dashboard data
     * Call this method when customer data changes
     */
    public void refreshDashboardData() {
        Log.d(TAG, "Manual refresh requested");
        if (dbHelper != null) {
            loadDashboardData();
        }
    }    /**
     * Helper method to ensure RecyclerView measures properly
     */
    private void ensureRecyclerViewMeasurement() {
        if (rvCountries != null && rvCountries.getAdapter() != null) {
            rvCountries.post(() -> {
                // Force the RecyclerView to remeasure its height
                rvCountries.getLayoutManager().requestLayout();
            });
        }
    }

    /**
     * Alternative method to set RecyclerView height programmatically
     */
    private void setRecyclerViewHeightBasedOnContent() {
        if (rvCountries == null || countryAdapter == null) return;
        
        int itemCount = countryAdapter.getItemCount();
        if (itemCount == 0) {
            rvCountries.getLayoutParams().height = 0;
            rvCountries.requestLayout();
            return;
        }
        
        // Approximate height per item (item height + margins)
        int itemHeight = 80; // dp - adjust this based on your item layout
        float density = getResources().getDisplayMetrics().density;
        int totalHeightPx = (int) (itemCount * itemHeight * density);
        
        rvCountries.getLayoutParams().height = totalHeightPx;
        rvCountries.requestLayout();
        
        Log.d(TAG, "Set RecyclerView height to " + totalHeightPx + "px for " + itemCount + " items");
    }
      private void setDefaultValues() {
        tvUsersCount.setText("0");
        tvPropertiesCount.setText("0");
        tvCustomersCount.setText("0");
        tvReservationsCount.setText("0");
    }    @Override
    public void onResume() {
        super.onResume();
        // Refresh dashboard data when fragment becomes visible
        Log.d(TAG, "onResume - refreshing dashboard data");
        if (dbHelper != null) {
            loadDashboardData();
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
