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

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.AdminViewHolder> {    private Context context;
    private List<User> adminsList;
    private OnAdminInteractionListener listener;
    private long currentAdminId = -1; // ID of the currently logged-in admin

    public interface OnAdminInteractionListener {
        void onDeleteAdmin(User admin);
    }    public AdminAdapter(Context context, List<User> adminsList, OnAdminInteractionListener listener, long currentAdminId) {
        this.context = context;
        this.adminsList = adminsList;
        this.listener = listener;
        this.currentAdminId = currentAdminId;
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin, parent, false);
        return new AdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        User admin = adminsList.get(position);
        
        // Set admin information
        holder.adminName.setText(admin.getFirstName() + " " + admin.getLastName());
        holder.adminEmail.setText(admin.getEmail());
        
        // Set additional details
        String details = "";
        if (admin.getPhone() != null && !admin.getPhone().isEmpty()) {
            details += "Phone: " + admin.getPhone();
        }
        if (admin.getCountry() != null && !admin.getCountry().isEmpty()) {
            if (!details.isEmpty()) details += " • ";
            details += admin.getCountry();
            if (admin.getCity() != null && !admin.getCity().isEmpty()) {
                details += ", " + admin.getCity();
            }
        }
        holder.adminDetails.setText(details.isEmpty() ? "No additional details" : details);
        
        // Set role badge
        holder.roleBadge.setText("ADMIN");
        
        // Set profile icon (default)
        holder.profileIcon.setImageResource(R.drawable.ic_admin);
          // Handle delete button - disable for default admin and current admin
        if (admin.getEmail().equals("admin@admin.com")) {
            holder.deleteButton.setEnabled(false);
            holder.deleteButton.setText("Default");
            holder.deleteButton.setAlpha(0.5f);
        } else if (admin.getUserId() == currentAdminId) {
            holder.deleteButton.setEnabled(false);
            holder.deleteButton.setText("You");
            holder.deleteButton.setAlpha(0.5f);
        } else {
            holder.deleteButton.setEnabled(true);
            holder.deleteButton.setText("Delete");
            holder.deleteButton.setAlpha(1.0f);
            holder.deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteAdmin(admin);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return adminsList.size();
    }

    /**
     * Updates the current admin ID in the adapter
     * @param currentAdminId The current admin's user ID
     */
    public void setCurrentAdminId(long currentAdminId) {
        this.currentAdminId = currentAdminId;
        notifyDataSetChanged(); // Refresh the UI to update button states
    }

    static class AdminViewHolder extends RecyclerView.ViewHolder {
        ImageView profileIcon;
        TextView adminName;
        TextView adminEmail;
        TextView adminDetails;
        TextView roleBadge;
        Button deleteButton;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            profileIcon = itemView.findViewById(R.id.admin_profile_icon);
            adminName = itemView.findViewById(R.id.admin_name);
            adminEmail = itemView.findViewById(R.id.admin_email);
            adminDetails = itemView.findViewById(R.id.admin_details);
            roleBadge = itemView.findViewById(R.id.admin_role_badge);
            deleteButton = itemView.findViewById(R.id.btn_delete_admin);
        }
    }
}
