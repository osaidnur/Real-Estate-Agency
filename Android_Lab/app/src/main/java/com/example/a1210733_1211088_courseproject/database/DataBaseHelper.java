package com.example.a1210733_1211088_courseproject.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.a1210733_1211088_courseproject.database.sql.FavoriteQueries;
import com.example.a1210733_1211088_courseproject.database.sql.PropertyQueries;
import com.example.a1210733_1211088_courseproject.database.sql.ReservationQueries;
import com.example.a1210733_1211088_courseproject.database.sql.UserQueries;
import com.example.a1210733_1211088_courseproject.models.User;
import com.example.a1210733_1211088_courseproject.models.Property;
import com.example.a1210733_1211088_courseproject.models.Reservation;
import com.example.a1210733_1211088_courseproject.utils.PasswordUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables using SQL queries from dedicated query files
        db.execSQL(UserQueries.CREATE_TABLE);
        db.execSQL(PropertyQueries.CREATE_TABLE);
        db.execSQL(FavoriteQueries.CREATE_TABLE);
        db.execSQL(ReservationQueries.CREATE_TABLE);

        // Insert the default admin account with the provided db instance
        insertDefaultAdmin(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // For more drastic changes, drop and recreate tables
        db.execSQL(ReservationQueries.DROP_TABLE);
        db.execSQL(FavoriteQueries.DROP_TABLE);
        db.execSQL(PropertyQueries.DROP_TABLE);
        db.execSQL(UserQueries.DROP_TABLE);
        onCreate(db);
    }

    /**
     * Retrieves all properties from the Property table using a readable database.
     *
     * @return Cursor pointing to all properties.
     */
    public Cursor getAllProperties() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(PropertyQueries.GET_ALL_PROPERTIES, null);
    }


    /**
     * Retrieves all users from the User table using a readable database.
     *
     * @return Cursor pointing to all users.
     */
    public Cursor getAllUsers() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(UserQueries.GET_ALL_USERS, null);

    }

    /**
     * Inserts a default admin account with the provided database instance
     * @param db The SQLiteDatabase instance to use
     * @return true if successful, false otherwise
     */
    private boolean insertDefaultAdmin(SQLiteDatabase db) {
        // Check if admin user already exists
        ContentValues values = new ContentValues();
        values.put(UserQueries.COLUMN_EMAIL, "admin@admin.com");
        
        // Hash the admin password
        String plainPassword = "Admin123!";
        String hashedPassword = PasswordUtils.hashPasswordSimple(plainPassword);
        values.put(UserQueries.COLUMN_PASSWORD, hashedPassword);
        
        values.put(UserQueries.COLUMN_FIRST_NAME, "Admin");
        values.put(UserQueries.COLUMN_LAST_NAME, "User"); // Adding required last_name field
        values.put(UserQueries.COLUMN_ROLE, "admin");
        try {
            long result = db.insert(UserQueries.TABLE_NAME, null, values);
            if (result == -1) {
                Log.e("DatabaseHelper", "Failed to insert the default admin account");
                return false;
            } else {
                Log.d("DatabaseHelper", "Default Admin account created successfully with id: " + result);
                return true;
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Failed to insert default admin account: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inserts a new user into the database with hashed password
     * @param user The User object containing user data
     * @return The ID of the inserted user, or -1 if insertion failed
     */
    public long insertUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        
        // Hash the password before storing
        String hashedPassword = PasswordUtils.hashPasswordSimple(user.getPassword());
        if (hashedPassword == null) {
            Log.e("DatabaseHelper", "Failed to hash password");
            return -1;
        }
        
        values.put(UserQueries.COLUMN_EMAIL, user.getEmail());
        values.put(UserQueries.COLUMN_PASSWORD, hashedPassword);
        values.put(UserQueries.COLUMN_FIRST_NAME, user.getFirstName());
        values.put(UserQueries.COLUMN_LAST_NAME, user.getLastName());
        values.put(UserQueries.COLUMN_GENDER, user.getGender());
        values.put(UserQueries.COLUMN_PHONE, user.getPhone());
        values.put(UserQueries.COLUMN_COUNTRY, user.getCountry());
        values.put(UserQueries.COLUMN_CITY, user.getCity());
        values.put(UserQueries.COLUMN_ROLE, user.getRole() != null ? user.getRole() : "customer"); // Default to customer
        values.put(UserQueries.COLUMN_PROFILE_PHOTO, user.getProfilePhoto());
        
        try {
            long result = db.insert(UserQueries.TABLE_NAME, null, values);
            if (result == -1) {
                Log.e("DatabaseHelper", "Failed to insert user: " + user.getEmail());
            } else {
                Log.d("DatabaseHelper", "User created successfully with id: " + result + " for email: " + user.getEmail());
            }
            return result;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error inserting user: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Checks if a user with the given email already exists
     * @param email The email to check
     * @return true if user exists, false otherwise
     */
    public boolean isUserExists(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        
        try {
            cursor = db.rawQuery(UserQueries.GET_USER_BY_EMAIL, new String[]{email});
            return cursor != null && cursor.getCount() > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error checking if user exists: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Retrieves a user by their ID
     * @param userId The user ID to search for
     * @return User object if found, null otherwise
     */
    public User getUserById(long userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        
        try {
            cursor = db.rawQuery(UserQueries.GET_USER_BY_ID, new String[]{String.valueOf(userId)});
            
            if (cursor != null && cursor.moveToFirst()) {
                // Get user data from cursor
                String email = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_EMAIL));
                String firstName = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_FIRST_NAME));
                String lastName = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_LAST_NAME));
                String gender = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_GENDER));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_PHONE));
                String country = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_COUNTRY));
                String city = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_CITY));
                String role = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_ROLE));
                String profilePhoto = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_PROFILE_PHOTO));
                
                return new User(userId, email, null, firstName, lastName, gender, 
                              phone, country, city, role, profilePhoto);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error retrieving user by ID: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * Adds a property to a user's favorites
     * @param userId The ID of the user
     * @param propertyId The ID of the property to add to favorites
     * @return true if successful, false otherwise
     */
    public boolean addToFavorites(long userId, long propertyId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FavoriteQueries.COLUMN_USER_ID, userId);
        values.put(FavoriteQueries.COLUMN_PROPERTY_ID, propertyId);

        try {
            long result = db.insert(FavoriteQueries.TABLE_NAME, null, values);
            return result != -1;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error adding to favorites: " + e.getMessage());
            return false;
        }
    }

    /**
     * Removes a property from a user's favorites
     * @param userId The ID of the user
     * @param propertyId The ID of the property to remove from favorites
     * @return true if successful, false otherwise
     */
    public boolean removeFromFavorites(long userId, long propertyId) {
        SQLiteDatabase db = getWritableDatabase();
        String selection = FavoriteQueries.COLUMN_USER_ID + " = ? AND " +
                          FavoriteQueries.COLUMN_PROPERTY_ID + " = ?";
        String[] selectionArgs = { String.valueOf(userId), String.valueOf(propertyId) };

        try {
            int deletedRows = db.delete(FavoriteQueries.TABLE_NAME, selection, selectionArgs);
            return deletedRows > 0;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error removing from favorites: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves a property by its ID
     * @param propertyId The property ID to search for
     * @return Property object if found, null otherwise
     */
    public Property getPropertyById(long propertyId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        
        try {
            cursor = db.rawQuery(PropertyQueries.GET_PROPERTY_BY_ID, new String[]{String.valueOf(propertyId)});
            
            if (cursor != null && cursor.moveToFirst()) {
                String type = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_TYPE));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_DESCRIPTION));
                int bedrooms = cursor.getInt(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_BEDROOMS));
                int bathrooms = cursor.getInt(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_BATHROOMS));
                float area = cursor.getFloat(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_AREA));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_PRICE));
                String country = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_COUNTRY));
                String city = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_CITY));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_IMAGE_URL));
                boolean isSpecial = cursor.getInt(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_IS_SPECIAL)) == 1;
                double discount = cursor.getDouble(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_DISCOUNT));
                
                return new Property(propertyId, type, title, description, bedrooms, bathrooms,
                                  area, price, country, city, imageUrl, isSpecial, discount);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error retrieving property by ID: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * Retrieves all reservations for a specific user
     * @param userId The user ID to get reservations for
     * @return List of Reservation objects for the user
     */
    public List<Reservation> getUserReservations(long userId) {
        SQLiteDatabase db = getReadableDatabase();
        List<Reservation> reservations = new ArrayList<>();
        Cursor cursor = null;
        
        try {
            cursor = db.rawQuery(ReservationQueries.GET_USER_RESERVATIONS, new String[]{String.valueOf(userId)});
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    long reservationId = cursor.getLong(cursor.getColumnIndexOrThrow(ReservationQueries.COLUMN_RESERVATION_ID));
                    long propertyId = cursor.getLong(cursor.getColumnIndexOrThrow(ReservationQueries.COLUMN_PROPERTY_ID));
                    String dateString = cursor.getString(cursor.getColumnIndexOrThrow(ReservationQueries.COLUMN_RESERVATION_DATE));
                    String status = cursor.getString(cursor.getColumnIndexOrThrow(ReservationQueries.COLUMN_STATUS));
                    
                    // Parse the date - assuming it's stored as ISO string
                    LocalDateTime reservationDate = LocalDateTime.parse(dateString);
                    
                    reservations.add(new Reservation(reservationId, userId, propertyId, reservationDate, status));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error retrieving user reservations: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return reservations;
    }

    /**
     * Retrieves all favorite properties for a specific user
     * @param userId The user ID to get favorite properties for
     * @return List of Property objects that are favorites for the user
     */
    public List<Property> getUserFavoriteProperties(long userId) {
        SQLiteDatabase db = getReadableDatabase();
        List<Property> favoriteProperties = new ArrayList<>();
        Cursor cursor = null;
        
        try {
            cursor = db.rawQuery(FavoriteQueries.GET_USER_FAVORITE_PROPERTIES, new String[]{String.valueOf(userId)});
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    long propertyId = cursor.getLong(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_PROPERTY_ID));
                    String type = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_TYPE));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_TITLE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_DESCRIPTION));
                    int bedrooms = cursor.getInt(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_BEDROOMS));
                    int bathrooms = cursor.getInt(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_BATHROOMS));
                    float area = cursor.getFloat(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_AREA));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_PRICE));
                    String country = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_COUNTRY));
                    String city = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_CITY));
                    String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_IMAGE_URL));
                    boolean isSpecial = cursor.getInt(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_IS_SPECIAL)) == 1;
                    double discount = cursor.getDouble(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_DISCOUNT));
                    
                    favoriteProperties.add(new Property(propertyId, type, title, description, bedrooms, bathrooms,
                                                      area, price, country, city, imageUrl, isSpecial, discount));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error retrieving user favorite properties: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return favoriteProperties;
    }

    /**
     * Retrieves all properties that have special offers (featured properties)
     * @return List of Property objects that are marked as special
     */
    public List<Property> getFeaturedProperties() {
        SQLiteDatabase db = getReadableDatabase();
        List<Property> featuredProperties = new ArrayList<>();
        Cursor cursor = null;
        
        try {
            // Query for properties where isSpecial = 1
            String query = "SELECT * FROM " + PropertyQueries.TABLE_NAME + 
                          " WHERE " + PropertyQueries.COLUMN_IS_SPECIAL + " = 1" +
                          " ORDER BY " + PropertyQueries.COLUMN_DISCOUNT + " DESC";
            cursor = db.rawQuery(query, null);
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    long propertyId = cursor.getLong(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_PROPERTY_ID));
                    String type = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_TYPE));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_TITLE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_DESCRIPTION));
                    int bedrooms = cursor.getInt(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_BEDROOMS));
                    int bathrooms = cursor.getInt(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_BATHROOMS));
                    float area = cursor.getFloat(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_AREA));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_PRICE));
                    String country = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_COUNTRY));
                    String city = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_CITY));
                    String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_IMAGE_URL));
                    boolean isSpecial = cursor.getInt(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_IS_SPECIAL)) == 1;
                    double discount = cursor.getDouble(cursor.getColumnIndexOrThrow(PropertyQueries.COLUMN_DISCOUNT));
                    
                    featuredProperties.add(new Property(propertyId, type, title, description, bedrooms, bathrooms,
                                                       area, price, country, city, imageUrl, isSpecial, discount));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error retrieving featured properties: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return featuredProperties;
    }
    
    /**
     * Deletes a user from the database
     * Note: This will also cascade delete related favorites and reservations due to foreign key constraints
     * @param userId The ID of the user to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteUser(long userId) {
        SQLiteDatabase db = getWritableDatabase();
        
        try {
            // First, delete related favorites and reservations to avoid foreign key constraint issues
            String favoritesSelection = FavoriteQueries.COLUMN_USER_ID + " = ?";
            String reservationsSelection = ReservationQueries.COLUMN_USER_ID + " = ?";
            String[] selectionArgs = { String.valueOf(userId) };
            
            // Delete user's favorites
            db.delete(FavoriteQueries.TABLE_NAME, favoritesSelection, selectionArgs);
            
            // Delete user's reservations
            db.delete(ReservationQueries.TABLE_NAME, reservationsSelection, selectionArgs);
            
            // Now delete the user
            int deletedRows = db.delete(UserQueries.TABLE_NAME, 
                                      UserQueries.COLUMN_USER_ID + " = ?", 
                                      selectionArgs);
            
            boolean success = deletedRows > 0;
            if (success) {
                Log.d("DatabaseHelper", "User deleted successfully with ID: " + userId);
            } else {
                Log.e("DatabaseHelper", "Failed to delete user with ID: " + userId);
            }
            return success;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error deleting user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Retrieves all customers (users with role 'customer') from the database
     * @return List of User objects that are customers
     */
    public List<User> getAllCustomers() {
        SQLiteDatabase db = getReadableDatabase();
        List<User> customers = new ArrayList<>();
        Cursor cursor = null;
        
        try {
            String query = "SELECT * FROM " + UserQueries.TABLE_NAME + 
                          " WHERE " + UserQueries.COLUMN_ROLE + " = ?";
            cursor = db.rawQuery(query, new String[]{"customer"});
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    long userId = cursor.getLong(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_USER_ID));
                    String email = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_EMAIL));
                    String firstName = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_FIRST_NAME));
                    String lastName = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_LAST_NAME));
                    String gender = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_GENDER));
                    String phone = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_PHONE));
                    String country = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_COUNTRY));
                    String city = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_CITY));
                    String role = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_ROLE));
                    String profilePhoto = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_PROFILE_PHOTO));
                    
                    customers.add(new User(userId, email, null, firstName, lastName, gender,
                                         phone, country, city, role, profilePhoto));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error retrieving customers: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return customers;
    }

    /**
     * Retrieves all admins (users with role 'admin') from the database
     * @return List of User objects that are admins
     */
    public List<User> getAllAdmins() {
        SQLiteDatabase db = getReadableDatabase();
        List<User> admins = new ArrayList<>();
        Cursor cursor = null;
        
        try {
            String query = "SELECT * FROM " + UserQueries.TABLE_NAME + 
                          " WHERE " + UserQueries.COLUMN_ROLE + " = ?";
            cursor = db.rawQuery(query, new String[]{"admin"});
            
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    long userId = cursor.getLong(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_USER_ID));
                    String email = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_EMAIL));
                    String firstName = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_FIRST_NAME));
                    String lastName = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_LAST_NAME));
                    String gender = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_GENDER));
                    String phone = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_PHONE));
                    String country = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_COUNTRY));
                    String city = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_CITY));
                    String role = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_ROLE));
                    String profilePhoto = cursor.getString(cursor.getColumnIndexOrThrow(UserQueries.COLUMN_PROFILE_PHOTO));
                    
                    admins.add(new User(userId, email, null, firstName, lastName, gender,
                                      phone, country, city, role, profilePhoto));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error retrieving admins: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return admins;
    }
}
