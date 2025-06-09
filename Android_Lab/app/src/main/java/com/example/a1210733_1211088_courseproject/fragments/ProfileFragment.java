package com.example.a1210733_1211088_courseproject.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.a1210733_1211088_courseproject.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView profilePic;
    private EditText firstNameEdit, lastNameEdit, phoneEdit;
    private EditText currentPasswordEdit, newPasswordEdit, confirmPasswordEdit;
    private Button saveProfileBtn, updatePasswordBtn, changePhotoBtn;

    private Uri profileImageUri;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        profilePic = view.findViewById(R.id.profile_image);
        firstNameEdit = view.findViewById(R.id.edit_first_name);
        lastNameEdit = view.findViewById(R.id.edit_last_name);
        phoneEdit = view.findViewById(R.id.edit_phone);
        currentPasswordEdit = view.findViewById(R.id.edit_current_password);
        newPasswordEdit = view.findViewById(R.id.edit_new_password);
        confirmPasswordEdit = view.findViewById(R.id.edit_confirm_password);
        saveProfileBtn = view.findViewById(R.id.btn_save_profile);
        updatePasswordBtn = view.findViewById(R.id.btn_update_password);
        changePhotoBtn = view.findViewById(R.id.btn_change_photo);

        // In a real app, these values would be retrieved from a database
        loadUserProfile();

        // Set up button click listeners
        changePhotoBtn.setOnClickListener(v -> openImageChooser());
        saveProfileBtn.setOnClickListener(v -> saveProfileChanges());
        updatePasswordBtn.setOnClickListener(v -> updatePassword());
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            profileImageUri = data.getData();
            profilePic.setImageURI(profileImageUri);
            // In a real app, you would save this URI to a database or shared preferences
        }
    }

    private void loadUserProfile() {
        // In a real app, load from database. For demo, we use sample data
        firstNameEdit.setText("John");
        lastNameEdit.setText("Doe");
        phoneEdit.setText("+1234567890");
    }

    private void saveProfileChanges() {
        String firstName = firstNameEdit.getText().toString().trim();
        String lastName = lastNameEdit.getText().toString().trim();
        String phone = phoneEdit.getText().toString().trim();

        if (validateProfileInput(firstName, lastName, phone)) {
            // In a real app, save to database
            Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateProfileInput(String firstName, String lastName, String phone) {
        if (TextUtils.isEmpty(firstName)) {
            firstNameEdit.setError("First name is required");
            return false;
        }

        if (TextUtils.isEmpty(lastName)) {
            lastNameEdit.setError("Last name is required");
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            phoneEdit.setError("Phone number is required");
            return false;
        }

        // Simple phone validation - could be made more sophisticated
        if (!phone.matches("^\\+?[0-9]{10,15}$")) {
            phoneEdit.setError("Please enter a valid phone number");
            return false;
        }

        return true;
    }

    private void updatePassword() {
        String currentPassword = currentPasswordEdit.getText().toString();
        String newPassword = newPasswordEdit.getText().toString();
        String confirmPassword = confirmPasswordEdit.getText().toString();

        if (validatePasswordChange(currentPassword, newPassword, confirmPassword)) {
            // In a real app, verify current password and update with new password in database
            Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
            currentPasswordEdit.setText("");
            newPasswordEdit.setText("");
            confirmPasswordEdit.setText("");
        }
    }

    private boolean validatePasswordChange(String current, String newPass, String confirm) {
        if (TextUtils.isEmpty(current)) {
            currentPasswordEdit.setError("Current password is required");
            return false;
        }

        if (TextUtils.isEmpty(newPass)) {
            newPasswordEdit.setError("New password is required");
            return false;
        }

        if (TextUtils.isEmpty(confirm)) {
            confirmPasswordEdit.setError("Please confirm your new password");
            return false;
        }

        if (!newPass.equals(confirm)) {
            confirmPasswordEdit.setError("Passwords do not match");
            return false;
        }

        // Password security validation (min 8 chars, 1 uppercase, 1 number, 1 special char)
        Pattern pattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
        Matcher matcher = pattern.matcher(newPass);
        if (!matcher.matches()) {
            newPasswordEdit.setError("Password must have at least 8 characters, including uppercase, number, and special character");
            return false;
        }

        return true;
    }
}
