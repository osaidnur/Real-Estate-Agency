package com.example.a1210733_1211088_courseproject.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1210733_1211088_courseproject.R;
import com.example.a1210733_1211088_courseproject.activities.AddAdminActivity;
import com.example.a1210733_1211088_courseproject.adapters.AdminAdapter;
import com.example.a1210733_1211088_courseproject.database.DataBaseHelper;
import com.example.a1210733_1211088_courseproject.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class AdminsFragment extends Fragment implements AdminAdapter.OnAdminInteractionListener {

    private static final String TAG = "AdminsFragment";
      private RecyclerView adminsRecyclerView;
    private AdminAdapter adminAdapter;
    private TextView emptyStateText;
    private FloatingActionButton fabAddAdmin;
    private DataBaseHelper dbHelper;
    private List<User> adminsList;
    private long currentAdminId = -1; // ID of the currently logged-in admin

    public AdminsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admins, container, false);
    }    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get current admin ID from arguments
        if (getArguments() != null) {
            currentAdminId = getArguments().getLong("current_admin_id", -1);
        }

        // Initialize database helper
        dbHelper = new DataBaseHelper(getContext(), "RealEstate", null, 1);

        // Initialize views
        initializeViews(view);

        // Set up RecyclerView
        setupRecyclerView();

        // Set up floating action button
        setupFAB();

        // Load admins data
        loadAdmins();
    }

    private void initializeViews(View view) {
        adminsRecyclerView = view.findViewById(R.id.admins_recycler);
        emptyStateText = view.findViewById(R.id.empty_admins_text);
        fabAddAdmin = view.findViewById(R.id.fab_add_admin);
    }    private void setupRecyclerView() {
        adminsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adminsList = new ArrayList<>();
        adminAdapter = new AdminAdapter(getContext(), adminsList, this, currentAdminId);
        adminsRecyclerView.setAdapter(adminAdapter);
    }

    private void setupFAB() {
        fabAddAdmin.setOnClickListener(v -> {
            // Navigate to Add Admin Activity
            Intent intent = new Intent(getContext(), AddAdminActivity.class);
            startActivityForResult(intent, 100);
        });
    }

    private void loadAdmins() {
        if (dbHelper != null) {
            adminsList.clear();
            adminsList.addAll(dbHelper.getAllAdmins());
            
            if (adminsList.isEmpty()) {
                adminsRecyclerView.setVisibility(View.GONE);
                emptyStateText.setVisibility(View.VISIBLE);
                emptyStateText.setText("No admins found.\nUse the + button to add an admin.");
            } else {
                adminsRecyclerView.setVisibility(View.VISIBLE);
                emptyStateText.setVisibility(View.GONE);
                adminAdapter.notifyDataSetChanged();
            }
        }
    }    @Override
    public void onDeleteAdmin(User admin) {
        // Prevent deletion of the current admin account
        if (admin.getEmail().equals("admin@admin.com")) {
            Toast.makeText(getContext(), "Cannot delete the default admin account!", 
                          Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Prevent admin from deleting themselves
        if (admin.getUserId() == currentAdminId) {
            Toast.makeText(getContext(), "You cannot delete your own admin account!", 
                          Toast.LENGTH_SHORT).show();
            return;
        }
          // Show confirmation dialog
        androidx.appcompat.app.AlertDialog.Builder builder = 
            new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Delete Admin");
        builder.setMessage("Are you sure you want to delete admin: " + 
                          admin.getFirstName() + " " + admin.getLastName() + "?");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            if (dbHelper.deleteUser(admin.getUserId())) {
                Toast.makeText(getContext(), "Admin deleted successfully", 
                              Toast.LENGTH_SHORT).show();
                loadAdmins(); // Refresh the list
            } else {
                Toast.makeText(getContext(), "Failed to delete admin", 
                              Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", null);
        
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
        
        // Reset button fonts to default system font
        if (dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE) != null) {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setTypeface(android.graphics.Typeface.DEFAULT);
        }
        if (dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE) != null) {
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE).setTypeface(android.graphics.Typeface.DEFAULT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == getActivity().RESULT_OK) {
            // Admin was added successfully, refresh the list
            loadAdmins();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh admins list when fragment becomes visible
        loadAdmins();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
