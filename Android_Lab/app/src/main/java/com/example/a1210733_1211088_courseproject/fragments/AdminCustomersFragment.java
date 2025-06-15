package com.example.a1210733_1211088_courseproject.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import com.example.a1210733_1211088_courseproject.R;
import com.example.a1210733_1211088_courseproject.adapters.CustomerAdapter;
import com.example.a1210733_1211088_courseproject.database.DataBaseHelper;
import com.example.a1210733_1211088_courseproject.models.User;

import java.util.List;

public class AdminCustomersFragment extends Fragment implements CustomerAdapter.OnCustomerInteractionListener {
    
    private RecyclerView recyclerView;
    private CustomerAdapter adapter;
    private List<User> customersList;
    private TextView emptyText;
    private DataBaseHelper dbHelper;

    public AdminCustomersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_customers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize database helper
        dbHelper = new DataBaseHelper(getContext(), "RealEstate", null, 1);

        recyclerView = view.findViewById(R.id.customers_recycler);
        emptyText = view.findViewById(R.id.empty_customers_text);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load customers from database
        loadCustomers();
    }

    /**
     * Loads customers from the database
     */
    private void loadCustomers() {
        customersList = dbHelper.getAllCustomers();

        if (customersList.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            emptyText.setText("No customers found.");
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            // Set up adapter
            adapter = new CustomerAdapter(getContext(), customersList, this);
            recyclerView.setAdapter(adapter);
        }
    }    @Override
    public void onDeleteCustomer(User customer) {
        // Show confirmation dialog
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Delete Customer")
                .setMessage("Are you sure you want to delete " + customer.getFirstName() + " " + customer.getLastName() + "?\n\nThis will also delete all their favorites and reservations.")
                .setPositiveButton("Delete", (dialogInterface, which) -> {
                    // Perform delete operation
                    boolean success = dbHelper.deleteUser(customer.getUserId());
                    
                    if (success) {
                        Toast.makeText(getContext(), 
                            "Customer " + customer.getFirstName() + " " + customer.getLastName() + " deleted successfully", 
                            Toast.LENGTH_SHORT).show();
                        
                        // Remove from UI
                        customersList.remove(customer);
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                        
                        // Update empty state
                        if (customersList.isEmpty()) {
                            emptyText.setVisibility(View.VISIBLE);
                            emptyText.setText("No customers found.");
                            recyclerView.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(getContext(), 
                            "Failed to delete customer", 
                            Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        
        dialog.show();
        
        // Reset button fonts to default system font
        if (dialog.getButton(AlertDialog.BUTTON_POSITIVE) != null) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(android.graphics.Typeface.DEFAULT);
        }
        if (dialog.getButton(AlertDialog.BUTTON_NEGATIVE) != null) {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(android.graphics.Typeface.DEFAULT);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh customers when fragment becomes visible
        if (dbHelper != null) {
            loadCustomers();
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
