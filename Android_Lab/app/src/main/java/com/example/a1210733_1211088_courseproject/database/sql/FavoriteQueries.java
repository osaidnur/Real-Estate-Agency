package com.example.a1210733_1211088_courseproject.database.sql;

/**
 * Contains all SQL queries related to the Favorite table.
 * This separates SQL queries from database logic for better maintenance.
 */
public class FavoriteQueries {
    // Table name
    public static final String TABLE_NAME = "Favorite";

    // Column names
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_PROPERTY_ID = "property_id";
    public static final String COLUMN_ADDED_AT = "added_at";

    // Create table query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_USER_ID + " INTEGER NOT NULL," +
            COLUMN_PROPERTY_ID + " INTEGER NOT NULL," +
            COLUMN_ADDED_AT + " DATE," +
            " PRIMARY KEY (" + COLUMN_USER_ID + ", " + COLUMN_PROPERTY_ID + ")," +
            " FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + UserQueries.TABLE_NAME + "(" + UserQueries.COLUMN_USER_ID + ") ON DELETE CASCADE," +
            " FOREIGN KEY(" + COLUMN_PROPERTY_ID + ") REFERENCES " + PropertyQueries.TABLE_NAME + "(" + PropertyQueries.COLUMN_PROPERTY_ID + ") ON DELETE CASCADE" +
            ");";

    // Drop table query
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    // Common queries
    public static final String GET_ALL_FAVORITES = "SELECT * FROM " + TABLE_NAME;

    public static final String GET_USER_FAVORITES =
            "SELECT * FROM " + TABLE_NAME +
            " WHERE " + COLUMN_USER_ID + " = ?";

    public static final String GET_PROPERTY_FAVORITES =
            "SELECT * FROM " + TABLE_NAME +
            " WHERE " + COLUMN_PROPERTY_ID + " = ?";

    public static final String CHECK_IS_FAVORITE =
            "SELECT COUNT(*) FROM " + TABLE_NAME +
            " WHERE " + COLUMN_USER_ID + " = ? AND " +
            COLUMN_PROPERTY_ID + " = ?";

    public static final String DELETE_FAVORITE =
            "DELETE FROM " + TABLE_NAME +
            " WHERE " + COLUMN_USER_ID + " = ? AND " +
            COLUMN_PROPERTY_ID + " = ?";

    // Join queries to get favorite properties with details
    public static final String GET_USER_FAVORITE_PROPERTIES =
            "SELECT p.* FROM " + PropertyQueries.TABLE_NAME + " p " +
            "JOIN " + TABLE_NAME + " f ON p." + PropertyQueries.COLUMN_PROPERTY_ID + " = f." + COLUMN_PROPERTY_ID + " " +
            "WHERE f." + COLUMN_USER_ID + " = ?";
}
