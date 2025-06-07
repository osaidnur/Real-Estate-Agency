package com.example.a1210733_1211088_courseproject.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a1210733_1211088_courseproject.R;
import com.example.a1210733_1211088_courseproject.api.PropertyApiClient;
import com.example.a1210733_1211088_courseproject.database.DataBaseHelper;
import com.example.a1210733_1211088_courseproject.database.sql.PropertyQueries;
import com.example.a1210733_1211088_courseproject.database.sql.UserQueries;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Button btnConnect;
    private PropertyApiClient propertyApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        View rootView = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize PropertyApiClient
        propertyApiClient = new PropertyApiClient(this);

        // Set up connect button click listener
        btnConnect = findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show loading message
                Toast.makeText(MainActivity.this, "Connecting to API...", Toast.LENGTH_SHORT).show();

                // Disable button to prevent multiple clicks
                btnConnect.setEnabled(false);

                // Fetch properties from API
                propertyApiClient.fetchAndStoreProperties();

                // Re-enable button after a delay (to prevent rapid clicking)
                btnConnect.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnConnect.setEnabled(true);
                        // Get and print all properties
                        getAllPropertiesAndPrint();
                        // Optionally, get and print all users
                        getUsersAndPrint();

                        if (propertyApiClient.isImportSuccessful()) {
                            // Navigate to AuthActivity only if successful
                            Intent intent = new Intent(MainActivity.this, authActivity.class);
                            startActivity(intent);
                            finish(); // Optional: close current activity
                        }
                    }
                }, 2000); // 2 seconds delay
            }
        });

        // Get and print all properties when the activity starts
        //getAllPropertiesAndPrint();
    }

    /**
     * Fetches all properties from the database and prints them to the terminal
     */
    private void getAllPropertiesAndPrint() {
        DataBaseHelper dbHelper = new DataBaseHelper(this, "RealEstate", null, 1);

        try {
            // Use the getAllProperties method from DataBaseHelper
            Cursor cursor = dbHelper.getAllProperties();

            System.out.println("==================================================");
            System.out.println("               ALL PROPERTIES IN DATABASE         ");
            System.out.println("==================================================");

            if (cursor == null || cursor.getCount() == 0) {
                System.out.println("No properties found in the database.");
            } else {
                System.out.println("Total properties found: " + cursor.getCount());

                // Get column indices once before the loop
                int idIndex = cursor.getColumnIndex(PropertyQueries.COLUMN_PROPERTY_ID);
                int typeIndex = cursor.getColumnIndex(PropertyQueries.COLUMN_TYPE);
                int titleIndex = cursor.getColumnIndex(PropertyQueries.COLUMN_TITLE);
                int descriptionIndex = cursor.getColumnIndex(PropertyQueries.COLUMN_DESCRIPTION);
                int bedroomsIndex = cursor.getColumnIndex(PropertyQueries.COLUMN_BEDROOMS);
                int bathroomsIndex = cursor.getColumnIndex(PropertyQueries.COLUMN_BATHROOMS);
                int areaIndex = cursor.getColumnIndex(PropertyQueries.COLUMN_AREA);
                int priceIndex = cursor.getColumnIndex(PropertyQueries.COLUMN_PRICE);
                int countryIndex = cursor.getColumnIndex(PropertyQueries.COLUMN_COUNTRY);
                int cityIndex = cursor.getColumnIndex(PropertyQueries.COLUMN_CITY);
                int isSpecialIndex = cursor.getColumnIndex(PropertyQueries.COLUMN_IS_SPECIAL);
                int discountIndex = cursor.getColumnIndex(PropertyQueries.COLUMN_DISCOUNT);

                // Iterate through cursor and print properties directly
                while (cursor.moveToNext()) {
                    System.out.println("--------------------------------------------------");
                    System.out.println("ID: " + (idIndex >= 0 ? cursor.getInt(idIndex) : "N/A"));
                    System.out.println("Title: " + (titleIndex >= 0 ? cursor.getString(titleIndex) : "N/A"));
                    System.out.println("Type: " + (typeIndex >= 0 ? cursor.getString(typeIndex) : "N/A"));
                    System.out.println("Price: $" + (priceIndex >= 0 ? cursor.getDouble(priceIndex) : "N/A"));

                    String city = cityIndex >= 0 ? cursor.getString(cityIndex) : "";
                    String country = countryIndex >= 0 ? cursor.getString(countryIndex) : "";
                    System.out.println("Location: " + city + ", " + country);

                    System.out.println("Area: " + (areaIndex >= 0 ? cursor.getDouble(areaIndex) : "N/A") + " sqft");
                    System.out.println("Bedrooms: " + (bedroomsIndex >= 0 ? cursor.getInt(bedroomsIndex) : "N/A"));
                    System.out.println("Bathrooms: " + (bathroomsIndex >= 0 ? cursor.getInt(bathroomsIndex) : "N/A"));

                    boolean isSpecial = isSpecialIndex >= 0 && cursor.getInt(isSpecialIndex) == 1;
                    System.out.println("Special: " + (isSpecial ? "Yes" : "No"));
                    if (isSpecial && discountIndex >= 0) {
                        System.out.println("Discount: " + cursor.getDouble(discountIndex) + "%");
                    }
                    System.out.println("--------------------------------------------------");
                }
            }
            System.out.println("==================================================");

            // Close the cursor when done
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            System.out.println("Error retrieving properties: " + e.getMessage());
            Log.e(TAG, "Error retrieving properties", e);
        } finally {
            dbHelper.close();
        }
    }


    private void getUsersAndPrint() {
        DataBaseHelper dbHelper = new DataBaseHelper(this, "RealEstate", null, 1);

        try {
            Cursor cursor = dbHelper.getAllUsers();

            System.out.println("==================================================");
            System.out.println("               ALL USERS IN DATABASE              ");
            System.out.println("==================================================");

            if (cursor == null || cursor.getCount() == 0) {
                System.out.println("No users found in the database.");
            } else {
                System.out.println("Total users found: " + cursor.getCount());

                while (cursor.moveToNext()) {
                    @SuppressLint("Range") long userId = cursor.getLong(cursor.getColumnIndex(UserQueries.COLUMN_USER_ID));
                    @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(UserQueries.COLUMN_EMAIL));
                    @SuppressLint("Range") String firstName = cursor.getString(cursor.getColumnIndex(UserQueries.COLUMN_FIRST_NAME));
                    @SuppressLint("Range") String lastName = cursor.getString(cursor.getColumnIndex(UserQueries.COLUMN_LAST_NAME));
                    @SuppressLint("Range") String role = cursor.getString(cursor.getColumnIndex(UserQueries.COLUMN_ROLE));

                    System.out.println("--------------------------------------------------");
                    System.out.println("User ID: " + userId);
                    System.out.println("Email: " + email);
                    System.out.println("Name: " + firstName + " " + lastName);
                    System.out.println("Role: " + role);
                    System.out.println("--------------------------------------------------");
                }
            }
            System.out.println("==================================================");

            // Close the cursor when done
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            System.out.println("Error retrieving users: " + e.getMessage());
            Log.e(TAG, "Error retrieving users", e);
        } finally {
            dbHelper.close();
        }
    }

}









