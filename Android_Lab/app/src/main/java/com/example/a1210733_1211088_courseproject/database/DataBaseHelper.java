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
import com.example.a1210733_1211088_courseproject.utils.PasswordUtils;

public class DataBaseHelper extends SQLiteOpenHelper {

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }    @Override
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
     */    private boolean insertDefaultAdmin(SQLiteDatabase db) {
        // Check if admin user already exists
        ContentValues values = new ContentValues();
        values.put(UserQueries.COLUMN_EMAIL, "admin@admin.com");
        
        // Hash the admin password
        String plainPassword = "Admin123!";
        String hashedPassword = PasswordUtils.hashPasswordSimple(plainPassword);
        values.put(UserQueries.COLUMN_PASSWORD, hashedPassword);
        
        values.put(UserQueries.COLUMN_FIRST_NAME, "Admin");
        values.put(UserQueries.COLUMN_LAST_NAME, "User"); // Adding required last_name field
        values.put(UserQueries.COLUMN_ROLE, "admin");        try {
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
    }    /**
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

}
