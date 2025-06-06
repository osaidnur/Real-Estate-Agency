package com.example.a1210733_1211088_courseproject.database.sql;

/**
 * Contains all SQL queries related to the Reservation table.
 * This separates SQL queries from database logic for better maintenance.
 */
public class ReservationQueries {
    // Table name
    public static final String TABLE_NAME = "Reservation";

    // Column names
    public static final String COLUMN_RESERVATION_ID = "reservation_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_PROPERTY_ID = "property_id";
    public static final String COLUMN_RESERVATION_DATE = "reservation_date";
    public static final String COLUMN_STATUS = "status";

    // Create table query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_RESERVATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_USER_ID + " INTEGER NOT NULL," +
            COLUMN_PROPERTY_ID + " INTEGER NOT NULL UNIQUE," + // enforce one reservation per property
            COLUMN_RESERVATION_DATE + " DATE NOT NULL," +
            COLUMN_STATUS + " TEXT NOT NULL," +
            " FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + UserQueries.TABLE_NAME + "(" + UserQueries.COLUMN_USER_ID + ") ON DELETE CASCADE," +
            " FOREIGN KEY(" + COLUMN_PROPERTY_ID + ") REFERENCES " + PropertyQueries.TABLE_NAME + "(" + PropertyQueries.COLUMN_PROPERTY_ID + ") ON DELETE CASCADE" +
            ");";

    // Drop table query
    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    // Common queries
    public static final String GET_ALL_RESERVATIONS = "SELECT * FROM " + TABLE_NAME;

    public static final String GET_RESERVATION_BY_ID =
            "SELECT * FROM " + TABLE_NAME +
            " WHERE " + COLUMN_RESERVATION_ID + " = ?";

    public static final String GET_USER_RESERVATIONS =
            "SELECT * FROM " + TABLE_NAME +
            " WHERE " + COLUMN_USER_ID + " = ?";

    public static final String GET_PROPERTY_RESERVATION =
            "SELECT * FROM " + TABLE_NAME +
            " WHERE " + COLUMN_PROPERTY_ID + " = ?";

    public static final String UPDATE_RESERVATION_STATUS =
            "UPDATE " + TABLE_NAME +
            " SET " + COLUMN_STATUS + " = ? " +
            " WHERE " + COLUMN_RESERVATION_ID + " = ?";

    public static final String DELETE_RESERVATION =
            "DELETE FROM " + TABLE_NAME +
            " WHERE " + COLUMN_RESERVATION_ID + " = ?";

    // Join queries to get reservation details with property and user information
    public static final String GET_USER_RESERVATIONS_WITH_DETAILS =
            "SELECT r.*, p.title, p.image_url, p.price FROM " + TABLE_NAME + " r " +
            "JOIN " + PropertyQueries.TABLE_NAME + " p ON r." + COLUMN_PROPERTY_ID + " = p." + PropertyQueries.COLUMN_PROPERTY_ID + " " +
            "WHERE r." + COLUMN_USER_ID + " = ?";
}
