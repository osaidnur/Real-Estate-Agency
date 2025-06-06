package com.example.a1210733_1211088_courseproject.api;

import com.example.a1210733_1211088_courseproject.models.Property;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PropertyJsonParser {

    /**
     * Parse the JSON response from the property API
     *
     * @param jsonResponse the JSON response string
     * @return a list of Property objects
     */
    public static List<Property> parsePropertyJson(String jsonResponse) {
        List<Property> properties = new ArrayList<>();

        try {
            // Parse the JSON response
            JSONObject jsonObject = new JSONObject(jsonResponse);

            // Get the properties array (ignoring categories as requested)
            JSONArray propertiesArray = jsonObject.getJSONArray("properties");

            // Iterate through the properties array
            for (int i = 0; i < propertiesArray.length(); i++) {
                JSONObject propertyObject = propertiesArray.getJSONObject(i);

                // Extract property details
                long id = propertyObject.optLong("id", 0);
                String type = propertyObject.optString("type", "");
                String title = propertyObject.optString("title", "");
                String description = propertyObject.optString("description", "");
                int bedrooms = propertyObject.optInt("bedrooms", 0);
                int bathrooms = propertyObject.optInt("bathrooms", 0);

                // Parse area from string (e.g. "120 m²") to float
                String areaString = propertyObject.optString("area", "0 m²");
                float area = 0;
                try {
                    // Extract numeric part from area string
                    area = Float.parseFloat(areaString.replaceAll("[^\\d.]", ""));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                double price = propertyObject.optDouble("price", 0.0);

                // Parse location into country and city
                String location = propertyObject.optString("location", "");
                String city = "";
                String country = "";

                if (location.contains(",")) {
                    String[] parts = location.split(",");
                    city = parts[0].trim();
                    country = parts.length > 1 ? parts[1].trim() : "";
                } else {
                    city = location;
                }

                String imageUrl = propertyObject.optString("image_url", "");

                // Default values for special and discount as they're not in the JSON
                boolean isSpecial = false;
                double discount = 0.0;

                // Create a new Property object
                Property property = new Property(
                        id, type, title, description, bedrooms, bathrooms,
                        area, price, country, city, imageUrl, isSpecial, discount
                );

                // Add to the list
                properties.add(property);
            }
        } catch (JSONException e) {
            e.printStackTrace();

            return null; // Return null if parsing fails
        }
        return properties; // Return the list of properties
    }
}