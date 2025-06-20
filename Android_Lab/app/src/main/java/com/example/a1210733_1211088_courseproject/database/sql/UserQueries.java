package com.example.a1210733_1211088_courseproject.database.sql;

/**
 * Contains all SQL queries related to the User table.
 * This separates SQL queries from database logic for better maintenance.
 */
public class UserQueries {
    // Table name
    public static final String TABLE_NAME = "User";

    // Column names
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_FIRST_NAME = "first_name";
    public static final String COLUMN_LAST_NAME = "last_name";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_COUNTRY = "country";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_ROLE = "role";
    public static final String COLUMN_PROFILE_PHOTO = "profile_photo";

    // Create table query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_EMAIL + " TEXT UNIQUE NOT NULL," +
            COLUMN_PASSWORD + " TEXT NOT NULL," +
            COLUMN_FIRST_NAME + " TEXT NOT NULL," +
            COLUMN_LAST_NAME + " TEXT NOT NULL," +
            COLUMN_GENDER + " TEXT," +
            COLUMN_PHONE + " TEXT," +
            COLUMN_COUNTRY + " TEXT," +
            COLUMN_CITY + " TEXT," +
            COLUMN_ROLE + " TEXT NOT NULL," +
            COLUMN_PROFILE_PHOTO + " TEXT" +
            ");";

    // Drop table query
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    // Common queries
    public static final String GET_ALL_USERS = "SELECT * FROM " + TABLE_NAME;

    public static final String GET_USER_BY_ID =
            "SELECT * FROM " + TABLE_NAME +
            " WHERE " + COLUMN_USER_ID + " = ?";

    public static final String GET_USER_BY_EMAIL =
            "SELECT * FROM " + TABLE_NAME +
            " WHERE " + COLUMN_EMAIL + " = ?";

    public static final String DELETE_USER =
            "DELETE FROM " + TABLE_NAME +
            " WHERE " + COLUMN_USER_ID + " = ?";    // Insert default admin account query
    public static final String INSERT_ADMIN_ACCOUNT =
            "INSERT INTO " + TABLE_NAME + " (" +
            COLUMN_EMAIL + ", " +
            COLUMN_PASSWORD + ", " +
            COLUMN_FIRST_NAME + ", " +
            COLUMN_LAST_NAME + ", " +
            COLUMN_ROLE +
            ") VALUES ('admin@admin.com', 'Admin123!', 'Admin', 'User', 'admin');";

    // Insert user query (for new user registration)
    public static final String INSERT_USER =
            "INSERT INTO " + TABLE_NAME + " (" +
            COLUMN_EMAIL + ", " +
            COLUMN_PASSWORD + ", " +
            COLUMN_FIRST_NAME + ", " +
            COLUMN_LAST_NAME + ", " +
            COLUMN_GENDER + ", " +
            COLUMN_PHONE + ", " +
            COLUMN_COUNTRY + ", " +
            COLUMN_CITY + ", " +
            COLUMN_ROLE + ", " +
            COLUMN_PROFILE_PHOTO +
            ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    // Update user profile query (excluding password)
    public static final String UPDATE_USER_PROFILE =
            "UPDATE " + TABLE_NAME + " SET " +
            COLUMN_FIRST_NAME + " = ?, " +
            COLUMN_LAST_NAME + " = ?, " +
            COLUMN_PHONE + " = ?, " +
            COLUMN_PROFILE_PHOTO + " = ? " +
            "WHERE " + COLUMN_USER_ID + " = ?";

    // Update user password query
    public static final String UPDATE_USER_PASSWORD =
            "UPDATE " + TABLE_NAME + " SET " +
            COLUMN_PASSWORD + " = ? " +
            "WHERE " + COLUMN_USER_ID + " = ?";
}
