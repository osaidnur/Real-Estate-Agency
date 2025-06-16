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
    private EditText firstNameEdit, lastNameEdit, phoneEdit;    private EditText currentPasswordEdit, newPasswordEdit, confirmPasswordEdit;
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
        );        // Initialize image picker result launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        
                        if (selectedImageUri != null) {
                            android.util.Log.d("ProfileFragment", "Image selected: " + selectedImageUri.toString());
                            
                            // Copy image to internal storage for permanent access
                            String internalPath = copyImageToInternalStorage(selectedImageUri);
                            
                            if (internalPath != null) {
                                // Use the internal file path
                                profileImageUri = Uri.fromFile(new java.io.File(internalPath));
                                setImageSafely(profileImageUri);
                                
                                // Store the internal path temporarily
                                if (currentUser != null) {
                                    sharedPrefManager.writeString("profile_photo_" + currentUser.getUserId(), internalPath);
                                    Toast.makeText(getContext(), "Photo selected. Save profile to update permanently.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Fallback: try to use the original URI with persistent permission
                                try {
                                    requireContext().getContentResolver().takePersistableUriPermission(
                                        selectedImageUri, 
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    );
                                    profileImageUri = selectedImageUri;
                                    setImageSafely(profileImageUri);
                                    
                                    // Save the URI temporarily
                                    if (currentUser != null) {
                                        sharedPrefManager.writeString("profile_photo_" + currentUser.getUserId(), selectedImageUri.toString());
                                        Toast.makeText(getContext(), "Photo selected. Save profile to update permanently.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                } catch (SecurityException e) {
                                    android.util.Log.w("ProfileFragment", "Could not get persistent permission or copy image", e);
                                    Toast.makeText(getContext(), "Error saving photo. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                }
        );
    }    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
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
            confirmPasswordEdit = view.findViewById(R.id.edit_confirm_password);            saveProfileBtn = view.findViewById(R.id.btn_save_profile);
            updatePasswordBtn = view.findViewById(R.id.btn_update_password);
            changePhotoBtn = view.findViewById(R.id.btn_change_photo);// Check if all required views are found
            if (profilePic == null || firstNameEdit == null || lastNameEdit == null || 
                phoneEdit == null || saveProfileBtn == null || updatePasswordBtn == null || 
                changePhotoBtn == null) {
                Toast.makeText(getContext(), "Error loading profile interface", Toast.LENGTH_SHORT).show();
                return;
            }

            // Load user profile data from database
            loadUserProfile();            // Set up button click listeners
            changePhotoBtn.setOnClickListener(v -> checkPermissionAndOpenImagePicker());
            saveProfileBtn.setOnClickListener(v -> saveProfileChanges());
            updatePasswordBtn.setOnClickListener(v -> updatePassword());
            
        } catch (Exception e) {
            android.util.Log.e("ProfileFragment", "Error in onViewCreated", e);
            if (getContext() != null) {
                Toast.makeText(getContext(), "Error loading profile. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
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
    }    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            profileImageUri = data.getData();
            
            // Take persistent URI permission
            if (profileImageUri != null) {
                try {
                    requireContext().getContentResolver().takePersistableUriPermission(
                        profileImageUri, 
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );
                } catch (SecurityException e) {
                    // Fallback: handle the case where we can't get persistent permission
                    android.util.Log.w("ProfileFragment", "Could not take persistent URI permission", e);
                }
            }
            
            setImageSafely(profileImageUri);

            // Save the profile photo URI to SharedPreferences for immediate access
            if (currentUser != null) {
                String uriString = profileImageUri.toString();
                sharedPrefManager.writeString("profile_photo_" + currentUser.getUserId(), uriString);
                Toast.makeText(getContext(), "Photo selected. Save profile to update permanently.", Toast.LENGTH_SHORT).show();
            }
        }
    }    private void loadUserProfile() {
        android.util.Log.d("ProfileFragment", "Loading user profile");
        
        try {
            long currentUserId = sharedPrefManager.getCurrentUserId();
            android.util.Log.d("ProfileFragment", "Current user ID: " + currentUserId);

            if (currentUserId == -1) {
                Toast.makeText(getContext(), "No user logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            // Load user data from database
            currentUser = dbHelper.getUserById(currentUserId);
            android.util.Log.d("ProfileFragment", "Loaded user: " + (currentUser != null ? currentUser.getEmail() : "null"));

            if (currentUser != null) {
                // Safely set text fields
                if (firstNameEdit != null) firstNameEdit.setText(currentUser.getFirstName());
                if (lastNameEdit != null) lastNameEdit.setText(currentUser.getLastName());
                if (phoneEdit != null) phoneEdit.setText(currentUser.getPhone());

                // Load profile photo safely
                loadProfilePhoto();
                
                android.util.Log.d("ProfileFragment", "User profile loaded successfully");
            } else {
                Toast.makeText(getContext(), "Failed to load user profile", Toast.LENGTH_SHORT).show();
                android.util.Log.e("ProfileFragment", "User not found in database");
            }
        } catch (Exception e) {
            android.util.Log.e("ProfileFragment", "Error loading user profile", e);
            if (getContext() != null) {
                Toast.makeText(getContext(), "Error loading profile data", Toast.LENGTH_SHORT).show();
            }
            // Set default image on error
            if (profilePic != null) {
                profilePic.setImageResource(R.drawable.ic_person);
            }
        }
    }private void loadProfilePhoto() {
        if (currentUser == null) return;

        android.util.Log.d("ProfileFragment", "Loading profile photo for user: " + currentUser.getUserId());

        // First try to load from database
        String photoPath = currentUser.getProfilePhoto();
        android.util.Log.d("ProfileFragment", "Database photo path: " + photoPath);
          if (photoPath != null && !photoPath.isEmpty()) {
            try {
                android.util.Log.d("ProfileFragment", "Trying to parse photo path: " + photoPath);
                
                // Check if it's a file path (starts with /) or URI (starts with content:// or file://)
                if (photoPath.startsWith("/")) {
                    // Handle absolute file path
                    java.io.File photoFile = new java.io.File(photoPath);
                    if (photoFile.exists() && photoFile.canRead()) {
                        Uri fileUri = Uri.fromFile(photoFile);
                        setImageSafely(fileUri);
                        profileImageUri = fileUri;
                        android.util.Log.d("ProfileFragment", "Successfully loaded photo from file path");
                        return;
                    } else {
                        android.util.Log.w("ProfileFragment", "File doesn't exist or can't be read: " + photoPath);
                        clearInvalidPhotoFromDatabase();
                    }
                } else {
                    // Handle URI (content:// or file://)
                    Uri photoUri = Uri.parse(photoPath);
                    android.util.Log.d("ProfileFragment", "Parsed URI: " + photoUri.toString());
                    
                    // Test if URI is accessible before setting it
                    if (isUriAccessible(photoUri)) {
                        setImageSafely(photoUri);
                        profileImageUri = photoUri;
                        android.util.Log.d("ProfileFragment", "Successfully loaded photo from database URI");
                        return;
                    } else {
                        android.util.Log.w("ProfileFragment", "Database URI not accessible, clearing from database");
                        clearInvalidPhotoFromDatabase();
                    }
                }
            } catch (Exception e) {
                android.util.Log.e("ProfileFragment", "Error loading photo from database: " + photoPath, e);
                clearInvalidPhotoFromDatabase();
            }
        }

        // Try to load from SharedPreferences (temporary storage)
        String tempPhotoPath = sharedPrefManager.readString("profile_photo_" + currentUser.getUserId(), "");
        android.util.Log.d("ProfileFragment", "Temp photo path: " + tempPhotoPath);
          if (!tempPhotoPath.isEmpty()) {
            try {
                android.util.Log.d("ProfileFragment", "Trying to load temp photo: " + tempPhotoPath);
                
                if (tempPhotoPath.startsWith("/")) {
                    // Handle file path
                    java.io.File photoFile = new java.io.File(tempPhotoPath);
                    if (photoFile.exists() && photoFile.canRead()) {
                        Uri fileUri = Uri.fromFile(photoFile);
                        setImageSafely(fileUri);
                        profileImageUri = fileUri;
                        android.util.Log.d("ProfileFragment", "Successfully loaded photo from temp file path");
                        return;
                    } else {
                        android.util.Log.w("ProfileFragment", "Temp file doesn't exist, clearing");
                        sharedPrefManager.writeString("profile_photo_" + currentUser.getUserId(), "");
                    }
                } else {
                    // Handle URI
                    Uri photoUri = Uri.parse(tempPhotoPath);
                    if (isUriAccessible(photoUri)) {
                        setImageSafely(photoUri);
                        profileImageUri = photoUri;
                        android.util.Log.d("ProfileFragment", "Successfully loaded photo from temp URI");
                        return;
                    } else {
                        android.util.Log.w("ProfileFragment", "Temp URI not accessible, clearing");
                        sharedPrefManager.writeString("profile_photo_" + currentUser.getUserId(), "");
                    }
                }
            } catch (Exception e) {
                android.util.Log.e("ProfileFragment", "Error loading photo from temp: " + tempPhotoPath, e);
                sharedPrefManager.writeString("profile_photo_" + currentUser.getUserId(), "");
            }
        }
        
        // Set default image if no valid photo found
        android.util.Log.d("ProfileFragment", "Using default profile image");
        profilePic.setImageResource(R.drawable.ic_person);
        profileImageUri = null;
    }    private boolean isUriAccessible(Uri uri) {
        if (uri == null) return false;
        
        try {
            String scheme = uri.getScheme();
            android.util.Log.d("ProfileFragment", "Checking URI accessibility: " + uri.toString() + " (scheme: " + scheme + ")");
            
            if ("file".equals(scheme)) {
                // For file:// URIs, check if file exists
                String path = uri.getPath();
                if (path != null) {
                    java.io.File file = new java.io.File(path);
                    boolean accessible = file.exists() && file.canRead();
                    android.util.Log.d("ProfileFragment", "File URI accessible: " + accessible);
                    return accessible;
                }
            } else if ("content".equals(scheme)) {
                // For content:// URIs, try to open input stream
                java.io.InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
                if (inputStream != null) {
                    inputStream.close();
                    android.util.Log.d("ProfileFragment", "Content URI accessible: true");
                    return true;
                }
            }
        } catch (Exception e) {
            android.util.Log.w("ProfileFragment", "URI not accessible: " + uri.toString(), e);
        }
        return false;
    }

    private void saveProfileChanges() {
        if (currentUser == null) {
            Toast.makeText(getContext(), "No user data available", Toast.LENGTH_SHORT).show();
            return;
        }

        String firstName = firstNameEdit.getText().toString().trim();
        String lastName = lastNameEdit.getText().toString().trim();
        String phone = phoneEdit.getText().toString().trim();        if (validateProfileInput(firstName, lastName, phone)) {
            // Prepare profile photo path
            String profilePhotoPath = "";
            
            // Check if we have a new photo to save
            String tempPhotoPath = sharedPrefManager.readString("profile_photo_" + currentUser.getUserId(), "");
            if (!tempPhotoPath.isEmpty()) {
                // Use the temporary photo path (could be file path or URI)
                profilePhotoPath = tempPhotoPath;
                android.util.Log.d("ProfileFragment", "Using temp photo path: " + profilePhotoPath);
            } else if (profileImageUri != null) {
                // Use current URI
                profilePhotoPath = profileImageUri.toString();
                android.util.Log.d("ProfileFragment", "Using current URI: " + profilePhotoPath);
            } else if (currentUser.getProfilePhoto() != null) {
                // Keep existing photo
                profilePhotoPath = currentUser.getProfilePhoto();
                android.util.Log.d("ProfileFragment", "Keeping existing photo: " + profilePhotoPath);
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

    private void setImageSafely(Uri uri) {
        if (profilePic == null || uri == null) {
            android.util.Log.w("ProfileFragment", "Cannot set image - profilePic or uri is null");
            return;
        }
        
        try {
            // Use post to ensure we're on the UI thread
            profilePic.post(() -> {
                try {
                    profilePic.setImageURI(uri);
                    android.util.Log.d("ProfileFragment", "Image set successfully");
                } catch (Exception e) {
                    android.util.Log.e("ProfileFragment", "Error setting image URI", e);
                    profilePic.setImageResource(R.drawable.ic_person);
                }
            });
        } catch (Exception e) {
            android.util.Log.e("ProfileFragment", "Error in setImageSafely", e);
            profilePic.setImageResource(R.drawable.ic_person);
        }
    }

    // Method to clear problematic photo URI - for debugging
    public void clearProfilePhoto() {
        if (currentUser != null) {
            try {
                // Clear from database
                dbHelper.updateUserProfile(
                    currentUser.getUserId(),
                    currentUser.getFirstName(),
                    currentUser.getLastName(),
                    currentUser.getPhone(),
                    "" // Clear photo path
                );
                
                // Clear from shared preferences
                sharedPrefManager.writeString("profile_photo_" + currentUser.getUserId(), "");
                
                // Set default image
                if (profilePic != null) {
                    profilePic.setImageResource(R.drawable.ic_person);
                }
                
                profileImageUri = null;
                android.util.Log.d("ProfileFragment", "Profile photo cleared successfully");
                
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Profile photo cleared", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                android.util.Log.e("ProfileFragment", "Error clearing profile photo", e);
            }
        }
    }

    private void clearInvalidPhotoFromDatabase() {
        if (currentUser != null) {
            try {
                dbHelper.updateUserProfile(
                    currentUser.getUserId(),
                    currentUser.getFirstName(),
                    currentUser.getLastName(),
                    currentUser.getPhone(),
                    "" // Clear photo path
                );
                android.util.Log.d("ProfileFragment", "Cleared invalid photo from database");
            } catch (Exception e) {
                android.util.Log.e("ProfileFragment", "Error clearing invalid photo from database", e);
            }
        }
    }

    private String copyImageToInternalStorage(Uri imageUri) {
        try {
            // Create a unique filename
            String filename = "profile_" + currentUser.getUserId() + "_" + System.currentTimeMillis() + ".jpg";
            
            // Get the app's internal storage directory
            java.io.File internalDir = new java.io.File(requireContext().getFilesDir(), "profile_photos");
            if (!internalDir.exists()) {
                internalDir.mkdirs();
            }
            
            java.io.File destinationFile = new java.io.File(internalDir, filename);
            
            // Copy the image
            try (java.io.InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
                 java.io.FileOutputStream outputStream = new java.io.FileOutputStream(destinationFile)) {
                
                if (inputStream == null) return null;
                
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            
            android.util.Log.d("ProfileFragment", "Image copied to: " + destinationFile.getAbsolutePath());
            return destinationFile.getAbsolutePath();
            
        } catch (Exception e) {
            android.util.Log.e("ProfileFragment", "Error copying image to internal storage", e);
            return null;
        }
    }
}
