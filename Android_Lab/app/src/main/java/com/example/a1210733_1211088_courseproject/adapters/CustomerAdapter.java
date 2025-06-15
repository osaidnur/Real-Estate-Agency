package com.example.a1210733_1211088_courseproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1210733_1211088_courseproject.R;
import com.example.a1210733_1211088_courseproject.models.User;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {
    
    private Context context;
    private List<User> customersList;
    private OnCustomerInteractionListener listener;

    public interface OnCustomerInteractionListener {
        void onDeleteCustomer(User customer);
    }

    public CustomerAdapter(Context context, List<User> customersList, OnCustomerInteractionListener listener) {
        this.context = context;
        this.customersList = customersList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        User customer = customersList.get(position);
          // Set customer details
        holder.customerName.setText(customer.getFirstName() + " " + customer.getLastName());
        holder.customerEmail.setText(customer.getEmail());
          // Set customer details (Phone, Location & Gender) on separate lines
        StringBuilder details = new StringBuilder();
        
        if (customer.getCountry() != null && !customer.getCountry().isEmpty()) {
            details.append(customer.getCountry());
            if (customer.getCity() != null && !customer.getCity().isEmpty()) {
                details.append(", ").append(customer.getCity());
            }
        }
        if (customer.getPhone() != null && !customer.getPhone().isEmpty()) {
            if (details.length() > 0) details.append("\n");
            details.append("Phone: ").append(customer.getPhone());
        }
        if (customer.getGender() != null && !customer.getGender().isEmpty()) {
            if (details.length() > 0) details.append("\n");
            details.append("Gender: ").append(customer.getGender());
        }
        holder.customerDetails.setText(details.length() > 0 ? details.toString() : "No additional details");
        
        // Set role badge
        holder.customerRoleBadge.setText("CUSTOMER");
        
        // Set profile icon (default)
        holder.profileIcon.setImageResource(R.drawable.ic_person);
        
        // Set delete button click listener
        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteCustomer(customer);
            }
        });
    }

    @Override
    public int getItemCount() {
        return customersList.size();
    }    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        ImageView profileIcon;
        TextView customerName, customerEmail, customerDetails, customerRoleBadge;
        Button deleteButton;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            profileIcon = itemView.findViewById(R.id.customer_profile_icon);
            customerName = itemView.findViewById(R.id.customer_name);
            customerEmail = itemView.findViewById(R.id.customer_email);
            customerDetails = itemView.findViewById(R.id.customer_details);
            customerRoleBadge = itemView.findViewById(R.id.customer_role_badge);
            deleteButton = itemView.findViewById(R.id.btn_delete_customer);
        }
    }
}
