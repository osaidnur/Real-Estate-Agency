package com.example.a1210733_1211088_courseproject.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.a1210733_1211088_courseproject.R;
import com.example.a1210733_1211088_courseproject.database.DataBaseHelper;
import com.example.a1210733_1211088_courseproject.models.User;
import com.example.a1210733_1211088_courseproject.utils.SharedPrefManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_READ_STORAGE = 2;

    private ImageView profilePic;
    private EditText firstNameEdit, lastNameEdit, phoneEdit;
    private EditText currentPasswordEdit, newPasswordEdit, confirmPasswordEdit;
    private Button saveProfileBtn, updatePasswordBtn, changePhotoBtn;

    private Uri profileImageUri;
    private DataBaseHelper dbHelper;
    private SharedPrefManager sharedPrefManager;
    private User currentUser;

    // Activity result launcher for permission requests
    private ActivityResultLauncher<String> requestPermissionLauncher;

    // Activity result launcher for image picker
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize permission result launcher
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        // Permission granted, open image picker
                        openImagePickerSafely();
                    } else {
                        // Permission denied
                        Toast.makeText(getContext(), "Permission denied. Cannot select profile image.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Initialize image picker result launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        profileImageUri = result.getData().getData();
                        profilePic.setImageURI(profileImageUri);

                        // Save the profile photo URI to SharedPreferences for immediate access
                        if (currentUser != null && profileImageUri != null) {
                            String uriString = profileImageUri.toString();
                            sharedPrefManager.writeString("profile_photo_" + currentUser.getUserId(), uriString);
                            Toast.makeText(getContext(), "Photo selected. Save profile to update permanently.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize database and shared preferences
        dbHelper = new DataBaseHelper(getContext(), "RealEstate", null, 1);
        sharedPrefManager = SharedPrefManager.getInstance(getContext());

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

        // Load user profile data from database
        loadUserProfile();

        // Set up button click listeners
        changePhotoBtn.setOnClickListener(v -> checkPermissionAndOpenImagePicker());
        saveProfileBtn.setOnClickListener(v -> saveProfileChanges());
        updatePasswordBtn.setOnClickListener(v -> updatePassword());
    }

    private void checkPermissionAndOpenImagePicker() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13+, check READ_MEDIA_IMAGES permission
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            } else {
                openImagePickerSafely();
            }
        } else {
            // For Android 12 and below, check READ_EXTERNAL_STORAGE permission
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                openImagePickerSafely();
            }
        }
    }

    private void openImagePickerSafely() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
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

            // Save the profile photo URI to SharedPreferences for immediate access
            if (currentUser != null) {
                String uriString = profileImageUri.toString();
                sharedPrefManager.writeString("profile_photo_" + currentUser.getUserId(), uriString);
                Toast.makeText(getContext(), "Photo selected. Save profile to update permanently.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadUserProfile() {
        long currentUserId = sharedPrefManager.getCurrentUserId();

        if (currentUserId == -1) {
            Toast.makeText(getContext(), "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Load user data from database
        currentUser = dbHelper.getUserById(currentUserId);

        if (currentUser != null) {
            firstNameEdit.setText(currentUser.getFirstName());
            lastNameEdit.setText(currentUser.getLastName());
            phoneEdit.setText(currentUser.getPhone());

            // Load profile photo
            loadProfilePhoto();
        } else {
            Toast.makeText(getContext(), "Failed to load user profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProfilePhoto() {
        if (currentUser == null) return;

        // First try to load from database
        String photoPath = currentUser.getProfilePhoto();
        if (photoPath != null && !photoPath.isEmpty()) {
            try {
                Uri photoUri = Uri.parse(photoPath);
                profilePic.setImageURI(photoUri);
                profileImageUri = photoUri;
                return;
            } catch (Exception e) {
                // Photo URI might be invalid, try SharedPreferences
            }
        }

        // Try to load from SharedPreferences (temporary storage)
        String tempPhotoPath = sharedPrefManager.readString("profile_photo_" + currentUser.getUserId(), "");
        if (!tempPhotoPath.isEmpty()) {
            try {
                Uri photoUri = Uri.parse(tempPhotoPath);
                profilePic.setImageURI(photoUri);
                profileImageUri = photoUri;
            } catch (Exception e) {
                // Keep default image
            }
        }
    }

    private void saveProfileChanges() {
        if (currentUser == null) {
            Toast.makeText(getContext(), "No user data available", Toast.LENGTH_SHORT).show();
            return;
        }

        String firstName = firstNameEdit.getText().toString().trim();
        String lastName = lastNameEdit.getText().toString().trim();
        String phone = phoneEdit.getText().toString().trim();

        if (validateProfileInput(firstName, lastName, phone)) {
            // Prepare profile photo path
            String profilePhotoPath = "";
            if (profileImageUri != null) {
                profilePhotoPath = profileImageUri.toString();
            } else if (currentUser.getProfilePhoto() != null) {
                profilePhotoPath = currentUser.getProfilePhoto();
            }

            // Update in database
            boolean success = dbHelper.updateUserProfile(
                    currentUser.getUserId(),
                    firstName,
                    lastName,
                    phone,
                    profilePhotoPath
            );

            if (success) {
                // Update current user object
                currentUser.setFirstName(firstName);
                currentUser.setLastName(lastName);
                currentUser.setPhone(phone);
                currentUser.setProfilePhoto(profilePhotoPath);

                // Clear temporary photo storage
                sharedPrefManager.writeString("profile_photo_" + currentUser.getUserId(), "");

                Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to update profile. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validateProfileInput(String firstName, String lastName, String phone) {
        if (TextUtils.isEmpty(firstName)) {
            firstNameEdit.setError("First name is required");
            return false;
        }

        if (TextUtils.isEmpty(firstName) || firstName.length() < 3) {
            firstNameEdit.setError("First name must be at least 2 characters");
            return false;
        }

        if (TextUtils.isEmpty(lastName) || lastName.length() < 3) {
            lastNameEdit.setError("Last name must be at least 2 characters");
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


        if (!phone.isEmpty() && !phone.matches("^\\+\\d{1,3}\\s?\\d{6,15}$")) {
            phoneEdit.setError("Please enter a valid phone number");
            return false;
        }
        // Simple phone validation - could be made more sophisticated
//        if (!phone.matches("^\\+?[0-9]{10,15}$")) {
//            phoneEdit.setError("Please enter a valid phone number");
//            return false;
//        }

        return true;
    }

    private void updatePassword() {
        if (currentUser == null) {
            Toast.makeText(getContext(), "No user data available", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentPassword = currentPasswordEdit.getText().toString();
        String newPassword = newPasswordEdit.getText().toString();
        String confirmPassword = confirmPasswordEdit.getText().toString();

        if (validatePasswordChange(currentPassword, newPassword, confirmPassword)) {
            // Update password in database
            boolean success = dbHelper.updateUserPassword(
                    currentUser.getUserId(),
                    currentPassword,
                    newPassword
            );

            if (success) {
                Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                currentPasswordEdit.setText("");
                newPasswordEdit.setText("");
                confirmPasswordEdit.setText("");
            } else {
                Toast.makeText(getContext(), "Failed to update password. Please check your current password and try again.", Toast.LENGTH_LONG).show();
            }
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

        if (newPass.length() < 6) {
            newPasswordEdit.setError("Password must be at least 6 characters");
            return false;
        }

        if (!newPass.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&^()_+=\\-{}\\[\\]:;\"'<>,./~`|\\\\]).+$")) {
            newPasswordEdit.setError("Password must include at least one letter, one number, and one special character");
            return false;
        }

        return true;
    }
}
