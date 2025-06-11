package com.example.a1210733_1211088_courseproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        User customer = customersList.get(position);
        
        // Set customer details
        holder.customerName.setText(customer.getFirstName() + " " + customer.getLastName());
        holder.customerEmail.setText(customer.getEmail());
        
        // Format and set customer info
        StringBuilder info = new StringBuilder();
        if (customer.getPhone() != null && !customer.getPhone().isEmpty()) {
            info.append("Phone: ").append(customer.getPhone()).append("\n");
        }
        if (customer.getCountry() != null && !customer.getCountry().isEmpty()) {
            info.append("Location: ").append(customer.getCountry());
            if (customer.getCity() != null && !customer.getCity().isEmpty()) {
                info.append(", ").append(customer.getCity());
            }
            info.append("\n");
        }
        if (customer.getGender() != null && !customer.getGender().isEmpty()) {
            info.append("Gender: ").append(customer.getGender());
        }
        
        holder.customerInfo.setText(info.toString().trim());
        holder.customerId.setText("ID: " + customer.getUserId());
        
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
    }

    static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView customerName, customerEmail, customerInfo, customerId;
        Button deleteButton;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.customer_name);
            customerEmail = itemView.findViewById(R.id.customer_email);
            customerInfo = itemView.findViewById(R.id.customer_info);
            customerId = itemView.findViewById(R.id.customer_id);
            deleteButton = itemView.findViewById(R.id.btn_delete_customer);
        }
    }
}
