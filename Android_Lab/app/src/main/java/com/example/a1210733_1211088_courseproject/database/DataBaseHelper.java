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
        ContentValues values = new ContentValues();
        values.put(UserQueries.COLUMN_EMAIL, "admin@admin.com");
        values.put(UserQueries.COLUMN_PASSWORD, "Admin123!");
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

}
