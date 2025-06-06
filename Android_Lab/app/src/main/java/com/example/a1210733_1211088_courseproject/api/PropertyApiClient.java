package com.example.a1210733_1211088_courseproject.api;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.a1210733_1211088_courseproject.database.DataBaseHelper;
import com.example.a1210733_1211088_courseproject.database.sql.PropertyQueries;
import com.example.a1210733_1211088_courseproject.models.Property;

import java.util.List;

public class PropertyApiClient {
    private static final String TAG = "PropertyAPI";
    private static final String API_URL = "https://mocki.io/v1/4d533823-8133-4631-8112-00e69a143881";
    private Context context;
    private DataBaseHelper dbHelper;

    public PropertyApiClient(Context context) {
        this.context = context;
        this.dbHelper = new DataBaseHelper(context, "RealEstate", null, 1);
    }

    /**
     * Fetch properties from the API and store them in the database
     */
    public void fetchAndStoreProperties() {
        // Create a new ConnectionAsyncTask to fetch the data
        PropertyConnectionTask task = new PropertyConnectionTask();
        task.setOnTaskCompleted(new PropertyConnectionTask.OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String result) {
                if (result != null) {
                    // Parse the JSON result
                    Log.d(TAG, "API Response received: " + result.substring(0, Math.min(100, result.length())) + "...");

                    List<Property> properties = PropertyJsonParser.parsePropertyJson(result);

                    // Print all properties to Logcat
                    Log.d(TAG, "===== PROPERTIES FROM API =====");
                    for (Property property : properties) {
                        Log.d(TAG, property.getPropertyId() + ": " + property.getTitle() +
                              " | Type: " + property.getType() +
                              " | Price: $" + property.getPrice() +
                              " | Location: " + property.getCity() + ", " + property.getCountry());
                    }
                    Log.d(TAG, "===== TOTAL: " + properties.size() + " PROPERTIES =====");

                    // Store the properties in the database
                    storePropertiesInDatabase(properties);

                    // Notify the user
                    Toast.makeText(context, properties.size() + " properties imported successfully!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Handle error with more detailed logging
                    Log.e(TAG, "Failed to fetch data from API - result was null");
                    Toast.makeText(context, "Failed to import properties from API",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Execute the task with the API URL
        Log.d(TAG, "Starting API request to: " + API_URL);
        task.execute(API_URL);
    }

    /**
     * Store a list of properties in the database
     * @param properties the list of properties to store
     */
    private void storePropertiesInDatabase(List<Property> properties) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            // Begin transaction for better performance
            db.beginTransaction();

            for (Property property : properties) {
                ContentValues values = new ContentValues();
                // Don't set ID as it's auto-increment in the database
                // If you want to preserve IDs from API, uncomment the line below
                // values.put(PropertyQueries.COLUMN_PROPERTY_ID, property.getPropertyId());
                values.put(PropertyQueries.COLUMN_TYPE, property.getType());
                values.put(PropertyQueries.COLUMN_TITLE, property.getTitle());
                values.put(PropertyQueries.COLUMN_DESCRIPTION, property.getDescription());
                values.put(PropertyQueries.COLUMN_BEDROOMS, property.getBedrooms());
                values.put(PropertyQueries.COLUMN_BATHROOMS, property.getBathrooms());
                values.put(PropertyQueries.COLUMN_AREA, property.getArea());
                values.put(PropertyQueries.COLUMN_PRICE, property.getPrice());
                values.put(PropertyQueries.COLUMN_COUNTRY, property.getCountry());
                values.put(PropertyQueries.COLUMN_CITY, property.getCity());
                values.put(PropertyQueries.COLUMN_IMAGE_URL, property.getImageUrl());
                values.put(PropertyQueries.COLUMN_IS_SPECIAL, property.isSpecial() ? 1 : 0);
                values.put(PropertyQueries.COLUMN_DISCOUNT, property.getDiscount());

                // Insert the property into the database
                db.insert(PropertyQueries.TABLE_NAME, null, values);
            }

            // Mark transaction as successful
            db.setTransactionSuccessful();
        } finally {
            // End the transaction
            db.endTransaction();
        }
    }
}
