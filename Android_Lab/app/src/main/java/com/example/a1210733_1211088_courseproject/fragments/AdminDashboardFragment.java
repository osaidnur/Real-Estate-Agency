package com.example.a1210733_1211088_courseproject.fragments;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1210733_1211088_courseproject.R;
import com.example.a1210733_1211088_courseproject.database.DataBaseHelper;
import com.example.a1210733_1211088_courseproject.database.sql.PropertyQueries;
import com.example.a1210733_1211088_courseproject.database.sql.UserQueries;

public class AdminDashboardFragment extends Fragment {
    
    private static final String TAG = "AdminDashboard";
      private TextView tvPropertiesCount, tvUsersCount, tvCustomersCount;
    private RecyclerView rvRecentActivities;
    private Button btnQuickViewCustomers, btnQuickManageProperties;
    private DataBaseHelper dbHelper;

    public AdminDashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize database helper
        dbHelper = new DataBaseHelper(getContext(), "RealEstate", null, 1);        // Initialize views
        tvPropertiesCount = view.findViewById(R.id.tv_properties_count);
        tvUsersCount = view.findViewById(R.id.tv_users_count);
        tvCustomersCount = view.findViewById(R.id.tv_customers_count);
        rvRecentActivities = view.findViewById(R.id.rv_recent_activities);
        btnQuickViewCustomers = view.findViewById(R.id.btn_quick_view_customers);
        btnQuickManageProperties = view.findViewById(R.id.btn_quick_manage_properties);

        // Set up RecyclerView
        rvRecentActivities.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up quick action buttons
        setupQuickActions();

        // Load dashboard data
        loadDashboardData();
    }
    
    private void loadDashboardData() {
        // Load property count
        loadPropertiesCount();
        
        // Load users count
        loadUsersCount();
        
        // Load customers count
        loadCustomersCount();
        
        // Load recent activities (placeholder for now)
        setupRecentActivities();
    }
    
    private void loadPropertiesCount() {
        try {
            Cursor cursor = dbHelper.getAllProperties();
            int count = (cursor != null) ? cursor.getCount() : 0;
            tvPropertiesCount.setText(String.valueOf(count));
            
            if (cursor != null) {
                cursor.close();
            }
            
            Log.d(TAG, "Properties count loaded: " + count);
        } catch (Exception e) {
            Log.e(TAG, "Error loading properties count", e);
            tvPropertiesCount.setText("0");
        }
    }
    
    private void loadUsersCount() {
        try {
            Cursor cursor = dbHelper.getAllUsers();
            int count = (cursor != null) ? cursor.getCount() : 0;
            tvUsersCount.setText(String.valueOf(count));
            
            if (cursor != null) {
                cursor.close();
            }
            
            Log.d(TAG, "Users count loaded: " + count);
        } catch (Exception e) {
            Log.e(TAG, "Error loading users count", e);
            tvUsersCount.setText("0");
        }
    }
    
    private void loadCustomersCount() {
        try {
            int count = dbHelper.getAllCustomers().size();
            tvCustomersCount.setText(String.valueOf(count));
            
            Log.d(TAG, "Customers count loaded: " + count);
        } catch (Exception e) {
            Log.e(TAG, "Error loading customers count", e);
            tvCustomersCount.setText("0");
        }
    }
      private void setupRecentActivities() {
        // Set up RecyclerView for recent activities
        // TODO: Create adapter for recent activities
        // For now, we'll leave this as a placeholder
    }

    private void setupQuickActions() {
        btnQuickViewCustomers.setOnClickListener(v -> {
            // Navigate to customers fragment
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.admin_fragment_container, new AdminCustomersFragment())
                    .commit();
            }
        });

        btnQuickManageProperties.setOnClickListener(v -> {
            // Show properties management toast for now
            if (getContext() != null) {
                android.widget.Toast.makeText(getContext(), 
                    "Property management coming soon!", 
                    android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh dashboard data when fragment becomes visible
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
